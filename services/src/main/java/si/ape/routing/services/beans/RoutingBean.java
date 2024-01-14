package si.ape.routing.services.beans;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.json.JSONObject;
import si.ape.routing.lib.Branch;
import si.ape.routing.lib.Street;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

import si.ape.routing.models.converters.BranchConverter;
import si.ape.routing.models.entities.BranchEntity;

import si.ape.routing.lib.data.Pair;
import si.ape.routing.lib.data.Reason;

/**
 * The RoutingBean class is a stateless session bean that provides methods for manipulating the database. It provides
 * a method for calculating the next hop towards the specified destination, etc. and thus handles all the business
 * logic of the microservice.
 */
@ApplicationScoped
public class RoutingBean {

    /**
     * The routing bean's logger.
     */
    private Logger log = Logger.getLogger(RoutingBean.class.getName());

    /**
     * The entity manager.
     */
    @Inject
    private EntityManager em;

    /**
     * The graph that represents the network of branches.
     */
    private SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph = null;

    /**
     * The maximum distance between two branches for them to be considered connected.
     */
    private final double HAVERSINE_MAX_DISTANCE = 200; // km

    /**
     * Initializes the bean by generating the graph.
     */
    @PostConstruct
    public void init() {
        log.info("RoutingBean created.");
        if (graph == null) {
            generateGraph();
        }
        log.info("Graph generated.");
    }

    /**
     * Gets the next hop between the source and destination.
     *
     * @param source      The source street.
     * @param destination The destination street.
     * @return The next hop between the source and destination.
     */
    public Pair<Branch, Reason> nextHop(Street source, Street destination) {
        Branch sourceBranch = getClosestBranch(source);
        Branch destinationBranch = getClosestBranch(destination);

        if (sourceBranch == null || destinationBranch == null) {
            if (sourceBranch == null) {
                log.severe("Source branch is null.");
            }
            if (destinationBranch == null) {
                log.severe("Destination branch is null.");
            }
            return new Pair<>(null, Reason.INTERNAL_ERROR);
        }

        if (sourceBranch.getId().equals(destinationBranch.getId())) {
            return new Pair<>(sourceBranch, Reason.ALREADY_AT_DESTINATION);
        }

        // If the checks above pass, we can use the graph to find the shortest path between the source and destination.
        //List<DefaultWeightedEdge> path = DijkstraShortestPath.findPathBetween(graph, sourceBranch.getId(), destinationBranch.getId()).getEdgeList();
        GraphPath<Integer, DefaultWeightedEdge> graphPath = DijkstraShortestPath.findPathBetween(graph, sourceBranch.getId(), destinationBranch.getId());
        if (graphPath == null) {
            log.info("Graph path is null, the algorithm failed to find a path.");
            return new Pair<>(null, Reason.NO_PATH_FOUND);
        }
        List<DefaultWeightedEdge> path = graphPath.getEdgeList();

        // If the path is null, that means there is no path between the source and destination.
        if (path == null || path.isEmpty()) {
            log.info("Path is null or empty, the algorithm failed to find a path.");
            return new Pair<>(null, Reason.NO_PATH_FOUND);
        }

        // Parse the path to find the next hop.
        int nextHopId = graph.getEdgeTarget(path.get(0));
        /*System.out.println("PATH SIZE:" + path.size());
        if (path.size() > 1) {
            for (int i = 1; i < path.size(); i++) {
                System.out.println("PATH " + i + ": " + graph.getEdgeTarget(path.get(i)));
            }
        }*/

        // If we are here, we passed the check for final parcel center. If the returned street here is the same as the
        // source street, get the next hop in branch where possible.
        Branch nextHop = BranchConverter.toDto(em.find(BranchEntity.class, nextHopId));
        if (nextHop.getStreet().equals(source)) {
            int i = 0;
            do {
                if (i < path.size()) {
                    nextHopId = graph.getEdgeTarget(path.get(i));
                    nextHop = BranchConverter.toDto(em.find(BranchEntity.class, nextHopId));
                    i++;
                } else {
                    return new Pair<>(null, Reason.NO_PATH_FOUND);
                }
            } while (nextHop.getStreet().equals(source));
        }

        return new Pair<>(BranchConverter.toDto(em.find(BranchEntity.class, nextHopId)), Reason.PATH_FOUND);
    }

