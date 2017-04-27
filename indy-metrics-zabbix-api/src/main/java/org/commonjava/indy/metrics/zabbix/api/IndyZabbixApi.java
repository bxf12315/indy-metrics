package org.commonjava.indy.metrics.zabbix.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by xiabai on 3/31/17.
 */
public class IndyZabbixApi
                implements ZabbixApi
{
    private static final Logger logger = LoggerFactory.getLogger( IndyZabbixApi.class );

    private CloseableHttpClient httpClient;

    private URI uri;

    private volatile String auth;

    public IndyZabbixApi( String url )
    {
        try
        {
            uri = new URI( url.trim() );
        }
        catch ( URISyntaxException e )
        {
            throw new RuntimeException( "url invalid", e );
        }
    }

    public IndyZabbixApi( URI uri )
    {
        this.uri = uri;
    }

    public IndyZabbixApi( String url, CloseableHttpClient httpClient )
    {
        this( url );
        this.httpClient = httpClient;
    }

    public IndyZabbixApi( URI uri, CloseableHttpClient httpClient )
    {
        this( uri );
        this.httpClient = httpClient;
    }

    @Override
    public void init()
    {
        if ( httpClient == null )
        {
            httpClient = HttpClients.custom().build();
        }
    }

    @Override
    public void destroy()
    {
        if ( httpClient != null )
        {
            try
            {
                httpClient.close();
            }
            catch ( Exception e )
            {
                logger.error( "close httpclient error!", e );
            }
        }
    }

    @Override
    public boolean login( String user, String password )
    {
        this.auth = null;
        Request request = RequestBuilder.newBuilder()
                                        .paramEntry( "user", user )
                                        .paramEntry( "password", password )
                                        .method( "user.login" )
                                        .build();
        JsonNode response = call( request );
        String auth = response.get( "result" ).asText();
        if ( auth != null && !auth.isEmpty() )
        {
            this.auth = auth;
            return true;
        }
        return false;
    }

    @Override
    public String apiVersion()
    {
        Request request = RequestBuilder.newBuilder().method( "apiinfo.version" ).build();
        JsonNode response = call( request );
        return response.get( "result" ).asText();
    }

    public boolean hostExists( String name )
    {
        Request request = RequestBuilder.newBuilder().method( "host.exists" ).paramEntry( "host", name ).build();
        JsonNode response = call( request );
        return response.get( "result" ).asBoolean();
    }

    /**
     *
     * @param name
     * @return hostid
     */
    public String getHost( String name )
    {
        ObjectMapper mapper = new ObjectMapper(  );
        ObjectNode jsonNode = mapper.createObjectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        arrayNode.add( name );
        jsonNode.put( "host", arrayNode);
        Request request = RequestBuilder.newBuilder().method( "host.get" ).paramEntry( "filter", jsonNode ).build();
        JsonNode response = call( request );
        if (response.get( "result" ).isNull() || response.get( "result" ).get( 0 ) ==null)
        {
            return null;
        }
        return response.get( "result" ).get( 0 ).get( "hostid" ).asText();
    }

    /**
     *
     * @param host
     * @param groupId
     * @param ip
     * @return hostid
     */
    public String hostCreate( String host, String groupId, String ip )
    {
        // host not exists, create it.
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode groups = mapper.createArrayNode();
        ObjectNode group = mapper.createObjectNode();
        group.put( "groupid", groupId );
        groups.add( group );

        // "interfaces": [
        // {
        // "type": 1,
        // "main": 1,
        // "useip": 1,
        // "ip": "192.168.3.1",
        // "dns": "",
        // "port": "10050"
        // }
        // ],

        ObjectNode interface1 = mapper.createObjectNode();
        //        JSONObject interface1 = new JSONObject();
        interface1.put( "type", 1 );
        interface1.put( "main", 1 );
        interface1.put( "useip", 1 );
        interface1.put( "ip", ip );
        interface1.put( "dns", "" );
        interface1.put( "port", "10050" );

        Request request = RequestBuilder.newBuilder()
                                        .method( "host.create" )
                                        .paramEntry( "host", host )
                                        .paramEntry( "groups", groups )
                                        .paramEntry( "interfaces", new Object[] { interface1 } )
                                        .build();
        JsonNode response = call( request );
        logger.info( "call IndyZabbixSender checkHost  zabbixApi.call( request )" + response );
        return response.get( "result" ).get( "hostids" ).get( 0 ).asText();
    }

    public boolean hostgroupExists( String name )
    {
        Request request = RequestBuilder.newBuilder().method( "hostgroup.exists" ).paramEntry( "name", name ).build();
        JsonNode response = call( request );
        return response.get( "result" ).asBoolean();
    }

    /**
     *
     * @param name
     * @return groupId
     */
    public String getHostgroup( String name )
    {
        ObjectMapper mapper = new ObjectMapper(  );
        ObjectNode jsonNode = mapper.createObjectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        arrayNode.add( name );
        jsonNode.put( "name", arrayNode);
        Request request = RequestBuilder.newBuilder().method( "hostgroup.get" ).paramEntry( "filter", jsonNode ).build();
        JsonNode response = call( request );
        if (response.get( "result" ).isNull() || response.get( "result" ).get( 0 ) ==null)
        {
            return null;
        }
        return response.get( "result" ).get( 0 ).get( "groupid" ).asText();
    }

    /**
     *
     * @param name
     * @return groupId
     */
    public String hostgroupCreate( String name )
    {
        Request request = RequestBuilder.newBuilder().method( "hostgroup.create" ).paramEntry( "name", name ).build();
        JsonNode response = call( request );
        return response.get( "result" ).get( "groupids" ).get( 0 ).toString();
    }

    @Override
    public JsonNode call( Request request )
    {
        if ( request.getAuth() == null )
        {
            request.setAuth( this.auth );
        }

        try
        {
            ObjectMapper mapper = new ObjectMapper();
            HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder.post()
                                                                                      .setUri( uri )
                                                                                      .addHeader( "Content-Type",
                                                                                                  "application/json" )
                                                                                      .setEntity( new StringEntity(
                                                                                                      mapper.writeValueAsString(
                                                                                                                      request ),
                                                                                                      ContentType.APPLICATION_JSON ) )
                                                                                      .build();
            CloseableHttpResponse response = httpClient.execute( httpRequest );
            HttpEntity entity = response.getEntity();
            return mapper.readTree( entity.getContent() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "DefaultZabbixApi call exception!", e );
        }
    }

    public String createItem( String host, String item, String hostid ,int valueType)
    {
        // create item
        int type = 2; // trapper
        int value_type = valueType; // float
        int delay = 30;
        Request request = RequestBuilder.newBuilder()
                                        .method( "item.create" )
                                        .paramEntry( "name", item )
                                        .paramEntry( "key_", item )
                                        .paramEntry( "hostid", hostid )
                                        .paramEntry( "type", type )
                                        .paramEntry( "value_type", value_type )
                                        .paramEntry( "delay", delay )
                                        .build();

        JsonNode response = call( request );
        return response.get( "result" ).findValues( "itemids" ).get( 0 ).asText();
    }

    public String getItem( String host, String item, String hostid )
    {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode groups = mapper.createArrayNode();
        ObjectNode search = mapper.createObjectNode();
        search.put( "key_", item );
        Request getRequest = RequestBuilder.newBuilder()
                                           .method( "item.get" )
                                           .paramEntry( "hostids", hostid )
                                           .paramEntry( "search", search )
                                           .build();
        JsonNode response = call( getRequest );
        if (response.get( "result" ).isNull() || response.get( "result" ).get( 0 ) ==null)
        {
            return null;
        }
        return response.get( "result" ).get( 0 ).get( "itemid" ).asText();
    }
}
