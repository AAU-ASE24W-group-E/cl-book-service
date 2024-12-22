package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.OwnBook;
import at.aau.ase.cl.mapper.OwnBookMapper;
import at.aau.ase.cl.service.BookOwnerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.UUID;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookOwnerResource {
    @Inject
    BookOwnerService service;

    @PUT
    @Path("book-owner/{ownerId}/book/{bookId}")
    @APIResponse(responseCode = "204", description = "Updated")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response updateBookOwner(@PathParam("ownerId") UUID ownerId,
                                    @PathParam("bookId") UUID bookId,
                                    @RequestBody OwnBook ownBook) {
        var model = OwnBookMapper.INSTANCE.map(ownerId, bookId, ownBook);
        service.createOrUpdateBookOwnership(model);
        return Response.noContent().build();
    }
}
