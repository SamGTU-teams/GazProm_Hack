package DBQueries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class ConnectionToDB implements AutoCloseable {

    protected Logger LOG = Logger.getLogger(getClass().getName());

    protected Connection connection;

    protected ConnectionToDB(String url, String username, String password) {
        LOG.info("Init " + getClass().getSimpleName() + '\n');
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
