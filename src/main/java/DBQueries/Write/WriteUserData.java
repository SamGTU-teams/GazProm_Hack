package DBQueries.Write;

import Data.UserData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class WriteUserData extends WriteProcessToDB<UserData> {

    private static final Logger LOG = Logger.getLogger(WriteUserData.class.getName());

    private static final String CONTAINS_IN_USERS = "select id from users where id = ?;";
    private static final String INSERT_IN_USERS = "insert into users(id) values(?);";
    private static final String SELECT = "select id from paths where id = ? and " +
            "timeinterval = date_trunc('minute', ?);";
    private static final String INSERT = "insert into paths(id, timeInterval, lat, lon, distance) VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE = "update paths set(lat,lon,distance) = (?,?,?) where id = ? and " +
            "timeinterval = date_trunc('minute', ?);";

    public WriteUserData(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    public boolean addData(UserData data) {
        try (PreparedStatement statementSelect = connection.prepareStatement(CONTAINS_IN_USERS)) {
            statementSelect.setInt(1, data.id);
            if (!statementSelect.executeQuery().next()) {
                try (PreparedStatement statementInsert = connection.prepareStatement(INSERT_IN_USERS)) {
                    statementInsert.setInt(1, data.id);
                    statementInsert.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return super.addData(data);
    }

    @Override
    protected boolean select(UserData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT);
        statement.setInt(1, data.id);
        statement.setTimestamp(2, Timestamp.from(data.time));
        return statement.executeQuery().next();
    }

    @Override
    protected void update(UserData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE);
        statement.setDouble(1, data.lat);
        statement.setDouble(2, data.lon);
        statement.setDouble(3, data.distance);
        statement.setInt(4, data.id);
        statement.setTimestamp(5, Timestamp.from(data.time));
        statement.executeUpdate();
    }

    @Override
    protected void insert(UserData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT);
        statement.setInt(1, data.id);
        statement.setTimestamp(2, Timestamp.from(data.time));
        statement.setDouble(3, data.lat);
        statement.setDouble(4, data.lon);
        statement.setDouble(5, data.distance);
        statement.executeUpdate();
    }
}
