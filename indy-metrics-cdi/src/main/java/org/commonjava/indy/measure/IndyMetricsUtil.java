package org.commonjava.indy.measure;

import com.codahale.metrics.*;
import org.commonjava.indy.measure.annotation.IndyException;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by xiabai on 2/27/17.
 */
@ApplicationScoped
public class IndyMetricsUtil {
    private static final Logger logger = LoggerFactory.getLogger(IndyMetricsUtil.class);

    @Inject
    MetricRegistry metrics;

    private ScheduledReporter reporter;


    @PostConstruct
    public void initReporter(){
        reporter = ConsoleReporter.forRegistry(metrics).build();
        logger.info("call in IndyMetricsUtil.initReporter and reporter has been start");
        reporter.start(30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroyResource(){
        reporter.close();
    }

    public Timer getTimer(IndyMetrics indyMetrics){
        logger.info("call in IndyMetricsUtil.getTimer" );
        return metrics.timer(name(indyMetrics.c(), indyMetrics.name()));
    }

    public Meter getMeter(IndyMetrics indyMetrics){
        logger.info("call in IndyMetricsUtil.getMeter" );
        return metrics.meter(name(indyMetrics.c(), indyMetrics.name()));
    }

    public Meter getExceptionMeter(IndyException indyException){
        logger.info("call in IndyMetricsUtil.getExceptionMeter has exception" );
        return metrics.meter(name(indyException.c(), indyException.name()));
    }
}