    /**
     * Gets the closest branch to the provided street.
     *
     * @param street The street.
     * @return The closest branch to the provided street.
     */
    private Branch getClosestBranch(Street street) {
        TypedQuery<BranchEntity> branchesQuery = em.createNamedQuery("BranchEntity.getAllWithType", BranchEntity.class);
        branchesQuery.setParameter("branchTypeId", 1);
        List<BranchEntity> branchEntities = branchesQuery.getResultList();
        List<Branch> branches = branchEntities.stream().map(BranchConverter::toDto).toList();

        // If the provided street is not the address of a branch, that means the package is being sent from a
        // customer's address. In that case, we will have to find the closest branch to the customer's address, with the
        // stipulation that the branch must be in the same country.
        if (branches.stream().noneMatch(branch -> branch.getStreet().equals(street))) {
            List<Branch> branchesInSameCountry = branches.stream()
                    .filter(branch -> branch.getStreet().getCity().getCountry().getCode().equals(street.getCity().getCountry().getCode()))
                    .toList();

            // Combine the branches into a single Google API request to save time and credits.
            double sourceLatitude = street.getCity().getLatitude();
            double sourceLongitude = street.getCity().getLongitude();
            StringBuilder origins = new StringBuilder().append(sourceLatitude).append(",").append(sourceLongitude);
            StringBuilder destinations = new StringBuilder();
            for (int i = 0; i < branchesInSameCountry.size(); i++) {
                Branch branch = branchesInSameCountry.get(i);
                double destinationLatitude = branch.getStreet().getCity().getLatitude();
                double destinationLongitude = branch.getStreet().getCity().getLongitude();
                destinations.append(destinationLatitude).append(",").append(destinationLongitude);
                if (i != branchesInSameCountry.size() - 1) {
                    destinations.append("|");
                }
            }

            HashMap<Integer, Double> distances = new HashMap<>();
            if (branchesInSameCountry.size() < 25) {
                // Make small request for same country.
                distances = makeSmallRequestForCountriesInSameCountry(origins.toString(), destinations.toString(), branchesInSameCountry);
            } else {
                // Make batched request for same country.
                distances = makeBatchedRequestForCountriesInSameCountry(origins.toString(), destinations.toString(), branchesInSameCountry);
            }

            // Parse the resulting distances and find the closest branch.
            double minDistance = Double.MAX_VALUE;
            Branch closestBranch = null;
            for (Branch branch : branchesInSameCountry) {
                double distance = distances.get(branch.getId());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestBranch = branch;
                }
            }
            return closestBranch;
        } else {
            // If the provided street is the address of a branch, just return that branch.
            return branches.stream().filter(branch -> branch.getStreet().equals(street)).findFirst().orElse(null);
        }
    }

    /**
     * Generates the graph that represents the network of branches.
     */
    private void generateGraph() {
        graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        //List<BranchEntity> branchEntities = em.createNamedQuery("BranchEntity.getAll", BranchEntity.class).getResultList();
        TypedQuery<BranchEntity> branchesQuery = em.createNamedQuery("BranchEntity.getAllWithType", BranchEntity.class);
        branchesQuery.setParameter("branchTypeId", 1);
        List<BranchEntity> branchEntities = branchesQuery.getResultList();
        List<Branch> branches = branchEntities.stream().map(BranchConverter::toDto).toList();
        branches.forEach(branch -> graph.addVertex(branch.getId()));

        for (int i = 0; i < branches.size(); i++) {
            ArrayList<Branch> connectedBranches = getConnectedBranches(i, branches);
            double sourceLatitude = branches.get(i).getStreet().getCity().getLatitude();
            double sourceLongitude = branches.get(i).getStreet().getCity().getLongitude();
            StringBuilder origins = new StringBuilder().append(sourceLatitude).append(",").append(sourceLongitude);
            StringBuilder destinations = new StringBuilder();
            if (connectedBranches.size() < 25) {
                makeSmallRequest(i, origins, destinations, branches, connectedBranches);
            } else {
                // If the branch has more than 25 connected branches, we will have to make multiple requests to the
                // Google Maps API.
                makeBatchedRequest(i, origins, destinations, branches, connectedBranches);
            }
        }
    }

    /**
     * Gets the branches that are connected to the branch at the specified index.
     *
     * @param branchIndex The index of the branch.
     * @param branches    The list of branches.
     * @return The branches that are connected to the branch at the specified index.
     */
    private ArrayList<Branch> getConnectedBranches(int branchIndex, List<Branch> branches) {
        ArrayList<Branch> connectedBranches = new ArrayList<>();
        double sourceLatitude = branches.get(branchIndex).getStreet().getCity().getLatitude();
        double sourceLongitude = branches.get(branchIndex).getStreet().getCity().getLongitude();
        for (int i = 0; i < branches.size(); i++) {
            if (branchIndex != i) {
                double destinationLatitude = branches.get(i).getStreet().getCity().getLatitude();
                double destinationLongitude = branches.get(i).getStreet().getCity().getLongitude();
                double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                if (distance < HAVERSINE_MAX_DISTANCE) {
                    connectedBranches.add(branches.get(i));
                }
            }
        }
        return connectedBranches;
    }

    /**
     * Makes a request to the Google Maps API to get the distances between the source branch and the connected branches.
     * This method is used when the source branch has less than 25 connected branches.
     *
     * @param branchIndex       The index of the source branch.
     * @param origins           The StringBuilder that contains the origins.
     * @param destinations      The StringBuilder that contains the destinations.
     * @param branches          The list of branches.
     * @param connectedBranches The list of connected branches.
     */
    private void makeSmallRequest(int branchIndex, StringBuilder origins, StringBuilder destinations, List<Branch> branches, ArrayList<Branch> connectedBranches) {
        Branch sourceBranch = branches.get(branchIndex);
        for (int i = 0; i < connectedBranches.size(); i++) {
            Branch destinationBranch = connectedBranches.get(i);
            double destinationLatitude = destinationBranch.getStreet().getCity().getLatitude();
            double destinationLongitude = destinationBranch.getStreet().getCity().getLongitude();
            destinations.append(destinationLatitude).append(",").append(destinationLongitude);
            if (i != connectedBranches.size() - 1) {
                destinations.append("|");
            }
        }

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String apiKey = "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE";
        Client client = ClientBuilder.newClient();
        Response response = client.target(apiUrl)
                .queryParam("origins", origins)
                .queryParam("destinations", destinations)
                .queryParam("units", "metric")
                .queryParam("key", apiKey)
                .request()
                .get();

        if (response.getStatus() == 200) {
            JSONObject json = new JSONObject(response.readEntity(String.class));
            for (int i = 0; i < connectedBranches.size(); i++) {
                Branch destinationBranch = connectedBranches.get(i);
                // Handle empty JSONArrays.
                if (!json.getJSONArray("rows").isEmpty() && !json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").isEmpty()) {
                    if (json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(i).has("distance")) {
                        double distance = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(i).getJSONObject("distance").getDouble("value");
                        DefaultWeightedEdge edge = graph.addEdge(sourceBranch.getId(), destinationBranch.getId());
                        if (edge != null) {
                            graph.setEdgeWeight(edge, distance);
                        }
                    } else {
                        // If the distance is not found, we will have to use the Haversine formula.
                        double sourceLatitude = sourceBranch.getStreet().getCity().getLatitude();
                        double sourceLongitude = sourceBranch.getStreet().getCity().getLongitude();
                        double destinationLatitude = destinationBranch.getStreet().getCity().getLatitude();
                        double destinationLongitude = destinationBranch.getStreet().getCity().getLongitude();
                        double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                        DefaultWeightedEdge edge = graph.addEdge(sourceBranch.getId(), destinationBranch.getId());
                        if (edge != null) {
                            graph.setEdgeWeight(edge, distance);
                        }
                    }
                }
            }
        }
    }

    /**
     * Makes multiple requests to the Google Maps API to get the distances between the source branch and the connected
     * branches. This method is used when the source branch has more than 25 connected branches.
     *
     * @param branchIndex       The index of the source branch.
     * @param origins           The StringBuilder that contains the origins.
     * @param destinations      The StringBuilder that contains the destinations.
     * @param branches          The list of branches.
     * @param connectedBranches The list of connected branches.
     */
    private void makeBatchedRequest(int branchIndex, StringBuilder origins, StringBuilder destinations, List<Branch> branches, ArrayList<Branch> connectedBranches) {
        Branch sourceBranch = branches.get(branchIndex);
        final int batchSize = 25;
        final int numberOfBatches = (int) Math.ceil((double) connectedBranches.size() / batchSize);
        for (int i = 0; i < numberOfBatches; i++) {
            int startIndex = i * batchSize;
            int endIndex = Math.min((i + 1) * batchSize, connectedBranches.size());
            for (int j = startIndex; j < endIndex; j++) {
                Branch destinationBranch = connectedBranches.get(j);
                double destinationLatitude = destinationBranch.getStreet().getCity().getLatitude();
                double destinationLongitude = destinationBranch.getStreet().getCity().getLongitude();
                destinations.append(destinationLatitude).append(",").append(destinationLongitude);
                if (j != endIndex - 1) {
                    destinations.append("|");
                }
            }

            String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
            String apiKey = "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE";
            Client client = ClientBuilder.newClient();
            Response response = client.target(apiUrl)
                    .queryParam("origins", origins)
                    .queryParam("destinations", destinations)
                    .queryParam("units", "metric")
                    .queryParam("key", apiKey)
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                JSONObject json = new JSONObject(response.readEntity(String.class));
                for (int j = startIndex; j < endIndex; j++) {
                    Branch destinationBranch = connectedBranches.get(j);
                    // Handle empty JSONArrays.
                    if (!json.getJSONArray("rows").isEmpty() && !json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").isEmpty()) {
                        if (json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(j - startIndex).has("distance")) {
                            double distance = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(j - startIndex).getJSONObject("distance").getDouble("value");
                            DefaultWeightedEdge edge = graph.addEdge(sourceBranch.getId(), destinationBranch.getId());
                            if (edge != null) {
                                graph.setEdgeWeight(edge, distance);
                            }
                        } else {
                            // If the distance is not found, we will have to use the Haversine formula.
                            double sourceLatitude = sourceBranch.getStreet().getCity().getLatitude();
                            double sourceLongitude = sourceBranch.getStreet().getCity().getLongitude();
                            double destinationLatitude = destinationBranch.getStreet().getCity().getLatitude();
                            double destinationLongitude = destinationBranch.getStreet().getCity().getLongitude();
                            double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                            DefaultWeightedEdge edge = graph.addEdge(sourceBranch.getId(), destinationBranch.getId());
                            if (edge != null) {
                                graph.setEdgeWeight(edge, distance);
                            }
                        }
                    }
                }
            }
            // Reset the destinations StringBuilder.
            destinations = new StringBuilder();
        }
    }

    /**
     * Makes a request to the Google Maps API to get the distances between the source branch and the branches in the
     * same country. This method is used when the source branch has less than 25 branches in the same country.
     *
     * @param origins                 The StringBuilder that contains the origins.
     * @param destinations            The StringBuilder that contains the destinations.
     * @param branchesInSameCountry   The list of branches in the same country.
     * @return The distances between the source branch and the branches in the same country.
     */
    private HashMap<Integer, Double> makeSmallRequestForCountriesInSameCountry(String origins, String destinations, List<Branch> branchesInSameCountry) {

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String apiKey = "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE";
        Client client = ClientBuilder.newClient();
        Response response = client.target(apiUrl)
                .queryParam("origins", origins)
                .queryParam("destinations", destinations)
                .queryParam("units", "metric")
                .queryParam("key", apiKey)
                .request()
                .get();

        HashMap<Integer, Double> distances = new HashMap<>();

        if (response.getStatus() == 200) {
            JSONObject json = new JSONObject(response.readEntity(String.class));

            for (int i = 0; i < branchesInSameCountry.size(); i++) {
                Branch branch = branchesInSameCountry.get(i);
                // Handle empty JSONArrays.
                if (!json.getJSONArray("rows").isEmpty() && !json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").isEmpty()) {
                    if (json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(i).has("distance")) {
                        double distance = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(i).getJSONObject("distance").getDouble("value");
                        distances.put(branch.getId(), distance);
                    } else {
                        // If the distance is not found, we will have to use the Haversine formula.
                        double sourceLatitude = branch.getStreet().getCity().getLatitude();
                        double sourceLongitude = branch.getStreet().getCity().getLongitude();
                        double destinationLatitude = branch.getStreet().getCity().getLatitude();
                        double destinationLongitude = branch.getStreet().getCity().getLongitude();
                        double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                        distances.put(branch.getId(), distance);
                    }
                } else {
                    // If the Google API request fails, we will have to find the closest branch to the customer's address
                    // using the Haversine formula.
                    double sourceLatitude = branch.getStreet().getCity().getLatitude();
                    double sourceLongitude = branch.getStreet().getCity().getLongitude();
                    for (Branch b : branchesInSameCountry) {
                        double destinationLatitude = b.getStreet().getCity().getLatitude();
                        double destinationLongitude = b.getStreet().getCity().getLongitude();
                        double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                        distances.put(b.getId(), distance);
                    }
                }
            }
        }

        return distances;
    }

    /**
     * Makes multiple requests to the Google Maps API to get the distances between the source branch and the branches in
     * the same country. This method is used when the source branch has more than 25 branches in the same country.
     *
     * @param origins                 The StringBuilder that contains the origins.
     * @param destinations            The StringBuilder that contains the destinations.
     * @param branchesInSameCountry   The list of branches in the same country.
     * @return The distances between the source branch and the branches in the same country.
     */
    private HashMap<Integer, Double> makeBatchedRequestForCountriesInSameCountry(String origins, String destinations, List<Branch> branchesInSameCountry) {

        HashMap<Integer, Double> distances = new HashMap<>();

        final int batchSize = 25;
        final int numberOfBatches = (int) Math.ceil((double) branchesInSameCountry.size() / batchSize);
        for (int i = 0; i < numberOfBatches; i++) {
            int startIndex = i * batchSize;
            int endIndex = Math.min((i + 1) * batchSize, branchesInSameCountry.size());
            for (int j = startIndex; j < endIndex; j++) {
                Branch branch = branchesInSameCountry.get(j);
                double destinationLatitude = branch.getStreet().getCity().getLatitude();
                double destinationLongitude = branch.getStreet().getCity().getLongitude();
                destinations += destinationLatitude + "," + destinationLongitude;
                if (j != endIndex - 1) {
                    destinations += "|";
                }
            }

            String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
            String apiKey = "AIzaSyB-5Z6Z6Z6Z6Z6Z6Z6Z6Z6Z6Z6Z6Z6Z6Z6";
            Client client = ClientBuilder.newClient();
            Response response = client.target(apiUrl)
                    .queryParam("origins", origins)
                    .queryParam("destinations", destinations)
                    .queryParam("units", "metric")
                    .queryParam("key", apiKey)
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                JSONObject json = new JSONObject(response.readEntity(String.class));

                for (int j = startIndex; j < endIndex; j++) {
                    Branch branch = branchesInSameCountry.get(j);
                    // Handle empty JSONArrays.
                    if (!json.getJSONArray("rows").isEmpty() && !json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").isEmpty()) {
                        if (json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(j - startIndex).has("distance")) {
                            double distance = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(j - startIndex).getJSONObject("distance").getDouble("value");
                            distances.put(branch.getId(), distance);
                        } else {
                            // If the distance is not found, we will have to use the Haversine formula.
                            double sourceLatitude = branch.getStreet().getCity().getLatitude();
                            double sourceLongitude = branch.getStreet().getCity().getLongitude();
                            double destinationLatitude = branch.getStreet().getCity().getLatitude();
                            double destinationLongitude = branch.getStreet().getCity().getLongitude();
                            double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                            distances.put(branch.getId(), distance);
                        }
                    } else {
                        // If the Google API request fails, we will have to find the closest branch to the customer's address
                        // using the Haversine formula.
                        double sourceLatitude = branch.getStreet().getCity().getLatitude();
                        double sourceLongitude = branch.getStreet().getCity().getLongitude();
                        for (Branch b : branchesInSameCountry) {
                            double destinationLatitude = b.getStreet().getCity().getLatitude();
                            double destinationLongitude = b.getStreet().getCity().getLongitude();
                            double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                            distances.put(b.getId(), distance);
                        }
                    }
                }
            }
            // Reset the destinations StringBuilder.
            destinations = "";
        }
        return distances;
    }

    /**
     * Calculates the distance between two points on Earth using the Haversine formula.
     *
     * @param sourceLatitude        The latitude of the source point.
     * @param sourceLongitude       The longitude of the source point.
     * @param destinationLatitude   The latitude of the destination point.
     * @param destinationLongitude  The longitude of the destination point.
     * @return The distance between the two points on Earth.
     */
    private double haversineDistance(double sourceLatitude, double sourceLongitude,
                                     double destinationLatitude, double destinationLongitude) {
        double earthRadius = 6371; // km
        double dLat = Math.toRadians(destinationLatitude - sourceLatitude);
        double dLon = Math.toRadians(destinationLongitude - sourceLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(sourceLatitude)) * Math.cos(Math.toRadians(destinationLatitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

}
