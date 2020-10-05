package DBQueries.Write;

import Data.BankStatisticsAvg;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class WriteBankAvgData extends WriteProcessToDB<BankStatisticsAvg> {

    private static final Logger LOG = Logger.getLogger(WriteBankAvgData.class.getName());
    private static final String SELECT = "select id from avg_banks_stats where id = ?";
    private static final String UPDATE = "update avg_banks_stats set (all_users,avg_min_users,avg_max_users) = (?,?,?) where id = ?;";
    private static final String INSERT = "insert into avg_banks_stats (id,all_users,avg_min_users,avg_max_users) values(?,?,?,?);";

    public WriteBankAvgData(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    protected boolean select(BankStatisticsAvg data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT);
        statement.setInt(1, data.id);
        return statement.executeQuery().next();
    }

    @Override
    protected void update(BankStatisticsAvg data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE);
        statement.setInt(1, data.countUsers);
        statement.setDouble(2, data.minUsers);
        statement.setDouble(3, data.maxUsers);
        statement.setInt(4, data.id);
        statement.executeUpdate();
    }

    @Override
    protected void insert(BankStatisticsAvg data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT);
        statement.setInt(1, data.id);
        statement.setInt(2, data.countUsers);
        statement.setDouble(3, data.minUsers);
        statement.setDouble(4, data.maxUsers);
        statement.executeUpdate();
    }
}
