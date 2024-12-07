package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.mapper.BookMapper;
import at.aau.ase.cl.service.BookService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.UUID;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {
    @Inject
    BookService service;

    @POST
    @Path("book")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class))})
    @APIResponse(responseCode = "400", description = "Bad Request")
    public Response createBook(Book book) {
        // create book
        var model = BookMapper.INSTANCE.map(book);
        model = service.createBook(model);
        var result = BookMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @PUT
    @Path("book/isbn/{isbn}")
    @Operation(summary = "Import book by ISBN")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class))})
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response importBookByIsbn(@PathParam("isbn") String isbn) {
        var model = service.importBookByIsbn(isbn);
        var result = BookMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @GET
    @Path("book/{id}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class))})
    public Response getBook(@PathParam("id") UUID id) {
        var model = service.getBookById(id);
        var result = BookMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @GET
    @Path("book/isbn/{isbn}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Book.class))})
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response getBookByIsbn(@PathParam("isbn") String isbn) {
        var model = service.getBookByIsbn(isbn);
        var result = BookMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }
}
