package org.commonjava.indy.metrics.sigar;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiabai on 6/2/17.
 */
public class OSTest
{
    public static void main( String[] args ) throws InterruptedException
    {
        String javaPath = System.getProperty( "java.library.path" ).toString();
        javaPath = javaPath + ":" + OSTest.class.getClassLoader().getResource( "lib" ).getPath();
        System.setProperty( "java.library.path", javaPath );
        MetricRegistry registry = new MetricRegistry();
        registry.register( "system", new OSMetricsSet() );
        registry.register( "system", new FilesystemMetricsSet() );
        registry.register( "system", new NetworkMetricsSet() );

        final ConsoleReporter reporter = ConsoleReporter.forRegistry( registry )
                                                        .convertRatesTo( TimeUnit.SECONDS )
                                                        .convertDurationsTo( TimeUnit.MILLISECONDS )
                                                        .build();
        reporter.start( 1, TimeUnit.SECONDS );
        Thread.sleep( 10000 );
    }
}

