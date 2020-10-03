import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Launcher {

    private static final Logger LOG = Logger.getLogger(Launcher.class.getName());
    public static final String URL_DB = "jdbc:postgresql://localhost:5432/gazbank";
    public static final String PATH = "C:\\Users\\user\\Desktop\\Datas";

    public static void main(String[] args) throws ClassNotFoundException {

        LOG.info("Load PrepareBankData");
        PrepareBankData bankData = new PrepareBankData(URL_DB, "postgres", "rassafel");

        LOG.info("Load PrepareUserData");
        PrepareUserData userData = new PrepareUserData(URL_DB, "postgres", "rassafel");

        LOG.info("Load CalculateRating");
        CalculateRating rateData = new CalculateRating(URL_DB, "postgres", "rassafel");

        LOG.info("Load PrepareBuilding");
        PrepareBuilding building = new PrepareBuilding(URL_DB, "postgres", "rassafel");


//        LOG.info("Start PrepareBankData");
//        readBanks().forEach(bankData::addData);
//
//        LOG.info("Start PrepareUserData");
//        generateUsers(1000, 2).forEach(userData::addData);
//
//        LOG.info("Start CalculateRating");
//        rateData.updateData();

        LOG.info("Start PrepareBuilding");
        buildings(Paths.get(PATH)).forEach(building::addData);

    }

    /**
     * Generate random users.
     *
     * @param countQueries
     * @param countUsers
     * @return
     */
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
                    double dLon = Math.random() > 0.5 ? -0.00001 : 0.00001;
                    double dLat = Math.random() > 0.5 ? -0.00001 : 0.00001;
                    cur.lat += dLat;
                    cur.lon += dLon;
                } else {
                    cur = new Data();
                    cur.time = LocalDateTime.now().withSecond(0).withNano(0);
                    cur.curSeconds = 0;
                    cur.lon = (Math.random() * 2.6) + 54.6;
                    cur.lat = (Math.random() * 6) + 35;
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

    /**
     * Parsing ATM's from https://www.gazprombank.ru/rest/hackathon/atm/.
     *
     * @return
     */
    public static Stream<CashMachine> readBanks() {
        Type bankType = new TypeToken<List<CashMachine>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<CashMachine> cashMachines = new ArrayList<>();
        String URL = "https://www.gazprombank.ru/rest/hackathon/atm/?page=";
        int startPage = 1;
        int endPage = 57;

        for (int i = startPage; i <= endPage; i++) {
            cashMachines.addAll(gson.fromJson(CashMachine.getReader(URL + i), bankType));
        }
        return cashMachines.stream();
    }

    public static List<Building> readBuildings(Path path) {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(CashMachine.GeoLocation.class, Building.geoDeser).
                create();
        List<Building> list = new LinkedList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            for (JsonElement j : jsonArray) {
                Building building = gson.fromJson(j, Building.class);
                building.setType(path.getName(path.getNameCount() - 1).toString().replaceAll("(?iU)(.+?)\\.(.+)", "$1"));
                building.setPriority(Building.PRIORITY_MAP.getOrDefault(building.getType(), (short) -1));
                list.add(building);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Stream<Building> buildings(Path path) {
        try {
            return Files.list(path).flatMap(o -> readBuildings(o).stream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }

}
