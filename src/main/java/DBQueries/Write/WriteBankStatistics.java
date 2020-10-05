package DBQueries.Write;

import Data.BankStatisticsInInterval;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class WriteBankStatistics extends WriteProcessToDB<BankStatisticsInInterval> {

    private static final Logger LOG = Logger.getLogger(WriteBankStatistics.class.getName());

    private static final String SELECT = "select id from bankstats where id = ? and " +
            "timeinterval = date_trunc('minute', ?);";
    private static final String INSERT = "insert into bankstats(id,timeinterval,countusers,minusers,maxusers) values(?, ?, ?, ?, ?);";
    private static final String UPDATE = "update bankstats set(countusers,minusers,maxusers) = (?,?,?)" +
            "where where id = ? and timeinterval = date_trunc('minute', ?);";

    public WriteBankStatistics(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    protected boolean select(BankStatisticsInInterval data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT);
        statement.setInt(1, data.id);
        statement.setTimestamp(2, Timestamp.from(data.time));
        return statement.executeQuery().next();
    }

    @Override
    protected void update(BankStatisticsInInterval data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE);
        statement.setInt(1, data.countUsers);
        statement.setInt(2, data.minUsers);
        statement.setInt(3, data.maxUsers);
        statement.setInt(4, data.id);
        statement.setTimestamp(5, Timestamp.from(data.time));
        statement.executeUpdate();
    }

    @Override
    protected void insert(BankStatisticsInInterval data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT);
            statement.setInt(1, data.id);
        statement.setTimestamp(2, Timestamp.from(data.time));
        statement.setInt(3, data.countUsers);
        statement.setInt(4, data.minUsers);
        statement.setInt(5, data.maxUsers);
        statement.executeUpdate();
    }
}
