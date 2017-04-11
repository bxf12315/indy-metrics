package org.commonjava.indy.metrics.sender;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.commonjava.indy.metrics.reporter.IndyZabbixReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiabai on 3/29/17.
 */
public class IndyZabbixSender
{
    private ZabbixSender sender;

    private static final Logger logger = LoggerFactory.getLogger( IndyZabbixSender.class );

    boolean bCreateNotExistHostGroup = true;

    boolean bCreateNotExistHost = true;

    boolean bCreateNotExistItem = true;

    boolean bCreateNotExistZabbixSender = true;

    ZabbixSender zabbixSender;

    ZabbixApi zabbixApi;

    String zabbixHostUrl;

    String hostGroup = "NOS";//// default host group

    String group = "NOS";

    long clock = 0l;

    String hostName;

    String ip;

    String zabbixUserName;

    String zabbixUserPwd;

    // name, hostGroupId
    Map<String, String> hostGroupCache = new ConcurrentHashMap<String, String>();

    // name, hostId
    Map<String, String> hostCache = new ConcurrentHashMap<String, String>();

    // name, itemId
    Map<String, String> itemCache = new ConcurrentHashMap<String, String>();

    private PoolingHttpClientConnectionManager connManager;

    private RequestConfig requestConfig;

    public static IndyZabbixSender.Builder create(){ return new IndyZabbixSender.Builder();}

    public static class Builder
    {
        boolean bCreateNotExistHostGroup = true;

        boolean bCreateNotExistHost = true;

        boolean bCreateNotExistItem = true;

        boolean bCreateNotExistZabbixSender = true;

        ZabbixSender zabbixSender;

        ZabbixApi zabbixApi;

        String zabbixHostUrl;

        String hostGroup = "NOS";//// default host group

        String group = "NOS";

        long clock = 0l;

        String hostName;

        String ip;

        String zabbixUserName;

        String zabbixUserPwd;

        public Builder bCreateNotExistHostGroup(boolean bCreateNotExistHostGroup)
        {
            this.bCreateNotExistHostGroup = bCreateNotExistHostGroup;
            return this;
        }

        public Builder bCreateNotExistHost(boolean bCreateNotExistHost)
        {
            this.bCreateNotExistHost = bCreateNotExistHost;
            return this;
        }

        public Builder bCreateNotExistItem(boolean bCreateNotExistItem)
        {
            this.bCreateNotExistItem = bCreateNotExistItem;
            return this;
        }

        public Builder bCreateNotExistZabbixSender(boolean bCreateNotExistZabbixSender)
        {
            this.bCreateNotExistZabbixSender = bCreateNotExistZabbixSender;
            return this;
        }

        public Builder zabbixSender(ZabbixSender zabbixSender)
        {
            this.zabbixSender = zabbixSender;
            return this;
        }

        public Builder zabbixApi(ZabbixApi zabbixApi)
        {
            this.zabbixApi = zabbixApi;
            return this;
        }

        public Builder zabbixHostUrl(String zabbixHostUrl)
        {
            this.zabbixHostUrl = zabbixHostUrl;
            return this;
        }

        public Builder hostGroup(String hostGroup)
        {
            this.hostGroup = hostGroup;
            return this;
        }

        public Builder group(String group)
        {
            this.group = group;
            return this;
        }
        public Builder ip(String ip)
        {
            this.ip = ip;
            return this;
        }

        public Builder zabbixUserName(String zabbixUserName)
        {
            this.zabbixUserName = zabbixUserName;
            return this;
        }

        public Builder clock(long clock)
        {
            this.clock = clock;
            return this;
        }

        public Builder zabbixUserPwd(String zabbixUserPwd)
        {
            this.zabbixUserPwd = zabbixUserPwd;
            return this;
        }

