package metrics.proxy;

/**
 * Created by xiabai on 3/1/17.
 */
public class MeterClassImpl implements MeterClass{

    public void getMeter(boolean isException) throws Exception{
        Thread.sleep(300);
        if(isException){
            throw new Exception("has a exception");
        }
    }
}
