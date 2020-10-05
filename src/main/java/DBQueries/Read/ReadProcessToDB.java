package DBQueries.Read;

import DBQueries.ConnectionToDB;
import DataStreams.DataStream;

public abstract class ReadProcessToDB<T> extends ConnectionToDB implements DataStream<T> {

    protected ReadProcessToDB(String url, String username, String password) {
        super(url, username, password);
    }
}
