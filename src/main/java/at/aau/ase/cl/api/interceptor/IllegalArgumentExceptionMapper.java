package at.aau.ase.cl.api.interceptor;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        Log.warn("IllegalArgumentException", exception);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse.of(exception))
                .build();
    }
}
