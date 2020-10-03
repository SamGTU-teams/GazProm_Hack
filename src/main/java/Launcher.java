import Data.*;
import Generators.*;
import InputStreams.StreamData;
import LoadersToDB.*;
import ReadAndLoadToDB.CalculateAvgBankStats;
import ReadAndLoadToDB.CalculateRating;
import Readers.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Launcher {

    private static final Logger LOG = Logger.getLogger(Launcher.class.getName());
    public static final String URL_DB = "jdbc:postgresql://localhost:5432/gazbank";
    public static final String PATH = "C:\\Users\\user\\Desktop\\Datas\\магазин.json";
    public static final String URL_BANKS = "https://www.gazprombank.ru/rest/hackathon/atm/?page=";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "rassafel";

    public static void main(String[] args) {

        LOG.info("Init class Instant\n");
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        Instant startTime = now.toInstant(ZoneOffset.UTC);
        Instant endTime = now.plusHours(4).toInstant(ZoneOffset.UTC);
        LOG.info("\nstartTime = " + startTime + "\nendTime = " + endTime + '\n');


        LOG.info("Init class Generators.GenerateUserData\n");
        StreamData<UserData> users = new GenerateUserData();

        LOG.info("Init class Generators.GenerateBanksStatistics\n");
        StreamData<BankStatistics> statistics = new GenerateBanksStatistics();

        LOG.info("Init class Readers.ReaderBanks\n");
        ReadData<BankData> banks = new ReaderBanks();

        LOG.info("Init class Readers.ReaderBanks\n");
        ReadData<Building> buildings = new ReaderBuildings();

        LOG.info("Init class LoadersToDB.PrepareBankData\n");
        PrepareProcess<BankData> bankData = new PrepareBankData(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class LoadersToDB.PrepareUserData\n");
        PrepareProcess<UserData> userData = new PrepareUserData(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class LoadersToDB.PrepareBuilding\n");
        PrepareProcess<Building> buildingData = new PrepareBuilding(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class LoadersToDB.PrepareBankStatistics\n");
        PrepareProcess<BankStatistics> bankStatistics = new PrepareBankStatistics(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class ReadAndLoadToDB.CalculateRating\n");
        CalculateRating rateData = new CalculateRating(URL_DB, USERNAME, PASSWORD);

        LOG.info("Init class ReadAndLoadToDB.CalculateAvgBankStats\n");
        CalculateAvgBankStats avgData = new CalculateAvgBankStats(URL_DB, USERNAME, PASSWORD);


//        LOG.info("Start create and load user\n");
//        userData.addAllData(users.getStream(1, startTime, endTime));

//        LOG.info("Start load banks\n");
//        bankData.addAllData(banks.getStream(URL_BANKS));

//        LOG.info("Start load buildings\n");
//        buildingData.addAllData(buildings.getStream(PATH));

//        LOG.info("Start create and load bankstats\n");
//        bankStatistics.addAllData(statistics.getStream(324394, startTime, endTime));

//        LOG.info("Start CalculateRating\n");
//        rateData.updateData();

//        LOG.info("Start CalculateAvgBankStats\n");
//        avgData.calculateAvgBanksStats();




        LOG.info("Completed successfully");
    }

    /**
     * Generate random users.
     *
     * @param countQueries
     * @param countUsers
     * @return
     */
    public static Stream<UserData> generateUsers(int countQueries, final int countUsers) {
        Stream<UserData> stream = Stream.generate(new Supplier<>() {
            private final int maxId = countUsers;
            HashMap<Integer, Data> map = new HashMap<>(maxId);

            @Override
            public UserData get() {
                int id = (int) (Math.random() * maxId);
                Data cur;
                if (map.containsKey(id)) {
                    cur = map.get(id);
                    if (cur.curSeconds++ == 60) {
                        cur.curSeconds = 0;
                        cur.time = cur.time.plusSeconds(60);
                    }
                    double dLon = Math.random() > 0.5 ? -0.00001 : 0.00001;
                    double dLat = Math.random() > 0.5 ? -0.00001 : 0.00001;
                    cur.lat += dLat;
                    cur.lon += dLon;
                } else {
                    cur = new Data();
                    cur.time = Instant.now();
                    cur.curSeconds = 0;
                    cur.lon = (Math.random() * 2.6) + 54.6;
                    cur.lat = (Math.random() * 6) + 35;
                }
                map.put(id, cur);
                return new UserData(id, cur.time, cur.lat, cur.lon);
            }

            class Data {
                public int curSeconds;
                public Instant time;
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
    public static Stream<BankData> readBanks() {
        Type bankType = new TypeToken<List<BankData>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<BankData> cashMachines = new ArrayList<>();
        String URL = "https://www.gazprombank.ru/rest/hackathon/atm/?page=";
        int startPage = 1;
        int endPage = 57;

        for (int i = startPage; i <= endPage; i++) {
            cashMachines.addAll(gson.fromJson(BankData.getReader(URL + i), bankType));
        }
        return cashMachines.stream();
    }

    public static List<Building> readBuildings(Path path) {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Location.class, Building.geoDeser).
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
