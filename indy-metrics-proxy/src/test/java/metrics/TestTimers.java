package metrics;

/**
 * Created by xiabai on 2/24/17.
 */

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 */
public class TestTimers {
    /**
     */
    private static final MetricRegistry metrics = new MetricRegistry();

    /**
     */
    private static ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();

    /**
     */
    private static final Timer requests = metrics.timer(name(TestTimers.class, "request"));

    public static void handleRequest(int sleep) {
        Timer.Context context = requests.time();
        try {
            //some operator
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            context.stop();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        reporter.start(3, TimeUnit.SECONDS);
        Random random = new Random();
        int i=0;
        while(true){
            handleRequest(random.nextInt(1000));
            i++;
            System.out.println("result================================="+i);
        }
    }

}
