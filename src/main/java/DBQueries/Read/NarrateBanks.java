package DBQueries.Read;

import DBQueries.ConnectionToDB;
import Data.NarrateData;
import DataStreams.DataStream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class NarrateBanks extends ConnectionToDB implements DataStream<NarrateData> {

    private static final Logger LOG = Logger.getLogger(NarrateBanks.class.getName());

    private static final String AVG_QUERY = "select sum(avg_min_users), sum(avg_max_users), count(id) from avg_banks_stats;";
    private static final String SELECT = "select id, avg_min_users, avg_max_users from avg_banks_stats;";

    public static double MAX_LOAD = 1.3d;
    public static double MIN_LOAD = 0.6d;

    public static final String MAX = "max_atm_used";
    public static final String MIDDLE = "middle_atm_used";
    public static final String MIN = "min_atm_used";

    public NarrateBanks(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    public Stream<NarrateData> generateStream() {
        return getList().stream();
    }

    public List<NarrateData> getList(){
        List<NarrateData> result = new LinkedList<>();
        try(Statement statementAvg = connection.createStatement()){
            ResultSet setAvg = statementAvg.executeQuery(AVG_QUERY);
            double AVG = 0;
            if(setAvg.next()){
                AVG = (setAvg.getDouble(1) + setAvg.getDouble(2)) / 2 / setAvg.getInt(3);
            }
            try(Statement statement = connection.createStatement()){
                ResultSet set = statement.executeQuery(SELECT);
                while(set.next()){
                    int id = set.getInt(1);
                    double oneBankAvg = (set.getDouble(2) + set.getDouble(3)) / 2;
                    String toWhichDb;
                    if (oneBankAvg >= AVG * MAX_LOAD) {
                        toWhichDb = MAX;
                    } else if (oneBankAvg <= AVG * MIN_LOAD) {
                        toWhichDb = MIN;
                    } else {
                        toWhichDb = MIDDLE;
                    }
                    result.add(new NarrateData(toWhichDb, id, oneBankAvg));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
