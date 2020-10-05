package DBQueries.Read;

import DBQueries.ConnectionToDB;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GetIntegers extends ReadProcessToDB<Integer> {

    private static final Logger LOG = Logger.getLogger(GetIntegers.class.getName());

    private String select = "select %s from %s;";

    public GetIntegers(String url, String username, String password, String col, String table) {
        super(url, username, password);
        select = String.format(select, col, table);
    }

    @Override
    protected Logger log() {
        return LOG;
    }

    @Override
    public Stream<Integer> generateStream() {
        return getList().stream();
    }

    public List<Integer> getList() {
        List<Integer> list = new LinkedList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(select);
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public int[] getArray() {
        return getList().stream().mapToInt(t -> t).toArray();
    }
}
