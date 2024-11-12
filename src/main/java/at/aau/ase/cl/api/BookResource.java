package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.mapper.BookMapper;
import at.aau.ase.cl.service.BookService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.UUID;

@Path("/")
public class BookResource {
    @Inject
    BookService service;

    @POST
    @Path("book")
    public Response createBook(Book book) {
        // create book
        var model = BookMapper.INSTANCE.map(book);
        model = service.createBook(model);
        var result = BookMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @GET
    @Path("book/{id}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))})
    public Response getBook(@PathParam("id") UUID id) {
        var model = service.getBookById(id);
        var result = BookMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }
}
