package org.commonjava.indy;

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
public class ReporterIntializer {
    private final static String INDY_METRICS_CONF = "org.commonjava.indy.metrics.conf";
    private final static String INDY_MERTICS_REPORTER = "org.commonjava.indy.metrics.reporter";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER = "GraphiteReporter";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME = "GraphiteReporter.hostname";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT = "GraphiteReporter.port";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX = "GraphiteReporter.prefix";
    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PERIOD = "GraphiteReporter.period";

    public static void initReporter(MetricRegistry metrics) throws Exception {
        String conf = System.getProperty(INDY_METRICS_CONF);

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(conf)));

        ScheduledReporter report = null;
        if (conf == null || ("").equals(conf)) {
            report = getConsoleReporter(metrics);
        } else {
            String reporter = properties.getProperty(INDY_MERTICS_REPORTER);
            if (INDY_METRICS_REPORTER_GRPHITEREPORTER.equals(reporter)) {
                report = getGraphiteReporter(metrics, properties);
            }
        }
        report.start(Integer.parseInt(properties.getProperty(INDY_METRICS_REPORTER_GRPHITEREPORTER_PERIOD)), TimeUnit.SECONDS);

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
