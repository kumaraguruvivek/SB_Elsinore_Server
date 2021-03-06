package com.sb.elsinore.devices;

import com.sb.elsinore.BrewServer;
import jGPIO.InvalidGPIOException;
import java.math.BigDecimal;

/**
 * This class represents a compressor based device that needs a pause between
 * run cycles.
 *
 * @author Andy
 */
public class CompressorDevice extends OutputDevice {

    protected long lastStopTime = -1L;
    protected long lastStartTime = -1L;
    protected boolean running = false;
    protected long delayBetweenRuns = 1000 * 60 * 3; // 3 Minutes

    public CompressorDevice(String name, String gpio, BigDecimal cycleTimeSeconds) {
        super(name, gpio, cycleTimeSeconds);
    }

    
    /**
     * Run through a cycle and turn the device on/off as appropriate based on the input duty.
     * @param duty The percentage of time / power to run.  This will only run if the duty
     *              is between 0 and 100 and not null.
     */
    @Override
    public void runCycle(BigDecimal duty) throws InterruptedException, InvalidGPIOException {
        // Run if the duty is not null and is between 0 and 100 inclusive.
        if (duty != null && 
            duty.compareTo(BigDecimal.ZERO) > -1 &&
            duty.compareTo(HUNDRED) < 1) {
            initializeSSR();

            if (duty.compareTo(HUNDRED) == 0) {
                if (System.currentTimeMillis() - lastStopTime > delayBetweenRuns) {
                    if (!running) {
                        BrewServer.LOG.warning("Starting compressor device.");
                        lastStartTime = System.currentTimeMillis();
                    }
                    running = true;
                    setValue(true);
                } else {
                    BrewServer.LOG.warning("Need to wait before starting compressor again.: "+(delayBetweenRuns - (System.currentTimeMillis() - lastStopTime)));
                }
            }
            Thread.sleep(cycleTime.intValue());
        }
    }

    @Override
    public void turnOff() {
        if (running) {
            lastStopTime = System.currentTimeMillis();
            BrewServer.LOG.warning("Stopping compressor device.");
            BrewServer.LOG.warning("Ran for " + (lastStopTime - lastStartTime) / 60000f + " minutes");
        }
        running = false;
        setValue(false);
    }
    
    public void setDelay(BigDecimal delay)
    {
        delayBetweenRuns = delay.longValue() * 1000 * 60;
    }
}
