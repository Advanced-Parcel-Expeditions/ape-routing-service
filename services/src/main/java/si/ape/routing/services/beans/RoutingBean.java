package si.ape.routing.services.beans;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.json.JSONObject;
import si.ape.routing.lib.Branch;
import si.ape.routing.lib.Street;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import si.ape.routing.models.converters.BranchConverter;
import si.ape.routing.models.entities.BranchEntity;

@ApplicationScoped
public class RoutingBean {

    private Logger log = Logger.getLogger(RoutingBean.class.getName());

    @Inject
    private EntityManager em;

    private SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = null;

    private final double haversineMaxDistance = 200; // km

    public RoutingBean() {
        if (graph == null) {
            generateGraph();
        }
    }

    public Branch nextHop(Street source, Street destination) {
        return null;
    }



    public Branch nextHopOld(Street source, Street destination) {

        try {
            List<BranchEntity> branchEntities = em.createNamedQuery("BranchEntity.getAll")
                    .getResultList();
            ArrayList<Branch> branches = branchEntities.stream()
                    .map(BranchConverter::toDto)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            // Create graph.
            /*SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
                    new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);*/
            // Only create the graph if it does not exist yet, i.e. if this is the first time
            // this method is called, which should be the case for the first request. This is
            // a form of caching, to prevent hitting the Google Distance Matrix API too often.
            if (graph == null) {
                graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            }
            for (Branch branch : branches) {
                graph.addVertex(branch.getId());
            }

            // If the graph is empty, add edges between all branches that are close enough.
            // Again, this is a form of caching, to prevent hitting the Google Distance Matrix API too often.
            if (graph.edgeSet().isEmpty()) {
                /*final int MAX_DISTANCE = 200; // km

                // Create both-ways connections between all parcel centers. This is needed due to the limitations of the used
                // version of JGraphT library, as it does not include a functional undirected graph variant.
                double distance;
                for (int i = 0; i < branches.size(); i++) {
                    for (int j = 0; j < branches.size(); j++) {
                        if (i != j) {
                            Double sourceLatitude = branches.get(i).getStreet().getCity().getLatitude();
                            Double sourceLongitude = branches.get(i).getStreet().getCity().getLongitude();
                            Double destinationLatitude = branches.get(j).getStreet().getCity().getLatitude();
                            Double destinationLongitude = branches.get(j).getStreet().getCity().getLongitude();
                            distance = getDistanceBetweenCoordinates(sourceLatitude, sourceLongitude,
                                    destinationLatitude, destinationLongitude);
                            // Add edge only for branches that are close enough.
                            if (distance < MAX_DISTANCE) {
                                DefaultWeightedEdge edge = graph.addEdge(
                                        branches.get(i).getId(),
                                        branches.get(j).getId()
                                );
                                graph.setEdgeWeight(edge, distance);
                            }
                        }
                    }
                }*/
                generateGraph(branches);
            }

            /*Branch closestBranchToSource = null;
            Branch closestBranchToDestination = null;
            String sourceIsoCode = source.getCity().getCountry().getCode();
            Double sourceLatitude = source.getCity().getLatitude();
            Double sourceLongitude = source.getCity().getLongitude();
            String destinationIsoCode = destination.getCity().getCountry().getCode();
            Double destinationLatitude = destination.getCity().getLatitude();
            Double destinationLongitude = destination.getCity().getLongitude();
            double sourceMinimumDistance = Double.MAX_VALUE;
            double destinationMinimumDistance = Double.MAX_VALUE;
            for (Branch branch : branches) {
                String branchIsoCode = branch.getStreet().getCity().getCountry().getCode();
                Double branchLatitude = branch.getStreet().getCity().getLatitude();
                Double branchLongitude = branch.getStreet().getCity().getLongitude();
                if (branchIsoCode.equals(sourceIsoCode)) {
                    double currentDistance = getDistanceBetweenCoordinates(sourceLatitude, sourceLongitude,
                            branchLatitude, branchLongitude);
                    if (currentDistance < sourceMinimumDistance) {
                        sourceMinimumDistance = currentDistance;
                        closestBranchToSource = branch;
                    }
                } else if (branchIsoCode.equals(destinationIsoCode)) {
                    double currentDistance = getDistanceBetweenCoordinates(destinationLatitude, destinationLongitude,
                            branchLatitude, branchLongitude);
                    if (currentDistance < destinationMinimumDistance) {
                        destinationMinimumDistance = currentDistance;
                        closestBranchToDestination = branch;
                    }
                }
            }*/

            String sourceIsoCode = source.getCity().getCountry().getCode();
            Double sourceLatitude = source.getCity().getLatitude();
            Double sourceLongitude = source.getCity().getLongitude();
            String destinationIsoCode = destination.getCity().getCountry().getCode();
            Double destinationLatitude = destination.getCity().getLatitude();
            Double destinationLongitude = destination.getCity().getLongitude();
            Pair<Branch, Branch> closestBranches = getClosestBranches(
                    sourceLatitude, sourceLongitude, sourceIsoCode,
                    destinationLatitude, destinationLongitude, destinationIsoCode,
                    branches
            );
            if (closestBranches == null) {
                return null;
            }
            Branch closestBranchToSource = closestBranches.t;
            Branch closestBranchToDestination = closestBranches.u;

            if (closestBranchToSource == null || closestBranchToDestination == null) {
                return null;
            }

            List<DefaultWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(
                    graph,
                    closestBranchToSource.getId(),
                    closestBranchToDestination.getId()
            ).getEdgeList();

            ArrayList<Branch> shortestPathBranches = new ArrayList<>();
            int count = 0;
            for (DefaultWeightedEdge edge : shortestPath) {
                String edgeString = edge.toString();
                String secondId = edgeString.substring(edgeString.lastIndexOf(' ') + 1, edgeString.length() - 1);
                if (count == 0) {
                    String firstId = edgeString.substring(1, edgeString.indexOf(' '));
                    List<Branch> nodeBranches = branches
                            .stream()
                            .filter(b -> Integer.toString(b.getId()).equals(firstId) || Integer.toString(b.getId()).equals(secondId))
                            .collect(Collectors.toList());
                    shortestPathBranches.addAll(nodeBranches);
                } else {
                    Optional<Branch> branch = branches
                            .stream().filter(b -> Integer.toString(b.getId()).equals(secondId)).findFirst();
                    branch.ifPresent(shortestPathBranches::add);
                }
                count++;
            }

            return shortestPathBranches.get(0);
        } catch (Exception e) {
            log.severe(e.getMessage());
            rollbackTx();
        }
        return null;
    }

