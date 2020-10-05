package DBQueries.Write;

import Data.BankData;
import DataStreams.DataStream;
import DBQueries.ConnectionToDB;
import DataStreams.WriteProcess;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class WriteProcessToDB<T> extends ConnectionToDB implements WriteProcess<T> {

    protected WriteProcessToDB(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public boolean addData(T data){
        try{
            if(select(data)){
                update(data);
            } else {
                insert(data);
            }
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addAllData(Stream<T> stream){
        log().info("Start load data\n");
        try{
            stream.forEach(this::addData);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract boolean select(T data) throws SQLException;
    protected abstract void update(T data) throws SQLException;
    protected abstract void insert(T data) throws SQLException;
}
