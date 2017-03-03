package org.commonjava.indy.measure;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiabai on 3/3/17.
 */
public class ReporterFactory {
    private final static String INDY_METRICS_CONF = "org.commonjava.indy.metrics.conf";
    private final static String INDY_MERTICS_REPORTER = "org.commonjava.indy.metrics.reporter";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER = "GraphiteReporter";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME = "GraphiteReporter.hostname";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT = "GraphiteReporter.port";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX = "GraphiteReporter.prefix";

    public static ScheduledReporter getReporter(MetricRegistry metrics) throws IOException {
        String conf = System.getProperty(INDY_METRICS_CONF);
        if (conf == null || ("").equals(conf)) {
            return getConsoleReporter(metrics);
        } else {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(conf)));
            String reporter = properties.getProperty(INDY_MERTICS_REPORTER);
            if (INDY_METRICS_REPORTER_GRPHITEREPORTER.equals(reporter)) {
                return getGraphiteReporter(metrics,properties);
            }
        }
        return null;

    }

    private static ScheduledReporter getConsoleReporter(MetricRegistry metrics) {
        return ConsoleReporter.forRegistry(metrics).build();
    }

    private static ScheduledReporter getGraphiteReporter(MetricRegistry metrics, Properties properties) {
        final Graphite graphite = new Graphite(new InetSocketAddress(properties.getProperty(INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME),
                Integer.parseInt(properties.getProperty(INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT))));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
                .prefixedWith(properties.getProperty(INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        return reporter;
    }
}
