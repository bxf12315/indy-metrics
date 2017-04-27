package org.commonjava.indy.metrics.zabbix.sender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.commonjava.indy.metrics.zabbix.api.IndyZabbixApi;
import org.commonjava.indy.metrics.zabbix.api.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiabai on 4/1/17.
 */
public class IndyZabbixSender
{
    private static final Logger logger = LoggerFactory.getLogger( IndyZabbixSender.class );

    ZabbixSender sender;

    boolean bCreateNotExistHostGroup = true;

    boolean bCreateNotExistHost = true;

    boolean bCreateNotExistItem = true;

    boolean bCreateNotExistZabbixSender = true;

    ZabbixApi zabbixApi;

    String zabbixHostUrl;

    String hostGroup = "NOS";//// default host group

    String group = "NOS";

    int connectTimeout = 3 * 1000;

    int socketTimeout = 3 * 1000;

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

    private static final String regEx = "^-?[0-9]+$";

    private static final Pattern pat = Pattern.compile( regEx );

    public static IndyZabbixSender.Builder create()
    {
        return new IndyZabbixSender.Builder();
    }

    public static class Builder
    {
        boolean bCreateNotExistHostGroup = true;

        boolean bCreateNotExistHost = true;

        boolean bCreateNotExistItem = true;

        boolean bCreateNotExistZabbixSender = true;

        ZabbixApi zabbixApi;

        String zabbixHostUrl;

        String hostGroup = "NOS";//// default host group

        String group = "NOS";

        long clock = 0l;

        String hostName;

        String ip;

        String zabbixUserName;

        String zabbixUserPwd;

        String zabbixHost;

        String zabbixPort;

        public Builder zabbixHost( String zabbixHost )
        {
            this.zabbixHost = zabbixHost;
            return this;
        }

        public Builder hostName( String hostName )
        {
            this.hostName = hostName;
            return this;
        }

        public Builder zabbixPort( String zabbixPort )
        {
            this.zabbixPort = zabbixPort;
            return this;
        }

        public Builder bCreateNotExistHostGroup( boolean bCreateNotExistHostGroup )
        {
            this.bCreateNotExistHostGroup = bCreateNotExistHostGroup;
            return this;
        }

        public Builder bCreateNotExistHost( boolean bCreateNotExistHost )
        {
            this.bCreateNotExistHost = bCreateNotExistHost;
            return this;
        }

        public Builder bCreateNotExistItem( boolean bCreateNotExistItem )
        {
            this.bCreateNotExistItem = bCreateNotExistItem;
            return this;
        }

        public Builder bCreateNotExistZabbixSender( boolean bCreateNotExistZabbixSender )
        {
            this.bCreateNotExistZabbixSender = bCreateNotExistZabbixSender;
            return this;
        }

        public Builder zabbixApi( ZabbixApi zabbixApi )
        {
            this.zabbixApi = zabbixApi;
            return this;
        }

        public Builder zabbixHostUrl( String zabbixHostUrl )
        {
            this.zabbixHostUrl = zabbixHostUrl;
            return this;
        }

        public Builder hostGroup( String hostGroup )
        {
            this.hostGroup = hostGroup;
            return this;
        }

        public Builder group( String group )
        {
            this.group = group;
            return this;
        }

        public Builder ip( String ip )
        {
            this.ip = ip;
            return this;
        }

        public Builder zabbixUserName( String zabbixUserName )
        {
            this.zabbixUserName = zabbixUserName;
            return this;
        }

        public Builder clock( long clock )
        {
            this.clock = clock;
            return this;
        }

        public Builder zabbixUserPwd( String zabbixUserPwd )
        {
            this.zabbixUserPwd = zabbixUserPwd;
            return this;
        }

        public IndyZabbixSender build()
        {
            return new IndyZabbixSender( this.bCreateNotExistHostGroup, this.bCreateNotExistHost,
                                         this.bCreateNotExistItem, this.bCreateNotExistZabbixSender, this.zabbixApi,
                                         this.zabbixHostUrl, this.hostGroup, this.group, this.clock, this.hostName,
                                         this.ip, this.zabbixUserName, this.zabbixUserPwd, this.zabbixHost,
                                         this.zabbixPort );
        }

    }

