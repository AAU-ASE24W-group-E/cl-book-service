package at.aau.ase.cl.api.model;

import at.aau.ase.cl.domain.AvailableBooksSearchCriteria;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record FindAvailableBooksParams(
        @Schema(description = "Latitude of the location to search for available books from in coordinates in the WGS84 geodetic datum.",
                examples = "46.6160474")
        @NotNull
        @QueryParam("latitude")
        double latitude,

        @Schema(description = "Longitude of the location to search for available books from in coordinates in the WGS84 geodetic datum.",
                examples = "14.2628435")
        @NotNull
        @QueryParam("longitude")
        double longitude,

        @Schema(description = "Distance in km from the location")
        @Min(1)
        @Max(100)
        @DefaultValue("" + AvailableBooksSearchCriteria.DEFAULT_DISTANCE)
        @QueryParam("distance")
        Integer distance,

        @Schema(description = """
                Quick search string of one or multiple keywords separated by whitespace
                  to search case-insensitively in book title, author name, or ISBN.
                Keywords shorter than 4 characters are considered stop-words and ignored.
                If request contains also specific criteria such as author or title,
                  then they are excluded from the quick search.
                """ )
        @QueryParam("quickSearch")
        @Size(min = 4)
        String quickSearch,

        @Schema(description = "Author of the book to search for")
        @QueryParam("author")
        String author,

        @Schema(description = "Title of the book to search for")
        @QueryParam("title")
        String title,

        @Schema(description = "Restrict search for books with status lendable. "
                              + "If no status restriction is set, then all books including unavailable are returned")
        @QueryParam("lendable")
        Boolean lendable,

        @Schema(description = "Restrict search for books with status exchangeable")
        @QueryParam("exchangeable")
        Boolean exchangeable,

        @Schema(description = "Restrict search for books with status giftable")
        @QueryParam("giftable")
        Boolean giftable,

        @Schema(description = "Offset for pagination, i.e. how many records to skip before returning search result",
                defaultValue = "0")
        @QueryParam("offset")
        Integer offset,

        @Schema(description = "Limit for pagination, i.e. how many records of search result to return",
                defaultValue = "" + AvailableBooksSearchCriteria.DEFAULT_LIMIT)
        @Min(1)
        @Max(1000)
        @QueryParam("limit")
        Integer limit
) {
}
