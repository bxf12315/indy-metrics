package org.commonjava.indy.metrics.reporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.commonjava.indy.metrics.filter.HealthCheckMetricFilter;
import org.commonjava.indy.metrics.filter.JVMMetricFilter;
import org.commonjava.indy.metrics.filter.SimpleMetricFilter;
import org.commonjava.indy.metrics.zabbix.sender.IndyZabbixApi;
import org.commonjava.indy.metrics.zabbix.sender.ZabbixApi;
import org.commonjava.indy.metrics.zabbix.sender.IndyZabbixSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.commonjava.indy.metrics.zabbix.sender.IndyZabbixReporter;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiabai on 3/3/17.
 */
public class ReporterIntializer
{
    private static final Logger logger = LoggerFactory.getLogger( ReporterIntializer.class );

    private final static String INDY_METRICS_CONF = "org.commonjava.indy.metrics.conf";

    private final static String INDY_METRICS_REPORTER = "org.commonjava.indy.metrics.reporter";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER = "GraphiteReporter";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME = "GraphiteReporter.hostname";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT = "GraphiteReporter.port";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX = "GraphiteReporter.prefix";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_SIMPLE_PERIOD = "GraphiteReporter.simple.period";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_JVM_PERIOD = "GraphiteReporter.jvm.period";

    private final static String INDY_METRICS_REPORTER_GRPHITEREPORTER_HEALTHCHECK_PERIOD =
                    "GraphiteReporter.healthcheck.period";

    private final static int INDY_METRICS_REPORTER_GRPHITEREPORTER_DEFAULT_PERIOD = 30;

    public static void initReporter( MetricRegistry metrics ) throws Exception
    {
        String conf = System.getProperty( INDY_METRICS_CONF );

        Properties properties = new Properties();
        properties.load( new FileInputStream( new File( conf ) ) );
        initZabbixReporport( metrics, properties );
        if ( conf == null || ( "" ).equals( conf ) )
        {
            initConsoleReporter( metrics, properties );
        }
        else
        {
            String reporter = properties.getProperty( INDY_METRICS_REPORTER );
            if ( INDY_METRICS_REPORTER_GRPHITEREPORTER.equals( reporter ) )
            {
//                initGraphiteReporterForSimpleMetric( metrics, properties );
//                initGraphiteReporterForJVMMetric( metrics, properties );
//                initGraphiteReporterForHealthCheckMetric( metrics, properties );
            }
        }

    }

    private static void initConsoleReporter( MetricRegistry metrics, Properties properties )
    {
        ConsoleReporter.forRegistry( metrics )
                       .build()
                       .start( INDY_METRICS_REPORTER_GRPHITEREPORTER_DEFAULT_PERIOD, TimeUnit.SECONDS );
    }

