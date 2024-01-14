package si.ape.routing.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.ape.routing.api.v1.resources.requests.NextHopExcludeRequest;
import si.ape.routing.api.v1.resources.requests.NextHopRequest;
import si.ape.routing.api.v1.resources.responses.NextHopResponse;
import si.ape.routing.lib.Branch;
import si.ape.routing.lib.data.Pair;
import si.ape.routing.lib.data.Reason;
import si.ape.routing.services.beans.RoutingBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutingResource {

    private final Logger log = Logger.getLogger(RoutingResource.class.getName());

    @Inject
    private RoutingBean routingBean;

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get next hop between locations.", summary = "Get next hop")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Next hop in the form of branch is delivered."
            ),
            @APIResponse(responseCode = "404", description = "Next hop not found .")
    })
    @POST
    @Path("/next-hop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNextHop(NextHopRequest request) {

        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Pair<Branch, Reason> nextHopPair = routingBean.nextHop(request.getSource(), request.getDestination());

        if (nextHopPair.first == null) {
            if (nextHopPair.second == Reason.NO_PATH_FOUND) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }

        System.out.println(nextHopPair.first.getId());

        NextHopResponse response = new NextHopResponse(nextHopPair.first, nextHopPair.second);

        return Response.ok(response).build();
    }

    @Operation(description = "Get next hop between locations.", summary = "Get next hop")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Next hop in the form of branch is delivered."
            ),
            @APIResponse(responseCode = "404", description = "Next hop not found .")
    })
    @POST
    @Path("/next-hop-exclude")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNextHopExclude(NextHopExcludeRequest request) {

        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Pair<Branch, Reason> nextHopPair = routingBean.nextHop(request.getSource(), request.getDestination(), request.getExclude());

        if (nextHopPair.first == null) {
            if (nextHopPair.second == Reason.NO_PATH_FOUND) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }

        System.out.println(nextHopPair.first.getId());

        NextHopResponse response = new NextHopResponse(nextHopPair.first, nextHopPair.second);

        return Response.ok(response).build();
    }



    @Operation(description = "Get health check.", summary = "Get health check")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Service is running."
            ),
    })
    @GET
    @Path("/health")
    public Response getHealth() {
        return Response.ok().build();
    }

    @Operation(description = "Get ready check.", summary = "Get ready check")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Service is ready to be used."
            ),
    })
    @GET
    @Path("/ready")
    public Response getReady() {
        if (routingBean.isReady()) {
            return Response.ok().build();
        }

        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }


}
