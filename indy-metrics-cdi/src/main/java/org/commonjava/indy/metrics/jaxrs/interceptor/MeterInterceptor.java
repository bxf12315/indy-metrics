package org.commonjava.indy.metrics.jaxrs.interceptor;

import com.codahale.metrics.Meter;
import org.commonjava.indy.measure.IndyMetricsUtil;
import org.commonjava.indy.measure.annotation.IndyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Created by xiabai on 2/27/17.
 */
@Interceptor
@IndyMetrics(type = IndyMetrics.MetricsType.METER)
public class MeterInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MeterInterceptor.class);

    @Inject
    IndyMetricsUtil util;

    @AroundInvoke
    public Object operation(InvocationContext context) throws Exception {
        logger.info("call in MeterHandler.operation");
            Meter requests = util.getMeter(context.getMethod().getAnnotation(IndyMetrics.class));
            Object o = context.proceed();
            requests.mark();
            return o;
    }
}
