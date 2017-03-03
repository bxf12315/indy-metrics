package org.commonjava.indy;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.commonjava.indy.measure.annotation.Measure;
import org.commonjava.indy.measure.annotation.MetricNamed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by xiabai on 2/27/17.
 */
@ApplicationScoped
public class IndyMetricsUtil {
    private static final Logger logger = LoggerFactory.getLogger(IndyMetricsUtil.class);

    @Inject
    MetricRegistry metricRegistry;

    private ScheduledReporter reporter;

    @PostConstruct
    public void initReporter() {
        logger.info("call in IndyMetricsUtil.initReporter and reporter has been start");
        try {
            reporter = ReporterFactory.getReporter( metricRegistry );
            reporter.start(30, TimeUnit.SECONDS);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroyResource() {
        reporter.close();
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