    private void generateGraph() {
        List<BranchEntity> parcelCenterEntities = em.createNamedQuery("BranchEntity.getAllParcelCenters")
                .getResultList();
        List<Branch> parcelCenters = parcelCenterEntities.stream()
                .map(BranchConverter::toDto)
                .toList();

        // Set up the graph.
        if (graph == null) {
            graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

            for (Branch parcelCenter : parcelCenters) {
                graph.addVertex(parcelCenter.getId());
            }
        }

        double[][] adjacencyMatrix = new double[parcelCenters.size()][parcelCenters.size()];

        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix.length; j++) {
                if (i == j) {
                    adjacencyMatrix[i][j] = 0;
                } else {
                    double sourceLatitude = parcelCenters.get(i).getStreet().getCity().getLatitude();
                    double sourceLongitude = parcelCenters.get(i).getStreet().getCity().getLongitude();
                    double destinationLatitude = parcelCenters.get(j).getStreet().getCity().getLatitude();
                    double destinationLongitude = parcelCenters.get(j).getStreet().getCity().getLongitude();
                    double distance = haversineDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                    if (distance < haversineMaxDistance) {
                        adjacencyMatrix[i][j] = Double.MAX_VALUE;
                    } else {
                        adjacencyMatrix[i][j] = -1;
                    }
                }
            }
        }

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
        Client client = ClientBuilder.newClient();
        StringBuilder origins = new StringBuilder();
        StringBuilder destinations = new StringBuilder();
        int numOfDestinations = 0;
        HashMap<Integer, Integer> centersToSizes = new HashMap<>();
        for (int i = 0; i < parcelCenters.size(); i++) {
            double sourceLatitude = parcelCenters.get(i).getStreet().getCity().getLatitude();
            double sourceLongitude = parcelCenters.get(i).getStreet().getCity().getLongitude();
            ArrayList<Integer> destinationsIndexes = new ArrayList<>();
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                if (adjacencyMatrix[i][j] == Double.MAX_VALUE) {
                    destinationsIndexes.add(j);
                }
            }

            origins.append(sourceLatitude).append(",").append(sourceLongitude).append("|");

            int destinationCount = 0;
            int lastIndex = 0;
            for (int j : destinationsIndexes) {
                double destinationLatitude = parcelCenters.get(j).getStreet().getCity().getLatitude();
                double destinationLongitude = parcelCenters.get(j).getStreet().getCity().getLongitude();
                destinations.append(destinationLatitude).append(",").append(destinationLongitude).append("|");
                destinationCount++;
                if (destinationCount >= 24) {
                    client = ClientBuilder.newClient();
                    Response response = client.target(apiUrl)
                            .queryParam("origins", origins.toString())
                            .queryParam("destinations", destinations.toString())
                            .queryParam("units", "metric")
                            .queryParam("key", "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE")
                            .request()
                            .get();

                    if (response.getStatus() == 200) {
                        JSONObject json = new JSONObject(response.readEntity(String.class));
                        for (int k = lastIndex; k < j; k++) {
                            double distance = json.getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(k - lastIndex)
                                    .getJSONObject("distance")
                                    .getDouble("value") / 1000;
                            adjacencyMatrix[i][k] = distance;
                            adjacencyMatrix[k][i] = distance;
                        }
                    }
                    lastIndex = j - 1;
                }
            }
        }
