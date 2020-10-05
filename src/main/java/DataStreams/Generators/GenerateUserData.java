package DataStreams.Generators;

import Data.Location;
import Data.UserData;
import DataStreams.DataStream;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GenerateUserData implements DataStream<UserData> {

    private static final Logger LOG = Logger.getLogger(GenerateUserData.class.getName());

    public static Location MAX = new Location(57.2, 41);
    public static Location MIN = new Location(54.6, 35);
    public static int secondsInMinute = 60;
    public static final int EARTH_RADIUS = 6372795;

    private int[] ids;
    private Instant start;
    private Instant end;

    private UserData[] last;

    public GenerateUserData(Instant start, Instant end, int... id) {
        LOG.info("Init " + getClass().getSimpleName() + '\n');
        this.ids = id;
        this.start = start;
        this.end = end;
        last = new UserData[id.length];
        Arrays.setAll(last, value -> null);
    }

    @Override
    public Stream<UserData> generateStream() {
        return getList().stream();
    }

    public List<UserData> getList() {
        List<UserData> list = new LinkedList<>();

        for (int i = 0; i < ids.length; i++) {
            Instant cur = start;
            while (cur.isBefore(end)) {
                list.addAll(getMinuteData(i, cur));
                cur = cur.plusSeconds(60);
            }
        }
        return list;
    }

    private List<UserData> getMinuteData(int index, final Instant time) {
        List<UserData> list = new LinkedList<>();
        int i = 0;
        if (last[index] == null) {
            i++;
            double lon = (Math.random() * (MAX.getLon() - MIN.getLon())) + MIN.getLon();
            double lat = (Math.random() * (MAX.getLat() - MIN.getLat())) + MIN.getLat();
            last[index] = new UserData(ids[index], time, lat, lon, 0);
            list.add(last[index]);
        }
        for (; i < secondsInMinute; i++) {
            double dLon = Math.random() > 0.5 ? -0.00001 : 0.00001;
            double dLat = Math.random() > 0.5 ? -0.00001 : 0.00001;
            double nLat = last[index].lat + dLat;
            double nLon = last[index].lon + dLon;
            double distance = calculateDistance(last[index].lat, nLat, last[index].lon, nLon);
            last[index] = new UserData(index, time, nLat, nLon, distance);
            list.add(last[index]);
        }
        return list;
    }

    private double calculateDistance(double lat1, double lat2, double lon1, double lon2){
            double difLat = Math.toRadians(lat2 - lat1);
            double difLon = Math.toRadians(lon2 - lon1);

            double result = Math.pow(Math.sin(difLon / 2), 2);
            result *= Math.cos(Math.toRadians(lat1));
            result *= Math.cos(Math.toRadians(lat2));
            result += Math.pow(Math.sin(difLat / 2), 2);
            result = 2 * Math.atan2(Math.sqrt(result), Math.sqrt(1 - result));
            return result * EARTH_RADIUS;
    }
}