    public IndyZabbixSender( boolean bCreateNotExistHostGroup, boolean bCreateNotExistHost, boolean bCreateNotExistItem,
                             boolean bCreateNotExistZabbixSender, ZabbixApi zabbixApi, String zabbixHostUrl,
                             String hostGroup, String group, long clock, String hostName, String ip,
                             String zabbixUserName, String zabbixUserPwd, String zabbixHost, String zabbixPort )
    {

        this.bCreateNotExistHostGroup = bCreateNotExistHostGroup;
        this.bCreateNotExistHost = bCreateNotExistHost;
        this.bCreateNotExistItem = bCreateNotExistItem;
        this.bCreateNotExistZabbixSender = bCreateNotExistZabbixSender;
        this.zabbixApi = zabbixApi;
        this.zabbixHostUrl = zabbixHostUrl;
        this.hostGroup = hostGroup;
        this.group = group;
        this.clock = clock;
        this.hostName = hostName;
        this.ip = ip;
        this.zabbixUserName = zabbixUserName;
        this.zabbixUserPwd = zabbixUserPwd;
        this.sender = new ZabbixSender( zabbixHost, Integer.parseInt( zabbixPort ) );
    }

    String checkHostGroup( String hostGroup )
    {
        if ( hostGroupCache.get( hostGroup ) == null )
        {
            this.zabbixApiInit();
            try
            {
                String groupid = zabbixApi.getHostgroup( hostGroup );
                if ( groupid == null )
                {
                    zabbixApi.hostgroupCreate( hostGroup );
                    hostGroupCache.put( hostGroup, groupid );
                }
                hostGroupCache.put( hostGroup, groupid );
                return groupid;
            }
            finally
            {
                this.destroy();
            }
        }
        return null;
    }

    String checkHost( String host, String ip )
    {
        try
        {
            if ( hostCache.get( host ) == null )
            {
                this.zabbixApiInit();
                String hostid = zabbixApi.getHost( host );
                if ( hostid !=null )
                {
                    hostCache.put( host, hostid );

                }
                else
                {// host not exists, create it.

                    zabbixApi.hostCreate( host, hostGroupCache.get( hostGroup ), ip );
                    hostCache.put( host, hostid );
                }
                return hostid;
            }
        }
        finally
        {
            this.destroy();
        }
        return null;
    }

    private String itemCacheKey( String host, String item )
    {
        return host + ":" + item;
    }

    String checkItem( String host, String item, int valueType )
    {

        try
        {
            if ( itemCache.get( itemCacheKey( host, item ) ) == null )
            {
                this.zabbixApiInit();

                String itemid = zabbixApi.getItem( host, item, hostCache.get( host ) );
                if ( itemid ==null)
                {
                    zabbixApi.createItem( host, item, hostCache.get( host ), valueType );
                    itemCache.put( itemCacheKey( host, item ), itemid );
                }
                else
                {
                    // put into cache
                    itemCache.put( itemCacheKey( host, item ), itemid );
                }
                return itemid;
            }
        }
        catch ( Throwable throwable )
        {
            this.destroy();
        }

        return null;
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
                int vauleType = 0;
                Matcher mat = pat.matcher( object.getValue() );
                if ( !mat.find() )
                {
                    vauleType = 4;
                }
                checkItem( hostName, key, vauleType );
            }
        }

        try
        {
            SenderResult senderResult = sender.send( dataObjectList, clock );
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
        if ( bCreateNotExistZabbixSender )
        {
            return;
        }
        if ( zabbixApi != null )
            zabbixApi.destroy();
    }

    private void zabbixApiInit()
    {
        if ( bCreateNotExistZabbixSender )
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
        zabbixApi = new IndyZabbixApi( this.zabbixHostUrl, httpclient );

        zabbixApi.init();

        if ( this.zabbixUserName == null || "".equals( this.zabbixUserName ) || this.zabbixUserPwd == null || "".equals(
                        this.zabbixUserPwd ) )
        {
            throw new RuntimeException( "can not find Zabbix's username or password" );
        }
        boolean login = zabbixApi.login( this.zabbixUserName, this.zabbixUserPwd );

        logger.info( "User:" + this.zabbixUserName + " login is " + login );
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

    public static void main( String[] args )
    {

        System.setProperty("javax.net.ssl.trustStore", "/home/xiabai/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

        int port = 10051;
        String host = "10.8.64.39";
        String url = "https://zabbix.host.stage.eng.rdu2.redhat.com/zabbix/api_jsonrpc.php";
        IndyZabbixSender zabbixSender = IndyZabbixSender.create()
                                                        .zabbixHost( host )
                                                        .zabbixPort( String.valueOf( port ) )
                                                        .zabbixHostUrl( url )
                                                        .zabbixUserName( "nos" )
                                                        .zabbixUserPwd( "nos" )
                                                        .bCreateNotExistHost( false )
                                                        .bCreateNotExistHostGroup( false )
                                                        .bCreateNotExistZabbixSender( false )
                                                        .hostName( "dhcp-136-234.nay.redhat.com" )
                                                        .build();
        zabbixSender.checkHostGroup( "NOS" );
        zabbixSender.checkHost( "dhcp-136-234.nay.redhat.com", "10.66.136.234");
        zabbixSender.checkItem( "dhcp-136-234.nay.redhat.com","test.test.3",0 );
    }
}
