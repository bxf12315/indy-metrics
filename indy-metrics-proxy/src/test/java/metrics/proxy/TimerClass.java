package metrics.proxy;

/**
 * Created by xiabai on 3/1/17.
 */

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.commonjava.indy.measure.annotation.Measure;
import org.commonjava.indy.measure.annotation.MetricNamed;
import org.commonjava.indy.metrics.proxy.invocationhandler.IndyMeticsInvocationHandler;

import java.lang.reflect.Proxy;

public interface TimerClass {

    @IndyMetrics(
            c=TimerClass.class,
            measure = @Measure( timers = @MetricNamed( name="testTimerRequest" )),
            exceptions = @Measure( meters= @MetricNamed( name="testTimerRequestException" ))
    )
    public void getTimer(boolean isException) throws Exception;

    public static TimerClass getTimerClass(MetricRegistry metricRegistry, ScheduledReporter reporter){
        TimerClass impl = new TimerClassImpl();
        IndyMeticsInvocationHandler<TimerClass> handler = new IndyMeticsInvocationHandler<TimerClass>(impl,metricRegistry,reporter);

        return (TimerClass) Proxy.newProxyInstance(TimerClass.class.getClassLoader(), new Class[]{TimerClass.class},handler);
    }
}
