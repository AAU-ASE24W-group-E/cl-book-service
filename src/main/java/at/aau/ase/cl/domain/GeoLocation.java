package at.aau.ase.cl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;

@Embeddable
public class GeoLocation {
    static final String DELIMITER = ",";
    static final CoordinateReferenceSystem<G2D> CRS4326 = CoordinateReferenceSystems.WGS84;

    @Column(name = "location", columnDefinition = "geography(POINT,4326)")
    Point<G2D> point;

    protected GeoLocation() {
    }

    public GeoLocation(double latitude, double longitude) {
        point = DSL.point(CRS4326, DSL.g(longitude, latitude));
    }

    public static GeoLocation fromString(String latLonStr) {
        int delimiterIndex = latLonStr.indexOf(DELIMITER);
        if (delimiterIndex != -1) {
            var latitude = Double.parseDouble(latLonStr.substring(0, delimiterIndex));
            var longitude = Double.parseDouble(latLonStr.substring(delimiterIndex + DELIMITER.length()));
            return new GeoLocation(latitude, longitude);
        }
        throw new IllegalArgumentException("Invalid GeoCoordinates: " + latLonStr);
    }

    public double getLatitude() {
        return point.getPosition().getLat();
    }

    public double getLongitude() {
        return point.getPosition().getLon();
    }

    @Override
    public String toString() {
        return point == null ? "N/A" : getLatitude() + DELIMITER + getLongitude();
    }
}
