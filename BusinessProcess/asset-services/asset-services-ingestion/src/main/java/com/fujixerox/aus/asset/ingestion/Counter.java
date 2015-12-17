package com.fujixerox.aus.asset.ingestion;

import java.util.concurrent.atomic.AtomicLong;

import com.fujixerox.aus.asset.ingestion.util.IngestionUtil;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Counter implements Runnable {

    private final AtomicLong _counter;

    private final long _sleepTime;

    public Counter(AtomicLong counter, long sleepTime) {
        _counter = counter;
        _sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            long prevValue = _counter.get();
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(_sleepTime);
                long curValue = _counter.get();
                IngestionUtil.err("Executions per " + _sleepTime + "ms: "
                        + (curValue - prevValue));
                prevValue = curValue;
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