        public IndyZabbixSender build(){
            return new IndyZabbixSender(this.bCreateNotExistHostGroup,
                                        this.bCreateNotExistHost,
                                        this.bCreateNotExistItem ,
                                        this.bCreateNotExistZabbixSender ,
                                        this.zabbixSender ,
                                        this.zabbixApi,
                                        this.zabbixHostUrl,
                                        this.hostGroup,
                                        this.group ,
                                        this.clock ,
                                        this.hostName ,
                                        this.ip ,
                                        this.zabbixUserName ,
                                        this.zabbixUserPwd );
        }

    }
    public IndyZabbixSender()
    {
        requestConfig = RequestConfig.custom()
                                                   .setConnectTimeout( 5 * 1000 )
                                                   .setConnectionRequestTimeout( 5 * 1000 )
                                                   .setSocketTimeout( 5 * 1000 )
                                                   .build();
        connManager = new PoolingHttpClientConnectionManager();
    }

    public IndyZabbixSender( boolean bCreateNotExistHostGroup, boolean bCreateNotExistHost, boolean bCreateNotExistItem,
                             boolean bCreateNotExistZabbixSender, ZabbixSender zabbixSender, ZabbixApi zabbixApi,
                             String zabbixHostUrl, String hostGroup, String group, long clock, String hostName,
                             String ip, String zabbixUserName, String zabbixUserPwd )
    {

        this.bCreateNotExistHostGroup = bCreateNotExistHostGroup;
        this.bCreateNotExistHost = bCreateNotExistHost;
        this.bCreateNotExistItem = bCreateNotExistItem;
        this.bCreateNotExistZabbixSender = bCreateNotExistZabbixSender;
        this.zabbixSender = zabbixSender;
        this.zabbixApi = zabbixApi;
        this.zabbixHostUrl = zabbixHostUrl;
        this.hostGroup = hostGroup;
        this.group = group;
        this.clock = clock;
        this.hostName = hostName;
        this.ip = ip;
        this.zabbixUserName = zabbixUserName;
        this.zabbixUserPwd = zabbixUserPwd;
    }

    void checkHostGroup( String hostGroup )
    {
        if ( hostGroupCache.get( hostGroup ) == null )
        {
            this.zabbixApiInit();
            try
            {
                JSONObject filter = new JSONObject();
                filter.put( "name", new String[] { hostGroup } );
                Request getRequest = RequestBuilder.newBuilder()
                                                   .method( "hostgroup.get" )
                                                   .paramEntry( "filter", filter )
                                                   .build();
                JSONObject getResponse = zabbixApi.call( getRequest );
                JSONArray result = getResponse.getJSONArray( "result" );
                if ( !result.isEmpty() )
                { // host group exists.
                    String groupid = result.getJSONObject( 0 ).getString( "groupid" );
                    hostGroupCache.put( hostGroup, groupid );
                }
                else
                {// host group not exists, create it.
                    Request createRequest = RequestBuilder.newBuilder()
                                                          .method( "hostgroup.create" )
                                                          .paramEntry( "name", hostGroup )
                                                          .build();
                    JSONObject createResponse = zabbixApi.call( createRequest );
                    String hostGroupId =
                                    createResponse.getJSONObject( "result" ).getJSONArray( "groupids" ).getString( 0 );
                    hostGroupCache.put( hostGroup, hostGroupId );
                }
            }
            finally
            {
                this.destroy();
            }
        }
    }

    void checkHost( String host, String ip )
    {
        try
        {
            if ( hostCache.get( host ) == null )
            {
                this.zabbixApiInit();
                logger.info( "call in hostCache.get( host ) == null " + host + ":" + ip );
                JSONObject filter = new JSONObject();
                filter.put( "host", new String[] { host } );
                Request getRequest =
                                RequestBuilder.newBuilder().method( "host.get" ).paramEntry( "filter", filter ).build();
                JSONObject getResponse = zabbixApi.call( getRequest );
                JSONArray result = getResponse.getJSONArray( "result" );
                if ( !result.isEmpty() )
                { // host exists.
                    String hostid = result.getJSONObject( 0 ).getString( "hostid" );
                    hostCache.put( host, hostid );

                    logger.info( "!result.isEmpty() " + hostid );
                }
                else
                {// host not exists, create it.
                    JSONArray groups = new JSONArray();
                    JSONObject group = new JSONObject();
                    group.put( "groupid", hostGroupCache.get( hostGroup ) );
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

                    JSONObject interface1 = new JSONObject();
                    interface1.put( "type", 1 );
                    interface1.put( "main", 1 );
                    interface1.put( "useip", 1 );
                    interface1.put( "ip", ip );
                    interface1.put( "dns", "" );
                    interface1.put( "port", "10051" );

                    Request request = RequestBuilder.newBuilder()
                                                    .method( "host.create" )
                                                    .paramEntry( "host", host )
                                                    .paramEntry( "groups", groups )
                                                    .paramEntry( "interfaces", new Object[] { interface1 } )
                                                    .build();
                    JSONObject response = zabbixApi.call( request );
                    logger.info( "call IndyZabbixSender checkHost  zabbixApi.call( request )" + response );
                    String hostId = response.getJSONObject( "result" ).getJSONArray( "hostids" ).getString( 0 );
                    hostCache.put( host, hostId );
                }
            }
        }finally
        {
            this.destroy();
        }
    }

