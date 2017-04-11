//package org.commonjava.indy.metrics.zabbix.reporter;
//
//import com.codahale.metrics.Counter;
//import com.codahale.metrics.Gauge;
//import com.codahale.metrics.Histogram;
//import com.codahale.metrics.Meter;
//import com.codahale.metrics.MetricFilter;
//import com.codahale.metrics.MetricRegistry;
//import com.codahale.metrics.ScheduledReporter;
//import com.codahale.metrics.Timer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.SortedMap;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by xiabai on 4/1/17.
// */
//public class IndyZabbixReporter extends ScheduledReporter
//{
//    private static final Logger logger = LoggerFactory.getLogger( IndyZabbixReporter.class );
//
//    private final String hostName;
//
//    private final String prefix;
//
//    private final String suffix;
//
//    public static Builder forRegistry( MetricRegistry registry )
//    {
//        return new Builder( registry );
//    }
//
//    public static class Builder
//    {
//
//        private final MetricRegistry registry;
//
//        private String name = "zabbix-reporter";
//
//        private TimeUnit rateUnit;
//
//        private TimeUnit durationUnit;
//
//        private MetricFilter filter;
//
//        private String hostName;
//
//        private String prefix = "";
//
//        private String suffix = "";
//
//        private String zabbixHostName;
//
//        public Builder( MetricRegistry registry )
//        {
//            this.registry = registry;
//
//            this.rateUnit = TimeUnit.SECONDS;
//            this.durationUnit = TimeUnit.MILLISECONDS;
//            this.filter = MetricFilter.ALL;
//
//        }
//
//        /**
//         * Convert rates to the given time unit.
//         *
//         * @param rateUnit
//         *            a unit of time
//         * @return {@code this}
//         */
//        public Builder convertRatesTo( TimeUnit rateUnit )
//        {
//            this.rateUnit = rateUnit;
//            return this;
//        }
//
//        /**
//         * Convert durations to the given time unit.
//         *
//         * @param durationUnit
//         *            a unit of time
//         * @return {@code this}
//         */
//        public Builder convertDurationsTo( TimeUnit durationUnit )
//        {
//            this.durationUnit = durationUnit;
//            return this;
//        }
//
//        /**
//         * Only report metrics which match the given filter.
//         *
//         * @param filter
//         *            a {@link MetricFilter}
//         * @return {@code this}
//         */
//        public Builder filter( MetricFilter filter )
//        {
//            this.filter = filter;
//            return this;
//        }
//
//        /**
//         * default register name is "zabbix-reporter".
//         *
//         * @param name
//         * @return
//         */
//        public Builder name( String name )
//        {
//            this.name = name;
//            return this;
//        }
//
//        public Builder hostName( String hostName )
//        {
//            this.hostName = hostName;
//            return this;
//        }
//
//        public Builder prefix( String prefix )
//        {
//            this.prefix = prefix;
//            return this;
//        }
//
//        public Builder suffix( String suffix )
//        {
//            this.suffix = suffix;
//            return this;
//        }
//
//        public Builder zabbixHostName( String zabbixHostName )
//        {
//            this.zabbixHostName = zabbixHostName;
//            return this;
//        }
//
//        /**
//         * Builds a {@link IndyZabbixReporter} with the given properties.
//         *
//         * @return a {@link IndyZabbixReporter}
//         */
//        public IndyZabbixReporter build( )
//        {
//            if ( hostName == null )
//            {
//                hostName = HostUtil.getHostName();
//                logger.info( name + " detect hostName: " + hostName );
//            }
//            return new IndyZabbixReporter( registry, name, rateUnit, durationUnit, filter，hostName,prefix, suffix );
//        }
//    }
//
//    private IndyZabbixReporter( MetricRegistry registry, String name, TimeUnit rateUnit, TimeUnit durationUnit,
//                                MetricFilter filter,  String hostName, String prefix,
//                                String suffix )
//    {
//        super( registry, name, filter, rateUnit, durationUnit );
//        this.hostName = hostName;
//        this.prefix = prefix;
//        this.suffix = suffix;
//    }
//    @Override
//    public void report( SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
//                        SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
//                        SortedMap<String, Timer> timers )
//    {
//
//    }
//}
