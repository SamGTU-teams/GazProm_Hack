package Data;

import java.time.Instant;
import java.util.logging.Logger;

public class UserData {

    private static final Logger LOG = Logger.getLogger(UserData.class.getName());

    public int userId;
    public Instant time;
    public double lat;
    public double lon;

    public UserData(int userId, Instant time, double lat, double lon) {
        this.userId = userId;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }
}
