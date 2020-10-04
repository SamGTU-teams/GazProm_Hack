package InputStreams.Generators;

import Data.Location;
import Data.UserData;
import InputStreams.Readers.ReaderBanks;
import InputStreams.StreamData;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GenerateUserData extends StreamData<UserData> {

    private static final Logger LOG = Logger.getLogger(GenerateUserData.class.getName());

    public static Location MAX = new Location(57.2, 41);
    public static Location MIN = new Location(54.6, 35);
    public static int secondsInMinute = 60;

    private int[] ids;
    private Instant start;
    private Instant end;

    private UserData[] last;

    public GenerateUserData(Instant start, Instant end, int... id) {
        super();
        this.ids = id;
        this.start = start;
        this.end = end;
        last = new UserData[id.length];
        Arrays.setAll(last, value -> null);
    }

    @Override
    protected Stream<UserData> generateStream() {
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
            last[index] = new UserData(ids[index], time, lat, lon);
            list.add(last[index]);
        }
        for (; i < secondsInMinute; i++) {
            double dLon = Math.random() > 0.5 ? -0.00001 : 0.00001;
            double dLat = Math.random() > 0.5 ? -0.00001 : 0.00001;
            last[index] = new UserData(index, time, last[index].lat + dLat, last[index].lon + dLon);
            list.add(last[index]);
        }
        return list;
    }
}
