package si.ape.routing.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.ape.routing.services.beans.ParcelBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import si.ape.routing.lib.*;

@ApplicationScoped
@Path("/routings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParcelResource {

    private final Logger log = Logger.getLogger(ParcelResource.class.getName());

    @Inject
    private ParcelBean parcelBean;


    @Context
    protected UriInfo uriInfo;


    @Operation(description = "Get parcel data by parameters.", summary = "Get parcel data by parameters")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Parcel data",
                    content = @Content(
                            schema = @Schema(implementation = Parcel.class))
            )})
    @GET
    public Response getParcel(@QueryParam("id") String id,
                              @QueryParam("senderId") Integer senderId,
                              @QueryParam("recipientId") Integer recipientId,
                              @QueryParam("parcelStatusId") Integer parcelStatusId) {

        List<Parcel> parcels = parcelBean.getParcelsByParameters(id, senderId, recipientId, parcelStatusId);

        /*if (parcels == null || parcels.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }*/

        return Response.status(Response.Status.OK).entity(parcels).build();
    }

    @Operation(description = "Add parcel data.", summary = "Add parcel data")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Parcel data successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createParcel(@RequestBody(
            description = "DTO object with parcel data.",
            required = true, content = @Content(
            schema = @Schema(implementation = Parcel.class))) Parcel parcel) {

        // TODO: Add validation
        if ((parcel.getId() == null || parcel.getRecipient() == null || parcel.getSender() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            parcel = parcelBean.createParcel(parcel);
        }

        return Response.status(Response.Status.OK).entity(parcel).build();

    }


    @Operation(description = "Update data for a parcel.", summary = "Update data for a parcel")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Parcel data successfully updated."
            )
    })
    @PUT
    @Path("{parcelId}")
    public Response putParcel(@Parameter(description = "Parcel ID.", required = true)
                                     @PathParam("parcelId") String parcelId,
                                     @RequestBody(
                                             description = "DTO object with parcel data.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = Parcel.class)))
                                     Parcel parcel){

        parcel = parcelBean.putParcel(parcelId, parcel);

        if (parcel == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @Operation(description = "Delete metadata for an image.", summary = "Delete metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metadata successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{parcelId}")
    public Response deleteParcel(@Parameter(description = "Metadata ID.", required = true)
                                        @PathParam("parcelId") Integer parcelId){

        boolean deleted = parcelBean.deleteParcel(parcelId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }





}