    private String itemCacheKey( String host, String item )
    {
        return host + ":" + item;
    }

    void checkItem( String host, String item )
    {

        try
        {
            if ( itemCache.get( itemCacheKey( host, item ) ) == null )
            {
                this.zabbixApiInit();
                JSONObject search = new JSONObject();
                search.put( "key_", item );
                Request getRequest = RequestBuilder.newBuilder()
                                                   .method( "item.get" )
                                                   .paramEntry( "hostids", hostCache.get( host ) )
                                                   .paramEntry( "search", search )
                                                   .build();
                JSONObject getResponse = zabbixApi.call( getRequest );
                JSONArray result = getResponse.getJSONArray( "result" );
                if ( result.isEmpty() )
                {
                    // create item
                    int type = 2; // trapper
                    int value_type = 0; // float
                    int delay = 30;
                    Request request = RequestBuilder.newBuilder()
                                                    .method( "item.create" )
                                                    .paramEntry( "name", item )
                                                    .paramEntry( "key_", item )
                                                    .paramEntry( "hostid", hostCache.get( host ) )
                                                    .paramEntry( "type", type )
                                                    .paramEntry( "value_type", value_type )
                                                    .paramEntry( "delay", delay )
                                                    .build();

                    JSONObject response = zabbixApi.call( request );
                    String itemId = response.getJSONObject( "result" ).getJSONArray( "itemids" ).getString( 0 );
                    itemCache.put( itemCacheKey( host, item ), itemId );
                }
                else
                {
                    // put into cache
                    itemCache.put( itemCacheKey( host, item ), result.getJSONObject( 0 ).getString( "itemid" ) );
                }
            }
        }catch ( Throwable throwable )
        {
            this.destroy();
        }
    }

    public SenderResult send( DataObject dataObject ) throws IOException
    {
        return this.send( dataObject, System.currentTimeMillis() / 1000L );
    }

    public SenderResult send( DataObject dataObject, long clock ) throws IOException
    {
        return this.send( Collections.singletonList( dataObject ), clock );
    }

    public SenderResult send( List<DataObject> dataObjectList ) throws IOException
    {
        return this.send( dataObjectList, System.currentTimeMillis() / 1000L );
    }

    /**
     *
     * @param dataObjectList
     * @param clock
     *            TimeUnit is SECONDS.
     * @return
     * @throws IOException
     */
    public SenderResult send( List<DataObject> dataObjectList, long clock ) throws IOException
    {
        logger.info( "call in IndyZabbixsend. send" );
        if ( bCreateNotExistHostGroup )
        {
            checkHostGroup( hostGroup );
        }
        if ( bCreateNotExistHost )
        {
            checkHost( hostName, ip );
        }

        if ( bCreateNotExistItem )
        {
            for ( DataObject object : dataObjectList )
            {
                String key = object.getKey();
                checkItem( hostName, key );
            }
        }

        try
        {
            SenderResult senderResult = zabbixSender.send( dataObjectList, clock );
            if ( !senderResult.success() )
            {
                logger.error( "send data to zabbix server error! senderResult:" + senderResult );
            }
            return senderResult;
        }
        catch ( IOException e )
        {
            logger.error( "send data to zabbix server error!", e );
        }
        return null;
    }

