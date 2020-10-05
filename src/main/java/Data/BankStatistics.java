package Data;

import java.util.logging.Logger;

public class BankStatistics {

    private static final Logger LOG = Logger.getLogger(BankStatistics.class.getName());

    public int id;
    public int countUsers;
    public int minUsers;
    public int maxUsers;

    public BankStatistics(int id, int countUsers, int minUsers, int maxUsers) {
        this.id = id;
        this.countUsers = countUsers;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
    }
}
