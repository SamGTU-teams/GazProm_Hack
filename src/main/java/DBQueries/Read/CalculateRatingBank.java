package DBQueries.Read;

import DBQueries.ConnectionToDB;
import Data.BankStatisticsInInterval;
import DataStreams.DataStream;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CalculateRatingBank extends ReadProcessToDB<BankStatisticsInInterval> {

    private static final Logger LOG = Logger.getLogger(CalculateRatingBank.class.getName());

    private static final String SELECT = "select paths.id from paths, banks " +
            "banks.id = ? and ? <= paths.timeinterval and ? < paths.timeinterval " +
            "and calculateDistance(paths.lat, banks.lat, paths.lon, banks.lon) < ?;";

    private static final String SELECT_BANKS = "select id from banks;";

    /**
     * Time interval in minutes.
     */
    private static int interval = 12;

    /**
     * ATM search radius.
     */
    private static double distance = 50;

    public CalculateRatingBank(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    public Stream<BankStatisticsInInterval> generateStream() {
        return getList().stream();
    }

    public List<BankStatisticsInInterval> getList() {
        List<BankStatisticsInInterval> result = new LinkedList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select min(timeinterval), max(timeinterval) from paths;");
            if (resultSet.next()) {
                final Instant minStamp = resultSet.getTimestamp(1).toInstant();
                final Instant maxStamp = resultSet.getTimestamp(2).toInstant().plusSeconds(1);
                Instant last = minStamp;
                while (last.isBefore(maxStamp)) {
                    Instant current = last.plusSeconds(interval * 60);
                    result.addAll(dataInInterval(last, current));
                    last = current;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    /**
     * Rating data from start timestamp to end timestamp.
     *
     * @param start
     * @param end
     */
    private List<BankStatisticsInInterval> dataInInterval(Instant start, Instant end) {
        List<BankStatisticsInInterval> result = new LinkedList<>();
        try (Statement statementBanks = connection.createStatement()) {
            ResultSet bankIds = statementBanks.executeQuery(SELECT_BANKS);
            while (bankIds.next()) {
                int id = bankIds.getInt(1);
                try (PreparedStatement statementSelect = connection.prepareStatement(SELECT)) {
                    statementSelect.setInt(1, id);
                    statementSelect.setTimestamp(2, Timestamp.from(start));
                    statementSelect.setTimestamp(3, Timestamp.from(end));
                    statementSelect.setDouble(4, distance);
                    ResultSet usersSet = statementSelect.executeQuery();
                    List<Integer> users = new LinkedList<>();
                    while (usersSet.next()) {
                        users.add(usersSet.getInt(1));
                    }
                    int count = (int) users.stream().distinct().count();
                    int min = users.stream().min(Integer::compareTo).orElse(0);
                    int max = users.stream().max(Integer::compareTo).orElse(0);
                    result.add(new BankStatisticsInInterval(id, end, count, min, max));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
