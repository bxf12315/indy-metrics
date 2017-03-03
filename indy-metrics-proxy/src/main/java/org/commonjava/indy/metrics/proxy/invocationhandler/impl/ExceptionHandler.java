package org.commonjava.indy.metrics.proxy.invocationhandler.impl;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.commonjava.indy.measure.annotation.IndyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by xiabai on 3/1/17.
 */
public class ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
    public void operation(MetricRegistry metricRegistry, IndyException indyException) throws Throwable{
        Meter meter = metricRegistry.meter(name(indyException.c(),indyException.name()));
        meter.mark();
    }
}
