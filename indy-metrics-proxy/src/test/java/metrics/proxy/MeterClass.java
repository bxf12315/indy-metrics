package metrics.proxy;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import org.commonjava.indy.measure.annotation.IndyException;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.commonjava.indy.metrics.proxy.invocationhandler.IndyMeticsInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * Created by xiabai on 3/1/17.
 */
public interface MeterClass {

    @IndyMetrics(type = IndyMetrics.MetricsType.METER,c=MeterClass.class,name="testMeterRequest")
    @IndyException(type = IndyException.IndyExceptionType.METERHANDLER,c=MeterClass.class,name="testMeterRequest has exception")
    public void getMeter(boolean isException) throws Exception;

    public static MeterClass getTimerClass(MetricRegistry metricRegistry, ScheduledReporter reporter){
        MeterClass impl = new MeterClassImpl();
        IndyMeticsInvocationHandler<MeterClass> handler = new IndyMeticsInvocationHandler<MeterClass>(impl,metricRegistry,reporter);

        return (MeterClass) Proxy.newProxyInstance(MeterClass.class.getClassLoader(), new Class[]{MeterClass.class},handler);
    }
}
