package ReadAndLoadToDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CalculateAvgBankStats extends ConnectionToDB {

    public static final String GROUP_BY_ID = "select id, sum(countusers), avg(minusers), avg(maxusers) from bankstats group by id";
    public static final String INSERT_AVG_STATS = "insert into avg_banks_stats(id, all_users, avg_min_users, avg_max_users)" +
            " values (?, ?, ?, ?)";

    public CalculateAvgBankStats(String url, String username, String password) {
        super(url, username, password);
    }

    public void calculateAvgBanksStats() {
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery(GROUP_BY_ID);
            while (resultSet.next()) {
                try (PreparedStatement insertState = connection.prepareStatement(INSERT_AVG_STATS)) {
                    insertState.setInt(1, resultSet.getInt(1));
                    insertState.setInt(2, resultSet.getInt(2));
                    insertState.setDouble(3, resultSet.getDouble(3));
                    insertState.setDouble(4, resultSet.getDouble(4));
                    insertState.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
