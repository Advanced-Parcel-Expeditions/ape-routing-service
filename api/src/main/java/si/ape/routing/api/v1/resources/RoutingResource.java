package si.ape.routing.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.ape.routing.api.v1.resources.requests.NextHopRequest;
import si.ape.routing.lib.Branch;
import si.ape.routing.lib.Street;
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

        Branch nextHop = routingBean.nextHop(request.getSource(), request.getDestination());

        if (nextHop == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(nextHop).build();
    }


}
