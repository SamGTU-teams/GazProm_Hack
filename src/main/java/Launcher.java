import Data.*;
import InputStreams.Generators.*;
import InputStreams.StreamData;
import DBQueries.Write.*;
import DBQueries.ReadWrite.*;
import DBQueries.Read.GetIntegers;
import InputStreams.Readers.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

public class Launcher {

    private static final Logger LOG = Logger.getLogger(Launcher.class.getName());
    public static final String URL_DB = "jdbc:postgresql://localhost:5432/gazbank";
    public static final String PATH = "C:\\Users\\user\\Desktop\\Programs\\Projects\\GazProm_Hack\\src\\main\\resources\\Datas\\магазин.json";
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

        StreamData<UserData> users = new GenerateUserData(startTime, endTime, 1);

        StreamData<BankStatistics> statistics = new GenerateBanksStatistics(startTime, endTime, select.getArray());

        StreamData<BankData> banks = new ReaderBanks(URL_BANKS);

        StreamData<Building> buildings = new ReaderBuildings(PATH);

        PrepareProcess<BankData> bankData = new PrepareBankData(URL_DB, USERNAME, PASSWORD);

        PrepareProcess<UserData> userData = new PrepareUserData(URL_DB, USERNAME, PASSWORD);

        PrepareProcess<Building> buildingData = new PrepareBuilding(URL_DB, USERNAME, PASSWORD);

        PrepareProcess<BankStatistics> bankStatistics = new PrepareBankStatistics(URL_DB, USERNAME, PASSWORD);

        CalculateRating rateData = new CalculateRating(URL_DB, USERNAME, PASSWORD);

        CalculateAvgBankStats avgData = new CalculateAvgBankStats(URL_DB, USERNAME, PASSWORD);

        NarrateCashMachines narrateCashMachines = new NarrateCashMachines(URL_DB, USERNAME, PASSWORD);



//        userData.addAllData(users);

//        bankData.addAllData(banks);

//        buildingData.addAllData(buildings);

//        bankStatistics.addAllData(statistics);

//        rateData.updateData();

//        LOG.info("Start DBQueries.ReadWrite.CalculateAvgBankStats\n");
//        avgData.calculateAvgBanksStats();
//
//        LOG.info("Start DBQueries.ReadWrite.NarrateCashMachines\n");
//        narrateCashMachines.narrateCashMachines();


        LOG.info("Completed successfully");
    }
}
