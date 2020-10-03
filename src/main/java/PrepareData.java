import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class PrepareData<T> {
    protected Connection connection;
    protected PrepareData(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean addData(T data);
}
