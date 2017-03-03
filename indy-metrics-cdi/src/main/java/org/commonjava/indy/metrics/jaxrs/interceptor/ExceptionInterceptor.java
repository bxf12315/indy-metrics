package org.commonjava.indy.metrics.jaxrs.interceptor;

import com.codahale.metrics.Meter;
import org.commonjava.indy.measure.IndyMetricsUtil;
import org.commonjava.indy.measure.annotation.IndyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Created by xiabai on 2/28/17.
 */
@IndyException(type = IndyException.IndyExceptionType.METERHANDLER)
@Interceptor
public class ExceptionInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);

    @Inject
    IndyMetricsUtil util;

    @AroundInvoke
    public Object operation(InvocationContext context) throws Exception {
        logger.info("call in ExceptionInterceptor.operation");
        Object o = null;
        try{
            o = context.proceed();
        }catch (Exception e){
            Meter meter = util.getExceptionMeter(context.getMethod().getAnnotation(IndyException.class));
            meter.mark();
            throw e;
        }
        return o;
    }
}
