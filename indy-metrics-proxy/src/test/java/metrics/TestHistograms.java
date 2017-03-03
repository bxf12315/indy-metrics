package metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by xiabai on 2/24/17.
 */
public class TestHistograms {
    /**
     */
    private static final MetricRegistry metrics = new MetricRegistry();

    /**
     */
    private static ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).convertDurationsTo(TimeUnit.SECONDS)
            .convertRatesTo(TimeUnit.MILLISECONDS).formattedFor(Locale.US).build();

    private static final Timer requests = metrics.timer(name(TestTimers.class, "request"));

    /**
     */
    private static final Histogram randomNums = metrics.histogram(name(TestHistograms.class, "random"));

    public static void handleRequest(double random) {
        randomNums.update((int) (random*100));
    }

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
        Random rand = new Random();
        Random random = new Random();
        while(true){
            handleRequest(rand.nextDouble());
            handleRequest(random.nextInt(1000));
//            Thread.sleep(100);
        }
    }
}
