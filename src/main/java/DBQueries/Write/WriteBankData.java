package DBQueries.Write;

import Data.BankData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class WriteBankData extends WriteProcessToDB<BankData> {

    private static final Logger LOG = Logger.getLogger(WriteBankData.class.getName());

    private static final String SELECT = "select id from banks where id = ?;";
    private static final String INSERT = "insert into banks(id, lat, lon, region, regionType, setlementtype, setlement," +
            " fulladdress, location, replaced) values (?,?,?,?,?,?,?,?,?,?);";
    private static final String UPDATE = "update banks set (lat, lon, region, regionType, setlementtype, setlement, " +
            "fulladdress, location, replaced) = (?,?,?,?,?,?,?,?,?) where id = ?;";

    public WriteBankData(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    protected boolean select(BankData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT);
        statement.setInt(1, data.getId());
        return statement.executeQuery().next();
    }

    @Override
    protected void update(BankData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE);
        statement.setDouble(1, data.getGeolocation().getLat());
        statement.setDouble(2, data.getGeolocation().getLon());
        statement.setString(3, data.getAddress().getRegion());
        statement.setString(4, data.getAddress().getRegionType());
        statement.setString(5, data.getAddress().getSettlementType());
        statement.setString(6, data.getAddress().getSettlement());
        statement.setString(7, data.getAddress().getFullAddress());
        statement.setString(8, data.getAddress().getLocation());
        statement.setBoolean(9, false);
        statement.setInt(10, data.getId());
        statement.executeUpdate();
    }

    @Override
    protected void insert(BankData data) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT);
        statement.setInt(1, data.getId());
        statement.setDouble(2, data.getGeolocation().getLat());
        statement.setDouble(3, data.getGeolocation().getLon());
        statement.setString(4, data.getAddress().getRegion());
        statement.setString(5, data.getAddress().getRegionType());
        statement.setString(6, data.getAddress().getSettlementType());
        statement.setString(7, data.getAddress().getSettlement());
        statement.setString(8, data.getAddress().getFullAddress());
        statement.setString(9, data.getAddress().getLocation());
        statement.setBoolean(10, false);
        statement.executeUpdate();
    }
}
