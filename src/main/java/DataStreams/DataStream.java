package DataStreams;

import java.util.logging.Logger;
import java.util.stream.Stream;

public interface DataStream<T> {

    public Stream<T> generateStream();

    public default Stream<T> getStream(){
        Logger.getLogger(getClass().getName()).info("Start create data\n");
        return generateStream();
    }
}
