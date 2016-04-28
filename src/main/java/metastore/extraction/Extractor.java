package metastore.extraction;

import java.util.concurrent.BlockingQueue;

public interface Extractor<S, T> {

    void extract(S s, BlockingQueue<T> queue) throws ExtractorException;
    
}
