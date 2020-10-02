import java.time.LocalDateTime;

public class RawData {
    public int userId;
    public LocalDateTime time;
    public double lat;
    public double lon;

    public RawData(int userId, LocalDateTime time, double lat, double lon) {
        this.userId = userId;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }
}