    public void destroy()
    {
        if (bCreateNotExistZabbixSender)
        {
            return;
        }
        if ( zabbixApi != null )
            zabbixApi.destroy();
    }

    private void zabbixApiInit()
    {
        if (bCreateNotExistZabbixSender)
        {
            return;
        }
        if ( this.zabbixHostUrl == null || "".equals( this.zabbixHostUrl ) )
        {
            throw new RuntimeException( "can not find Zabbix's Host" );
        }

        CloseableHttpClient httpclient = HttpClients.custom()
                                                    .setConnectionManager( connManager )
                                                    .setDefaultRequestConfig( requestConfig )
                                                    .build();
        zabbixApi = new DefaultZabbixApi( this.zabbixHostUrl, httpclient );

        zabbixApi.init();

        if ( this.zabbixUserName == null || "".equals( this.zabbixUserName ) || this.zabbixUserPwd == null || "".equals(
                        this.zabbixUserPwd ) )
        {
            throw new RuntimeException( "can not find Zabbix's username or password" );
        }
        boolean login = zabbixApi.login( this.zabbixUserName, this.zabbixUserPwd );

        logger.info( "User:" + this.zabbixUserName + " login is " + login );
    }

    public ZabbixSender getZabbixSender()
    {
        return zabbixSender;
    }

    public void setZabbixSender( ZabbixSender zabbixSender )
    {
        this.zabbixSender = zabbixSender;
    }

    public ZabbixApi getZabbixApi()
    {
        return zabbixApi;
    }

    public void setZabbixApi( ZabbixApi zabbixApi )
    {
        this.zabbixApi = zabbixApi;
    }

    public boolean isbCreateNotExistHost()
    {
        return bCreateNotExistHost;
    }

    public void setbCreateNotExistHost( boolean bCreateNotExistHost )
    {
        this.bCreateNotExistHost = bCreateNotExistHost;
    }

    public String getHostGroup()
    {
        return hostGroup;
    }

    public void setHostGroup( String hostGroup )
    {
        this.hostGroup = hostGroup;
    }

    public boolean isbCreateNotExistItem()
    {
        return bCreateNotExistItem;
    }

    public void setbCreateNotExistItem( boolean bCreateNotExistItem )
    {
        this.bCreateNotExistItem = bCreateNotExistItem;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public long getClock()
    {
        return clock;
    }

    public void setClock( long clock )
    {
        this.clock = clock;
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName( String hostName )
    {
        this.hostName = hostName;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp( String ip )
    {
        this.ip = ip;
    }

    public boolean isbCreateNotExistHostGroup()
    {
        return bCreateNotExistHostGroup;
    }

    public void setbCreateNotExistHostGroup( boolean bCreateNotExistHostGroup )
    {
        this.bCreateNotExistHostGroup = bCreateNotExistHostGroup;
    }

    public ZabbixSender getSender()
    {
        return sender;
    }

    public void setSender( ZabbixSender sender )
    {
        this.sender = sender;
    }

    public String getZabbixHostUrl()
    {
        return zabbixHostUrl;
    }

    public void setZabbixHostUrl( String zabbixHostUrl )
    {
        this.zabbixHostUrl = zabbixHostUrl;
    }

    public String getZabbixUserName()
    {
        return zabbixUserName;
    }

    public void setZabbixUserName( String zabbixUserName )
    {
        this.zabbixUserName = zabbixUserName;
    }

    public String getZabbixUserPwd()
    {
        return zabbixUserPwd;
    }

    public void setZabbixUserPwd( String zabbixUserPwd )
    {
        this.zabbixUserPwd = zabbixUserPwd;
    }

    public boolean isbCreateNotExistZabbixSender()
    {
        return bCreateNotExistZabbixSender;
    }

    public void setbCreateNotExistZabbixSender( boolean bCreateNotExistZabbixSender )
    {
        this.bCreateNotExistZabbixSender = bCreateNotExistZabbixSender;
    }
}
