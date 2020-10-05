package Data;

public class BankStatisticsAvg {

    public int id;
    public int countUsers;
    public double minUsers;
    public double maxUsers;

    public BankStatisticsAvg(int id, int countUsers, double minUsers, double maxUsers) {
        this.id = id;
        this.countUsers = countUsers;
        this.minUsers = minUsers;
        this.maxUsers = maxUsers;
    }
}
