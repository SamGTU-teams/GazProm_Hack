package DataStreams.Generators;

import Data.BankStatisticsInInterval;
import DataStreams.DataStream;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GenerateBanksStatistics implements DataStream<BankStatisticsInInterval> {

    private static final Logger LOG = Logger.getLogger(GenerateBanksStatistics.class.getName());

    public static int interval = 12;

    public static int MAX_USERS = 10;
    public static int MIN_USERS = 0;

    private int[] ids;
    private Instant start;
    private Instant end;

    public GenerateBanksStatistics(Instant start, Instant end, int... id) {
        LOG.info("Init " + getClass().getSimpleName() + '\n');
        this.ids = id;
        this.start = start;
        this.end = end;
    }

    @Override
    public Stream<BankStatisticsInInterval> generateStream() {
        return getList().stream();
    }

    public List<BankStatisticsInInterval> getList() {
        List<BankStatisticsInInterval> list = new LinkedList<>();

        for (int i = 0; i < ids.length; i++) {
            Instant last = start;
            while (last.isBefore(end)) {
                Instant cur = last.plusSeconds(interval * 60);
                list.add(generateIntervalData(i, last, cur));
                last = cur;
            }
        }
        return list;
    }

    private BankStatisticsInInterval generateIntervalData(int index, final Instant start, final Instant end) {
        Map<Instant, Integer> timeMap = new HashMap<>();
        Instant cur = start;
        while (cur.isBefore(end)) {
            timeMap.put(cur, (int) (Math.random() * (MAX_USERS - MIN_USERS)) + MIN_USERS);
            cur = cur.plusSeconds(60);
        }
        int countUsers = timeMap.values().stream().mapToInt(t -> t).sum();
        int minUsers = timeMap.values().stream().min(Integer::compareTo).orElse(0),
                maxUsers = timeMap.values().stream().max(Integer::compareTo).orElse(0);
        return new BankStatisticsInInterval(ids[index], end, countUsers, minUsers, maxUsers);
    }
}
