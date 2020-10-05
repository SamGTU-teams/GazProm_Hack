package DataStreams;

import java.util.logging.Logger;

public abstract class AbstractDataStream<T> implements DataStream<T> {

    private Logger LOG = Logger.getLogger(AbstractDataStream.class.getName());

    protected AbstractDataStream(){
        LOG.info("Init " + getClass().getSimpleName() + '\n');
    }
}
