package org.commonjava.indy.metrics.zabbix.sender;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

        System.setProperty("javax.net.ssl.trustStore", "/home/xiabai/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");



    }

    @After
    public void down()
    {
    }
    @Test
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
        assertEquals(zabbixSender.checkHostGroup( "NOS" ),"57");
    }

    @Test
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
        assertEquals(zabbixSender.checkHostGroup( "NOS" ),"57");
        assertEquals( zabbixSender.checkHost( "dhcp-136-234.nay.redhat.com", "10.66.136.234"),"10765");
    }

    @Test
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
        assertEquals(zabbixSender.checkHostGroup( "NOS" ),"57");
        assertEquals( zabbixSender.checkHost( "dhcp-136-234.nay.redhat.com", "10.66.136.234"),"10765");
        assertEquals(zabbixSender.checkItem( "dhcp-136-234.nay.redhat.com","test.test.3",0 ),"83455");
    }
}
