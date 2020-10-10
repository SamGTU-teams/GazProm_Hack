import DBQueries.Read.CalculateAvgBankStats;
import DBQueries.Read.CalculateRatingBank;
import DBQueries.Read.GetIntegers;
import DBQueries.Read.NarrateBanks;
import Data.*;
import DataStreams.DataStream;
import DBQueries.Write.*;
import DataStreams.Generators.GenerateBanksStatistics;
import DataStreams.Generators.GenerateUserData;
import DataStreams.PrepareData.PrepareUserData;
import DataStreams.PrepareDataStream;
import DataStreams.Readers.*;
import DataStreams.WriteProcess;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.logging.Logger;

public class Launcher {

    private static final Logger LOG = Logger.getLogger(Launcher.class.getName());
    public static final String URL_DB = "jdbc:postgresql://localhost:5432/gazbank";
    public static final String PATH = "src\\main\\resources\\Datas";
    public static final String URL_BANKS = "https://www.gazprombank.ru/rest/hackathon/atm/?page=";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "rassafel";

    public static void main(String[] args) {

        LOG.info("Init class Instant\n");
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        Instant startTime = now.toInstant(ZoneOffset.UTC);
        Instant endTime = now.plusHours(4).toInstant(ZoneOffset.UTC);
        LOG.info("\nstartTime = " + startTime + "\nendTime = " + endTime + '\n');


        GetIntegers select = new GetIntegers(URL_DB, USERNAME, PASSWORD, "id", "banks");

        DataStream<UserData> rawUsers = new GenerateUserData(startTime, endTime, 1);

        PrepareDataStream<UserData> users = new PrepareUserData();

//        DataStream<BankStatisticsInInterval> statistics = new CalculateRatingBank(URL_DB, USERNAME, PASSWORD);

        DataStream<BankStatisticsInInterval> statistics = new GenerateBanksStatistics(startTime, endTime, select.getArray());

        DataStream<BankData> banks = new ReaderBanks(URL_BANKS);

        DataStream<Building> buildings = new ReaderBuildings(filesList());

        WriteProcess<BankData> banksWrite = new WriteBankData(URL_DB, USERNAME, PASSWORD);

        WriteProcess<UserData> usersWrite = new WriteUserData(URL_DB, USERNAME, PASSWORD);

        WriteProcess<Building> buildingsWrite = new WriteBuilding(URL_DB, USERNAME, PASSWORD);

        WriteProcess<BankStatisticsInInterval> bankStatistics = new WriteBankStatistics(URL_DB, USERNAME, PASSWORD);

        DataStream<BankStatisticsAvg> avgData = new CalculateAvgBankStats(URL_DB, USERNAME, PASSWORD);

        DataStream<NarrateData> narrateBanks = new NarrateBanks(URL_DB, USERNAME, PASSWORD);

        WriteProcess<BankStatisticsAvg> avgWrite = new WriteBankAvgData(URL_DB, USERNAME, PASSWORD);

        WriteProcess<NarrateData> narrateWrite = new WriteNarrateData(URL_DB, USERNAME, PASSWORD);




        users.addAllData(rawUsers);

        usersWrite.addAllData(users);

        banksWrite.addAllData(banks);

        buildingsWrite.addAllData(buildings);

        bankStatistics.addAllData(statistics);

        avgWrite.addAllData(avgData);

        narrateWrite.addAllData(narrateBanks);





        LOG.info("Completed successfully");
    }

    private static String[] filesList(){
        return Arrays.stream(new File(PATH).listFiles()).map(File::getAbsolutePath).toArray(String[]::new);
    }
}
