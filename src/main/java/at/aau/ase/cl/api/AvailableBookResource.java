package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.FindAvailableBooksParams;
import at.aau.ase.cl.api.model.FindAvailableBooksResponse;
import at.aau.ase.cl.service.AvailableBookService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/available-book")
@Produces(MediaType.APPLICATION_JSON)
public class AvailableBookResource {
    @Inject
    AvailableBookService service;

    @GET
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = FindAvailableBooksResponse.class))})
    @APIResponse(responseCode = "400", description = "Bad Request")
    public Response findAvailableBooks(@BeanParam FindAvailableBooksParams params) {
        Log.tracef("Searching for available books with params: %s", params);
        var response = service.findAvailableBooks(params);
        return Response.ok(response).build();
    }
}
