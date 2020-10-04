package DBQueries.Write;

import InputStreams.StreamData;
import DBQueries.ConnectionToDB;

import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class PrepareProcess<T> extends ConnectionToDB {

    private static final Logger LOG = Logger.getLogger(PrepareProcess.class.getName());

    protected PrepareProcess(String url, String username, String password) {
        super(url, username, password);
    }

    public abstract boolean addData(T data);

    public boolean addAllData(StreamData<T> stream){
        return addAllData(stream.getStream());
    }

    public boolean addAllData(Stream<T> stream){
        LOG.info("Start load data\n");
        try{
            stream.forEach(this::addData);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
