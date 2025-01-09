package at.aau.ase.cl.domain;

public record AvailableBookProjection(
        BookOwnershipEntity bookOwnership,
        BookEntity book,
        BookOwnerEntity owner,
        Double distance
) {
    /**
     * Returns the distance in kilometers rounded to two decimal places.
     * @return the distance in kilometers or {@link Double#NaN} if the distance is not set
     */
    public double getRoundedDistanceKm() {
        return distance != null ? Math.round(distance / 10.0) / 100.0 : Double.NaN;
    }
}
