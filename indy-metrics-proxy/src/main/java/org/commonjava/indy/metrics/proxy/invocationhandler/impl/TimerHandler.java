package org.commonjava.indy.metrics.proxy.invocationhandler.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by xiabai on 3/1/17.
 */
public class TimerHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(TimerHandler.class);

    public Object operation(MetricRegistry metricRegistry, T proxyInstance, Method method, Object[] parameters,IndyMetrics indyMetrics) throws Throwable{
        logger.info("call in TimerHandler.operation");
        Timer.Context contextTime  = metricRegistry.timer(name(indyMetrics.c(),indyMetrics.name())).time();
        Object object =  method.invoke(proxyInstance,parameters);
        contextTime.stop();
        return object;
    }
}
