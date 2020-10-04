package InputStreams;

import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class StreamData<T> {

    private static final Logger LOG = Logger.getLogger(StreamData.class.getName());

    protected StreamData() {
        LOG.info("Init " + getClass().getSimpleName() + '\n');
    }

    public Stream<T> getStream(){
        LOG.info("Start create data\n");
        return generateStream();
    }

    protected abstract Stream<T> generateStream();
}
