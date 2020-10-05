package Data;

import java.time.Instant;
import java.util.logging.Logger;

public class UserData {

    private static final Logger LOG = Logger.getLogger(UserData.class.getName());

    public int id;
    public Instant time;
    public double lat;
    public double lon;
    public double distance;

    public UserData(int userId, Instant time, double lat, double lon, double distance) {
        this.id = userId;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
    }
}
