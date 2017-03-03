package org.commonjava.indy.metrics.proxy.invocationhandler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.commonjava.indy.measure.annotation.IndyException;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.commonjava.indy.metrics.proxy.invocationhandler.impl.ExceptionHandler;
import org.commonjava.indy.metrics.proxy.invocationhandler.impl.MeterHandler;
import org.commonjava.indy.metrics.proxy.invocationhandler.impl.TimerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiabai on 3/1/17.
 */
public class IndyMeticsInvocationHandler<T> implements InvocationHandler{

    private  T proxyInstance ;
    private  MetricRegistry metricRegistry ;
    private  ScheduledReporter reporter ;

    private static final Logger logger = LoggerFactory.getLogger(IndyMeticsInvocationHandler.class);

    public IndyMeticsInvocationHandler(T proxyInstance){
        new IndyMeticsInvocationHandler(proxyInstance, new MetricRegistry() );

    }

    public IndyMeticsInvocationHandler(T proxyInstance,MetricRegistry metricRegistry){
        reporter = ConsoleReporter.forRegistry(metricRegistry).build();
        new IndyMeticsInvocationHandler( proxyInstance, metricRegistry, reporter  );

    }

    public IndyMeticsInvocationHandler(T proxyInstance,MetricRegistry metricRegistry,ScheduledReporter reporter){
        this.proxyInstance = proxyInstance;
        this.metricRegistry = metricRegistry;
        this.reporter = reporter;
        logger.info("call in IndyMeticsInvocationHandler(T proxyInstance,MetricRegistry metricRegistry,ScheduledReporter reporter) and report started");
        reporter.start(1, TimeUnit.SECONDS);

    }
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        Object object = null;
        try {
            if (method.getAnnotation(IndyMetrics.class) instanceof IndyMetrics) {
                IndyMetrics indyMetrics = (IndyMetrics) method.getAnnotation(IndyMetrics.class);
                if (indyMetrics.type().equals(IndyMetrics.MetricsType.TIMER)) {
                    TimerHandler handler = new TimerHandler();
                    object = handler.operation(metricRegistry, proxyInstance, method, objects, method.getAnnotation(IndyMetrics.class));
                }
                if( indyMetrics.type().equals(IndyMetrics.MetricsType.TIMER)) {
                    MeterHandler handler = new MeterHandler();
                    object = handler.operation(metricRegistry, proxyInstance, method, objects, method.getAnnotation(IndyMetrics.class));
                }
            }
        }
        catch(Throwable throwable) {
            if (method.getAnnotation(IndyException.class) instanceof IndyException) {
                ExceptionHandler handler = new ExceptionHandler();
                handler.operation(metricRegistry,method.getAnnotation(IndyException.class));
            }

            throw throwable;
        }
        return object;
    }
}
