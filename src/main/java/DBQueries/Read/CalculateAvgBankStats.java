package DBQueries.Read;

import DBQueries.ConnectionToDB;
import Data.BankStatisticsAvg;
import DataStreams.DataStream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CalculateAvgBankStats extends ReadProcessToDB<BankStatisticsAvg> {

    private static final Logger LOG = Logger.getLogger(CalculateAvgBankStats.class.getName());

    private static final String SELECT = "select id, sum(countusers), avg(minusers), avg(maxusers) from bankstats group by id";

    public CalculateAvgBankStats(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    public Stream<BankStatisticsAvg> generateStream() {
        return getList().stream();
    }

    public List<BankStatisticsAvg> getList(){
        List<BankStatisticsAvg> result = new LinkedList<>();

        try(Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(SELECT);
            while(rs.next()){
                int id = rs.getInt(1);
                int sum = rs.getInt(2);
                double min = rs.getDouble(3);
                double max = rs.getDouble(4);
                result.add(new BankStatisticsAvg(id, sum, min, max));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }
}
