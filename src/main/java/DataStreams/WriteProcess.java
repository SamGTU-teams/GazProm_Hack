package DataStreams;

import java.util.stream.Stream;

public interface WriteProcess<T> {

    public boolean addData(T data);

    public default boolean addAllData(DataStream<T> stream){
        return addAllData(stream.getStream());
    }

    public default boolean addAllData(Stream<T> stream){
        try{
            stream.forEach(this::addData);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