    private static void initGraphiteReporterForSimpleMetric( MetricRegistry metrics, Properties properties )
    {
        final Graphite graphite = new Graphite( new InetSocketAddress(
                        properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME ),
                        Integer.parseInt( properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT ) ) ) );
        final GraphiteReporter reporter = GraphiteReporter.forRegistry( metrics )
                                                          .prefixedWith( properties.getProperty(
                                                                          INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX ) )
                                                          .convertRatesTo( TimeUnit.SECONDS )
                                                          .convertDurationsTo( TimeUnit.MILLISECONDS )
                                                          .filter( new SimpleMetricFilter() )
                                                          .build( graphite );
        String period = properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_SIMPLE_PERIOD );
        reporter.start( Integer.parseInt(
                        properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_SIMPLE_PERIOD ) ),
                        TimeUnit.SECONDS );
    }

    private static void initGraphiteReporterForJVMMetric( MetricRegistry metrics, Properties properties )
    {
        final Graphite graphite = new Graphite( new InetSocketAddress(
                        properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME ),
                        Integer.parseInt( properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT ) ) ) );
        final GraphiteReporter reporter = GraphiteReporter.forRegistry( metrics )
                                                          .prefixedWith( properties.getProperty(
                                                                          INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX ) )
                                                          .convertRatesTo( TimeUnit.SECONDS )
                                                          .convertDurationsTo( TimeUnit.MILLISECONDS )
                                                          .filter( new JVMMetricFilter() )
                                                          .build( graphite );
        reporter.start( Integer.parseInt( properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_JVM_PERIOD ) ),
                        TimeUnit.SECONDS );
    }

    private static void initGraphiteReporterForHealthCheckMetric( MetricRegistry metrics, Properties properties )
    {
        final Graphite graphite = new Graphite( new InetSocketAddress(
                        properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_HOSTNAME ),
                        Integer.parseInt( properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_PORT ) ) ) );
        final GraphiteReporter reporter = GraphiteReporter.forRegistry( metrics )
                                                          .prefixedWith( properties.getProperty(
                                                                          INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX ) )
                                                          .convertRatesTo( TimeUnit.SECONDS )
                                                          .convertDurationsTo( TimeUnit.MILLISECONDS )
                                                          .filter( new HealthCheckMetricFilter() )
                                                          .build( graphite );
        reporter.start( Integer.parseInt(
                        properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_HEALTHCHECK_PERIOD ) ),
                        TimeUnit.SECONDS );
    }

    private static void initZabbixReporport( MetricRegistry metrics, Properties properties )
    {
        logger.info( "call in report initZabbixReporport" );
        int port = 10051;
        String host = "10.8.64.39";
        try
        {

            String url = "https://zabbix.host.stage.eng.rdu2.redhat.com/zabbix/api_jsonrpc.php";
            ZabbixApi zabbixApi = new IndyZabbixApi( url );
            //            zabbixApi.init();
            IndyZabbixSender zabbixSender = IndyZabbixSender.create()
                                                            .zabbixApi( zabbixApi )
                                                            .zabbixHost( host )
                                                            .zabbixPort( String.valueOf( port ) )
                                                            .zabbixHostUrl( url )
                                                            .zabbixUserName( "nos" )
                                                            .zabbixUserPwd( "nos" )
                                                            .bCreateNotExistHost( false )
                                                            .bCreateNotExistHostGroup( false )
                                                            .bCreateNotExistZabbixSender( false )
                                                            .hostName( "dhcp-136-35.nay.redhat.com" )
                                                            .ip( "10.66.137.35" )
                                                            .build();
//            String apiVersion = zabbixApi.apiVersion();
//            System.err.println( "apiVersion:" + apiVersion );
//
//            boolean login = zabbixApi.login( "xiabai", "Mimashibxf@321" );
//            logger.info( "call in r zabbixApi.login =" + login );
//            IndyZabbixSender sender = new IndyZabbixSender();
//            sender.setHostGroup( "nos" ); org.commonjava.indy.metrics.zabbix.sender
//            sender.setZabbixSender( zabbixSender );
//            sender.setZabbixApi( zabbixApi );
//            sender.setHostName( "dhcp-137-35.nay.redhat.com" );
//            sender.setbCreateNotExistHostGroup( false );

            final IndyZabbixReporter reporter = IndyZabbixReporter.forRegistry( metrics )
                                                                  .prefix( properties.getProperty(
                                                                                  INDY_METRICS_REPORTER_GRPHITEREPORTER_PREFIX ) )
                                                                  .convertRatesTo( TimeUnit.SECONDS )
                                                                  .convertDurationsTo( TimeUnit.MILLISECONDS )
                                                                  .hostName( "dhcp-136-35.nay.redhat.com" )
                                                                  .filter( MetricFilter.ALL )
                                                                  .build( zabbixSender );
            logger.info( "call in report IndyZabbixReporter build" );

            reporter.start( Integer.parseInt(
                            properties.getProperty( INDY_METRICS_REPORTER_GRPHITEREPORTER_HEALTHCHECK_PERIOD ) ),
                            TimeUnit.SECONDS );
        }
        catch ( Throwable throwable )
        {
            throwable.printStackTrace();
            logger.info( "ini zabbix " + throwable.getMessage() );
        }
    }
}
