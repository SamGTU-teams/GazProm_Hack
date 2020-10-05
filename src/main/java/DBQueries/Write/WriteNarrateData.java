package DBQueries.Write;

import Data.NarrateData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class WriteNarrateData extends WriteProcessToDB<NarrateData> {

    private static final Logger LOG = Logger.getLogger(WriteUserData.class.getName());

    private static final String SELECT = "select id from %s where id = ?;";
    private static final String INSERT = "insert into %s (id,load_factor) values(?,?);";
    private static final String UPDATE = "update %s set load_factor = ? where id = ?;";

    public WriteNarrateData(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    protected boolean select(NarrateData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(SELECT, data.database));
        statement.setInt(1, data.id);
        return statement.executeQuery().next();
    }

    @Override
    protected void update(NarrateData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(UPDATE, data.database));
        statement.setDouble(1, data.loadFactor);
        statement.setInt(2, data.id);
        statement.executeUpdate();
    }

    @Override
    protected void insert(NarrateData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(INSERT, data.database));
        statement.setInt(1, data.id);
        statement.setDouble(2, data.loadFactor);
        statement.executeUpdate();
    }
}
