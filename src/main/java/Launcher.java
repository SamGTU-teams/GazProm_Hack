import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Launcher {

    public static final String URL_DB = "jdbc:postgresql://localhost:5432/gazbank";

    public static void main(String[] args) throws ClassNotFoundException {
        PrepareUserData userData = new PrepareUserData(URL_DB, "postgres", "rassafel");
        PrepareBankData bankData = new PrepareBankData(URL_DB, "postgres", "rassafel");

//        readBanks().forEach(bankData::addData);
//        generateUsers(10000000, 100).forEach(userData::addData);



    }

    public static Stream<RawData> generateUsers(int countQueries, final int countUsers) {
        Stream<RawData> stream = Stream.generate(new Supplier<RawData>() {
            private final int maxId = countUsers;
            HashMap<Integer, Data> map = new HashMap<>(maxId);

            @Override
            public RawData get() {
                int id = (int) (Math.random() * maxId);
                Data cur;
                if (map.containsKey(id)) {
                    cur = map.get(id);
                    if (cur.curSeconds++ == 60) {
                        cur.curSeconds = 0;
                        cur.time = cur.time.plusMinutes(1);
                    }
                    double dLon = Math.random() > 0.5 ? -0.001 : 0.001;
                    double dLat = Math.random() > 0.5 ? -0.001 : 0.001;
                    cur.lat += dLat;
                    cur.lon += dLon;
                } else {
                    cur = new Data();
                    cur.time = LocalDateTime.now().withSecond(0).withNano(0);
                    cur.curSeconds = 0;
                    cur.lon = (Math.random() * 360) - 180;
                    cur.lat = (Math.random() * 180) - 90;
                }
                map.put(id, cur);
                return new RawData(id, cur.time, cur.lat, cur.lon);
            }

            class Data {
                public int curSeconds;
                public LocalDateTime time;
                public double lon;
                public double lat;
            }
        });
        return stream.limit(countQueries);
    }

    public static Stream<CashMachine> readBanks(){
        Type bankType = new TypeToken<List<CashMachine>>(){}.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<CashMachine> cashMachines = new ArrayList<>();
        String URL = "https://www.gazprombank.ru/rest/hackathon/atm/?page=";
        int startPage = 1;
        int endPage = 57;

        for (int i = startPage; i <= endPage ; i++) {
            cashMachines.addAll(gson.fromJson(CashMachine.getReader(URL + i), bankType));
        }
        return cashMachines.stream();
    }
}
