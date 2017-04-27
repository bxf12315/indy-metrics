package org.commonjava.indy.metrics.zabbix.sender;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by xiabai on 4/27/17.
 */
public class ZabbixSenderTest
{
    @Before
    public void init()
    {
        //Set SSL certificate
        //        System.setProperty("javax.net.ssl.trustStore", "***");
        //        System.setProperty("javax.net.ssl.trustStorePassword", "***");

        System.setProperty( "javax.net.ssl.trustStore", "/home/xiabai/cacerts" );
        System.setProperty( "javax.net.ssl.trustStorePassword", "changeit" );

    }

    @After
    public void down()
    {
    }

    @Test
    @Ignore
    public void checkHostgroupTest()
    {
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
        assertEquals( zabbixSender.checkHostGroup( "NOS" ), "57" );
    }

    @Test
    @Ignore
    public void checkHostHostTest()
    {
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
        assertEquals( zabbixSender.checkHostGroup( "NOS" ), "57" );
        assertEquals( zabbixSender.checkHost( "dhcp-136-234.nay.redhat.com", "10.66.136.234" ), "10765" );
    }

    @Test
    @Ignore
    public void checkHostItemTest()
    {
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
        assertEquals( zabbixSender.checkHostGroup( "NOS" ), "57" );
        assertEquals( zabbixSender.checkHost( "dhcp-136-234.nay.redhat.com", "10.66.136.234" ), "10765" );
        assertEquals( zabbixSender.checkItem( "dhcp-136-234.nay.redhat.com", "test.test.3", 0 ), "83455" );
    }

    @Test
    @Ignore
    public void SendDataTest()
    {
        int port = 10051;
        String host = "10.8.64.39";
        String url = "https://zabbix.host.stage.eng.rdu2.redhat.com/zabbix/api_jsonrpc.php";
        IndyZabbixSender zabbixSender = IndyZabbixSender.create()
                                                        .zabbixHost( host )
                                                        .zabbixPort( String.valueOf( port ) )
                                                        .zabbixHostUrl( url )
                                                        .zabbixUserName( "nos" )
                                                        .zabbixUserPwd( "nos" )
                                                        .bCreateNotExistZabbixSender( false )
                                                        .hostName( "dhcp-136-234.nay.redhat.com" )
                                                        .build();

        DataObject d = new DataObject();
        d.setClock( System.currentTimeMillis() / 1000 );
        d.setHost( "dhcp-136-234.nay.redhat.com" );
        d.setKey( "test.test3" );
        d.setValue( "100" );
        try
        {
            final SenderResult send = zabbixSender.send( d );
            assertEquals( send.getProcessed(), send.getTotal() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            org.junit.Assert.assertFalse( true );
        }
    }
}
