package DataStreams.PrepareData;

import Data.UserData;
import DataStreams.AbstractDataStream;
import DataStreams.PrepareDataStream;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PrepareUserData implements PrepareDataStream<UserData> {

    private Map<Integer, Map<Instant, List<UserData>>> usersData = new HashMap<>();
    private Map<Integer, Instant> lastTime = new HashMap<>();
    private List<UserData> result = new LinkedList<>();

    @Override
    public Stream<UserData> generateStream() {
        return getList().stream();
    }

    public List<UserData> getList(){
        return result;
    }

    @Override
    public boolean addData(UserData data) {
        Map<Instant, List<UserData>> timeMap;
        List<UserData> list;
        if (usersData.containsKey(data.id)) {
            timeMap = usersData.get(data.id);

            if (timeMap.containsKey(data.time)) {
                list = timeMap.get(data.time);
                list.add(data);
            } else {
                list = new LinkedList<>();
                list.add(data);
                timeMap.put(data.time, list);

                //compact and clear map
                clearMap(data.id);
            }
        } else {
            timeMap = new HashMap<>();
            list = new LinkedList<>();

            list.add(data);
            timeMap.put(data.time, list);

            usersData.put(data.id, timeMap);
            lastTime.put(data.id, data.time);
        }
        return true;
    }

    public void clearMap(int id) {
        Map<Instant, List<UserData>> timeMap = usersData.get(id);
        if (timeMap.size() > 1) {
            Instant lastKey = lastTime.get(id);
            List<UserData> remove = timeMap.remove(lastKey);
            lastTime.put(id, timeMap.entrySet().iterator().next().getKey());
            double lat = remove.stream().mapToDouble(t -> t.lat).average().orElse(0);
            double lon = remove.stream().mapToDouble(t -> t.lon).average().orElse(0);
            double distance = remove.stream().mapToDouble(t -> t.distance).sum();
            result.add(new UserData(id, lastKey, lat, lon, distance));
        }
    }
}
