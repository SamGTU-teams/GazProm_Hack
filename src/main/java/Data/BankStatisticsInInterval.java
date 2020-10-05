package Data;

import java.time.Instant;
import java.util.logging.Logger;

public class BankStatisticsInInterval extends BankStatistics {

    private static final Logger LOG = Logger.getLogger(BankStatisticsInInterval.class.getName());

    public Instant time;

    public BankStatisticsInInterval(int id, Instant time, int countUsers, int minUsers, int maxUsers) {
        super(id, countUsers, minUsers, maxUsers);
        this.time = time;
    }
}
