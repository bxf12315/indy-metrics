package org.commonjava.indy.metrics.proxy.invocationhandler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.commonjava.indy.measure.annotation.Measure;
import org.commonjava.indy.measure.annotation.MetricNamed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codahale.metrics.MetricRegistry.name;

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
    public Object invoke(Object o, Method method, Object[] parameters) throws Throwable {

        Object object = null;
        IndyMetrics metrics = method.getAnnotation( IndyMetrics.class );
        List<Timer.Context> timers = null;
        if (metrics instanceof IndyMetrics )
        {
            Measure measures = metrics.measure();
            timers = Stream.of( measures.timers() )
                                               .map( named -> getTimer( metrics, measures, named ).time() )
                                               .collect( Collectors.toList() );
        }

        try {
            return method.invoke(proxyInstance,parameters);
        }
        catch(Throwable throwable) {
            if ( metrics instanceof IndyMetrics )
            {
                Measure exceptions = metrics.exceptions();
                Stream.of( exceptions.meters() ).forEach( named->getMeter( metrics, exceptions, named ).mark() );
            }

            throw throwable;
        }
        finally
        {
            if( timers !=null)
            {
                timers.forEach( Timer.Context::stop );
            }
            if ( metrics instanceof IndyMetrics )
            {
                Measure measures = metrics.measure();
                Stream.of( measures.meters() ).forEach( named->getMeter( metrics, measures, named ).mark() );
            }
        }
    }

    public Timer getTimer( IndyMetrics metrics, Measure measures, MetricNamed named ) {
        logger.info("call in IndyMetricsUtil.getTimer");
        Class<?> c = getClass( metrics, measures, named );
        return this.metricRegistry.timer( name( c, named.name()));
    }

    public Meter getMeter( IndyMetrics metrics, Measure measures, MetricNamed named) {
        logger.info("call in IndyMetricsUtil.getMeter");
        Class<?> c = getClass( metrics, measures, named );
        return metricRegistry.meter( name( c, named.name()));
    }

    private Class<?> getClass( IndyMetrics metrics, Measure measures, MetricNamed named )
    {
        Class<?> c = named.c();
        if ( Void.class.equals( c ) )
        {
            c = measures.c();
        }

        if ( Void.class.equals( c ) )
        {
            c = metrics.c();
        }

        return c;
    }
}
