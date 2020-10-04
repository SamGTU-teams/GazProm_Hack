package Data;

import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.util.logging.Logger;

public class Location {

    private static final transient Logger LOG = Logger.getLogger(Location.class.getName());

    @SerializedName("latitude")
    private double lat;
    @SerializedName("longitude")
    private double lon;

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Location() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
