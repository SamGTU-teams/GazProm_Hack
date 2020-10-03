import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrepareBankData {

    public static final String UPDATE = "insert into banks(bankid, lat, lon, region, regionType, setlementtype, setlement, fulladdress, location) values (?,?,?,?,?,?,?,?,?);";
    public static final String SELECT = "select bankid from banks where bankid = ?;";

    private Connection connection;

    public PrepareBankData(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add ATM to database.
     * @param data
     */
    public void addData(CashMachine data){
        try(PreparedStatement statementSelect = connection.prepareStatement(SELECT)){
            statementSelect.setInt(1, data.getId());
            if(!statementSelect.executeQuery().next()){
                try(PreparedStatement statementUpdate = connection.prepareStatement(UPDATE)){
                    statementUpdate.setInt(1, data.getId());
                    statementUpdate.setDouble(2, data.getGeolocation().getLat());
                    statementUpdate.setDouble(3, data.getGeolocation().getLon());
                    statementUpdate.setString(4, data.getAddress().getRegion());
                    statementUpdate.setString(5, data.getAddress().getRegionType());
                    statementUpdate.setString(6, data.getAddress().getSettlementType());
                    statementUpdate.setString(7, data.getAddress().getSettlement());
                    statementUpdate.setString(8, data.getAddress().getFullAddress());
                    statementUpdate.setString(9, data.getAddress().getLocation());
                    statementUpdate.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
