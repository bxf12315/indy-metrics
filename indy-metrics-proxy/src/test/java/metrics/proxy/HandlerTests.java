package metrics.proxy;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Created by xiabai on 3/1/17.
 */
public class HandlerTests {

    static MetricRegistry metricRegistry;
    static ScheduledReporter reporter;

    @BeforeClass
    public static void begin() {
        metricRegistry = new MetricRegistry();
        reporter = ConsoleReporter.forRegistry(metricRegistry).build();
    }


    @Test
    public void testTimerHandler() throws Exception {
        TimerClass timerClass = TimerClass.getTimerClass(metricRegistry, reporter);
        for (int i = 0; i < 20; i++) {
            timerClass.getTimer(false);
        }
        for (int i = 0; i < 20; i++) {
            try {
                timerClass.getTimer(true);
            } catch (Throwable throwable) {
                //do noting
            }

        }
    }

    @Test
    public void testMeterHandler() throws Exception {
        MeterClass meterClass = MeterClass.getTimerClass(metricRegistry, reporter);

        for (int i = 0; i < 20; i++) {
            meterClass.getMeter(false);
        }
        for (int i = 0; i < 20; i++) {
            try {
                meterClass.getMeter(true);
            } catch (Throwable throwable) {
                //do noting
            }
        }
    }
}