//            }
//            if (numOfDestinations + destinationsIndexes.size() > 25) {
//                client = ClientBuilder.newClient();
//                Response response = client.target(apiUrl)
//                        .queryParam("origins", origins.toString())
//                        .queryParam("destinations", destinations.toString())
//                        .queryParam("units", "metric")
//                        .queryParam("key", "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE")
//                        .request()
//                        .get();
//
//                if (response.getStatus() == 200) {
//                    numOfDestinations = destinationsIndexes.size();
//                    List<Integer> sortedKeys = centersToSizes.keySet().stream().sorted().toList();
//                    JSONObject json = new JSONObject(response.readEntity(String.class));
//                    int offset = 0;
//                    for (int key : sortedKeys) {
//                        int nextValid = 0;
//                        for (int count = 0; count <  centersToSizes.get(key); count++) {
//                            double distance = json.getJSONArray("rows")
//                                    .getJSONObject(key)
//                                    .getJSONArray("elements")
//                                    .getJSONObject(offset + count)
//                                    .getJSONObject("distance")
//                                    .getDouble("value") / 1000;
//                            while (adjacencyMatrix[key][nextValid] != Double.MIN_VALUE) {
//                                nextValid++;
//                            }
//                            adjacencyMatrix[key][nextValid] = distance;
//                            adjacencyMatrix[nextValid][key] = distance;
//                            nextValid++;
//                        }
//                    }
//                }
//            } else {
//                for (int j = 0; j < destinationsIndexes.size(); j++) {
//                    int destinationIndex = destinationsIndexes.get(j);
//                    double destinationLatitude = parcelCenters.get(destinationIndex).getStreet().getCity().getLatitude();
//                    double destinationLongitude = parcelCenters.get(destinationIndex).getStreet().getCity().getLongitude();
//                    origins.append(sourceLatitude).append(",").append(sourceLongitude).append("|");
//                    destinations.append(destinationLatitude).append(",").append(destinationLongitude).append("|");
//                }
//            }
//            centersToSizes.put(i, destinationsIndexes.size());
//        }

    }

    /**
     * Generates the graph of all branches and their distances between each other using only one Google API request call.
     */
    private void generateGraph(List<Branch> branches) {
        final int MAX_DISTANCE = 200; // km

        StringBuilder coordinates = new StringBuilder();

        for (int i = 0; i <  branches.size(); i++) {
            Double latitude = branches.get(i).getStreet().getCity().getLatitude();
            Double longitude = branches.get(i).getStreet().getCity().getLongitude();
            coordinates.append(latitude).append(",").append(longitude);
            if (i < branches.size() - 1) {
                coordinates.append("|");
            }
        }

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
        Client client = ClientBuilder.newClient();
        Response response = client.target(apiUrl)
                .queryParam("origins", coordinates.toString())
                .queryParam("destinations", coordinates.toString())
                .queryParam("units", "metric")
                .queryParam("key", "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE")
                .request()
                .get();

        if (response.getStatus() == 200) {
            JSONObject json = new JSONObject(response.readEntity(String.class));
            double distance;
            for (int i = 0; i < branches.size(); i++) {
                for (int j = 0; j < branches.size(); j++) {
                    if (i != j) {
                        distance = json.getJSONArray("rows")
                                .getJSONObject(i)
                                .getJSONArray("elements")
                                .getJSONObject(j)
                                .getJSONObject("distance")
                                .getDouble("value") / 1000;
                        // Add edge only for branches that are close enough.
                        if (distance < MAX_DISTANCE) {
                            DefaultWeightedEdge edge = graph.addEdge(
                                    branches.get(i).getId(),
                                    branches.get(j).getId()
                            );
                            graph.setEdgeWeight(edge, distance);
                        }
                    }
                }
            }
        }
    }

    private Pair<Branch, Branch> getClosestBranches(double sourceLatitude, double sourceLongitude, String sourceISO,
                                                    double destinationLatitude, double destinationLongitude, String destinationISO,
                                                    List<Branch> branches) {
        try {
            StringBuilder coordinates = new StringBuilder();
            coordinates.append(sourceLatitude).append(",").append(sourceLongitude).append("|")
                    .append(destinationLatitude).append(",").append(destinationLongitude);

            StringBuilder destinations = new StringBuilder();
            for (int i = 0; i < branches.size(); i++) {
                Double latitude = branches.get(i).getStreet().getCity().getLatitude();
                Double longitude = branches.get(i).getStreet().getCity().getLongitude();
                destinations.append(latitude).append(",").append(longitude);
                if (i < branches.size() - 1) {
                    destinations.append("|");
                }
            }

            String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
            Client client = ClientBuilder.newClient();
            Response response = client.target(apiUrl)
                    .queryParam("origins", coordinates.toString())
                    .queryParam("destinations", destinations.toString())
                    .queryParam("units", "metrics")
                    .queryParam("key", "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE")
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                JSONObject json = new JSONObject(response.readEntity(String.class));
                double minDistanceToSource = Double.MAX_VALUE;
                int closestBranchToSourceIndex = 0;
                double minDistanceToDestination = Double.MAX_VALUE;
                int closestBranchToDestinationIndex = 0;
                double currentDistance;
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < branches.size(); j++) {
                        currentDistance = json.getJSONArray("rows")
                                .getJSONObject(i)
                                .getJSONArray("elements")
                                .getJSONObject(j)
                                .getJSONObject("distance")
                                .getDouble("value") / 1000;
                        if (i == 0) {
                            String branchISO = branches.get(i).getStreet().getCity().getCountry().getCode();
                            if (currentDistance < minDistanceToSource && !branchISO.equals(sourceISO)) {
                                minDistanceToSource = currentDistance;
                                closestBranchToSourceIndex = j;
                            }
                        } else {
                            String branchISO = branches.get(i).getStreet().getCity().getCountry().getCode();
                            if (currentDistance < minDistanceToDestination && !branchISO.equals(sourceISO)) {
                                minDistanceToDestination = currentDistance;
                                closestBranchToDestinationIndex = j;
                            }
                        }
                    }
                }
                Pair<Branch, Branch> closestBranches = new Pair<>(
                        branches.get(closestBranchToSourceIndex),
                        branches.get(closestBranchToDestinationIndex)
                );
                return closestBranches;
            }
        } catch (Exception e) {
            log.severe("Failed finding closest branches.");
            return null;
        }
        return null;
    }


    private double getDistanceBetweenCoordinates(double sourceLatitude, double sourceLongitude,
                                                 double destinationLatitude, double destinationLongitude) {
        try {
            String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";
            //URL url = new URL(apiUrl);
            Client client = ClientBuilder.newClient();
            Response response = client.target(apiUrl)
                    .queryParam("origins", sourceLatitude + "," + sourceLongitude)
                    .queryParam("destinations", destinationLatitude + "," + destinationLongitude)
                    .queryParam("units", "metric")
                    .queryParam("key", "AIzaSyARV5eFh9Kopz9tNUBFZWjpD8QS99mGDqE")
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                // Read the JSON response and extract the rows -> elements -> distance -> value.
                // The value is the distance in meters.
                JSONObject json = new JSONObject(response.readEntity(String.class));
                return json.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance")
                        .getDouble("value") / 1000;

            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            rollbackTx();
        }
        return 0;
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

    class Pair<T, U> {
        public final T t;
        public final U u;

        public Pair(T t, U u) {
            this.t= t;
            this.u= u;
        }
    }

}
