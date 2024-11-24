package at.aau.ase.cl.api.interceptor;

public record ErrorResponse(String type, String message) {
    public static ErrorResponse of(Throwable error) {
        return new ErrorResponse(error.getClass().getSimpleName(), error.getMessage());
    }
}
