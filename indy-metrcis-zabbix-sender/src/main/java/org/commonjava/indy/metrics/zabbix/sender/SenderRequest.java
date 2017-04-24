package org.commonjava.indy.metrics.zabbix.sender;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
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
        byte[] jsonBytes = mapper.writeValueAsBytes( this );
//        byte[] jsonBytes = buildJSonString().getBytes();
        //mapper.writeValueAsBytes( this );
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

    public String buildJSonString()
    {
        StringBuilder dataObjectJSONString = new StringBuilder();
        for ( int i = 0; i < data.size(); i++ )
        {
            String value = data.get( i ).getValue();
            String host = data.get( i ).getHost();
            String item = data.get( i ).getKey();
            long clock =  data.get( i ).getClock();
            dataObjectJSONString.append( "{\n" + "\"host\":\"" + host + "\",\n"
                                         + "\"clock\":" +clock + ",\n"
                                                         + "\"key\":\"" + item + "\",\n"
                                                         + "\"value\":\"" + value.replace( "\\", "\\\\" ) + "\"}" );
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
        return "{" + "\"request\":\"sender data\",\n" + "\"data\":[\n" + dataObjectJSONString.toString() + "],"
                        +""+"\"clock\":" +clock + "\n"
                        +"}\n";
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

    //    @Override
    //    public String toString() {
    //        StringBuilder dataObjectJSONString = new StringBuilder();
    //        for ( int i = 0; i < data.size(); i++ )
    //        {
    //            dataObjectJSONString.append( "{\n" + "\"host\":\"" + data.get( i ).getHost() + "\",\n"
    //                                                         // + "\",\n"
    //                                                         + "\"key\":\"" + data.get( i ).getKey() + "\",\n"
    //                                                         + "\"value\":\"" + data.get( i )
    //                                                                                .getValue()
    //                                                                                .replace( "\\", "\\\\" ) + "\"}" );
    //            if ( i < data.size() - 1 )
    //            {
    //                dataObjectJSONString.append( "," );
    //            }
    //        }
    //logger.info( "{" + "\"request\":\"sender data\",\n" + "\"data\":[\n" + dataObjectJSONString.toString() + "]}\n" );
    //        return "{" + "\"request\":\"sender data\",\n" + "\"data\":[\n" + dataObjectJSONString.toString() + "]}\n";
    //    }

    public static void main( String[] args ) throws Exception
    {
        SenderRequest sr = new SenderRequest();
        sr.setClock( 1 );

        DataObject d = new DataObject();
        d.setClock( 10 );
        d.setHost( "a" );
        d.setKey( "b" );
        d.setValue( "100" );
        List<DataObject> data = new ArrayList<DataObject>();
        data.add( d );
        sr.setData( data );
        sr.setRequest( "reqeust" );

        ObjectMapper mapper = new ObjectMapper();
//        mapper.disable();
        mapper.setSerializationInclusion( JsonInclude.Include.NON_NULL);
//        mapper.disable( SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);

    byte[] jsonBytes = mapper.writeValueAsBytes( sr );
       /* byte[] jsonBytes11 = sr.buildJSonString().getBytes( "UTF-8" );
        System.out.println( sr.buildJSonString() );*/
        byte[] jsonBytes1 = mapper.writer( SerializationFeature.CLOSE_CLOSEABLE ).writeValueAsBytes( sr );
        byte[] jsonBytes2 = mapper.writer( SerializationFeature.EAGER_SERIALIZER_FETCH ).writeValueAsBytes( sr );
        byte[] jsonBytes3 = mapper.writer( SerializationFeature.FAIL_ON_EMPTY_BEANS ).writeValueAsBytes( sr );
        byte[] jsonBytes4 = mapper.writer( SerializationFeature.FAIL_ON_SELF_REFERENCES ).writeValueAsBytes( sr );
        byte[] jsonBytes5 = mapper.writer( SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS )
                                  .writeValueAsBytes( sr );
        byte[] jsonBytes6 = mapper.writer( SerializationFeature.FLUSH_AFTER_WRITE_VALUE ).writeValueAsBytes( sr );
        byte[] jsonBytes7 = mapper.writer( SerializationFeature.INDENT_OUTPUT ).writeValueAsBytes( sr );
        byte[] jsonBytes8 = mapper.writer( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS ).writeValueAsBytes( sr );
        byte[] jsonBytes9 = mapper.writer( SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID ).writeValueAsBytes( sr );

        StringBuilder sb = new StringBuilder();
        sb.append( jsonBytes1 );
        System.out.print( sb );
//
//        byte[] jsonBytes = sr.buildJSonString().getBytes();
//        System.out.print( jsonBytes );

    }

}

class ByteArraySerializer
                extends JsonSerializer<byte[]>
{

    @Override
    public void serialize( byte[] bytes, JsonGenerator jgen, SerializerProvider provider )
                    throws IOException, JsonProcessingException
    {
        jgen.writeStartArray();

        for ( byte b : bytes )
        {
            jgen.writeNumber( unsignedToBytes( b ) );
        }

        jgen.writeEndArray();

    }

    private static int unsignedToBytes( byte b )
    {
        return b & 0xFF;
    }

}
