package org.commonjava.indy.metrics.zabbix.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author hengyunabc
 *
 */
public class SenderRequest
{
    static final byte header[] = { 'Z', 'B', 'X', 'D', '\1' };

    String request = "sender data";

    private static final Logger logger = LoggerFactory.getLogger( SenderRequest.class );

    /**
     * TimeUnit is SECONDS.
     */
    long clock;

    List<DataObject> data;

    public byte[] toBytes() throws JsonProcessingException
    {
        // https://www.zabbix.org/wiki/Docs/protocols/zabbix_sender/2.0
        // https://www.zabbix.org/wiki/Docs/protocols/zabbix_sender/1.8/java_example
        //		JsonParser jsonParser = new JsonParser();

        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonBytes = buildJSonString(  ).getBytes();//mapper.writeValueAsBytes( this );
        //		byte[] a  = mapper.readValue( mapper.writeValueAsBytes(  ),byte[].class );
        //		byte[] jsonBytes = JSON.toJSONBytes(this);

        byte[] result = new byte[header.length + 4 + 4 + jsonBytes.length];

        System.arraycopy( header, 0, result, 0, header.length );

        result[header.length] = (byte) ( jsonBytes.length & 0xFF );
        result[header.length + 1] = (byte) ( ( jsonBytes.length >> 8 ) & 0x00FF );
        result[header.length + 2] = (byte) ( ( jsonBytes.length >> 16 ) & 0x0000FF );
        result[header.length + 3] = (byte) ( ( jsonBytes.length >> 24 ) & 0x000000FF );

        System.arraycopy( jsonBytes, 0, result, header.length + 4 + 4, jsonBytes.length );
        return result;
    }

    public String buildJSonString(  )
    {
        StringBuilder dataObjectJSONString = new StringBuilder();
        for ( int i = 0; i < data.size(); i++ )
        {
            dataObjectJSONString.append(
                            "{\n" + "\"host\":\"" + data.get( i ).getHost() + "\",\n"
                                           // + "\",\n"
                                            + "\"key\":\"" + data.get( i ).getKey() + "\",\n" + "\"value\":\""
                                            + data.get( i ).getValue().replace( "\\", "\\\\" ) + "\"}" );
            if ( i < data.size() - 1 )
            {
                dataObjectJSONString.append( "," );
            }
        }
//        for ( DataObject dataObject : data )
//        {
//            dataObjectJSONString.append( "{\n" + "\"host\":\"" + dataObject.getHost() + "\",\n" + "\"clock\":\""
//                                                         + dataObject.getClock() + "\",\n" + "\"key\":\""
//                                                         + dataObject.getKey() + "\",\n" + "\"value\":\""
//                                                         + dataObject.getValue().replace( "\\", "\\\\" ) + "\"}" );
//            dataObjectJSONString.append( "," );
//        }

        logger.info( "{" + "\"request\":\"sender data\",\n" + "\"data\":[\n" + dataObjectJSONString.toString()
                                     + "]}\n" );
        return "{" + "\"request\":\"sender data\",\n" + "\"data\":[\n" + dataObjectJSONString.toString() + "]}\n";
    }

    public String getRequest()
    {
        return request;
    }

    public void setRequest( String request )
    {
        this.request = request;
    }

    /**
     * TimeUnit is SECONDS.
     *
     * @return
     */
    public long getClock()
    {
        return clock;
    }

    /**
     * TimeUnit is SECONDS.
     *
     * @param clock
     */
    public void setClock( long clock )
    {
        this.clock = clock;
    }

    public List<DataObject> getData()
    {
        return data;
    }

    public void setData( List<DataObject> data )
    {
        this.data = data;
    }
}
