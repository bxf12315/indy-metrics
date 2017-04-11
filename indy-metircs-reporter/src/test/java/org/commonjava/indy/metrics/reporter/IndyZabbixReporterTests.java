package org.commonjava.indy.metrics.reporter;

import com.codahale.metrics.MetricRegistry;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;
import org.commonjava.indy.metrics.sender.IndyZabbixSender;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiabai on 3/29/17.
 */
public class IndyZabbixReporterTests
{
    @Test
    public void initTest()
    {
        int port = 10051;
        String host = "10.66.137.35";

        ZabbixSender zabbixSender = new ZabbixSender( host, port );
        String url = "https://zabbix.host.stage.eng.rdu2.redhat.com/zabbix/api_jsonrpc.php";
        ZabbixApi zabbixApi = new DefaultZabbixApi( url );
        zabbixApi.init();

        String apiVersion = zabbixApi.apiVersion();
        System.err.println( "apiVersion:" + apiVersion );

        boolean login = zabbixApi.login( "xiabai", "Mimashibxf@321" );
        IndyZabbixSender sender = new IndyZabbixSender();
        sender.setHostGroup( "nos" );
        sender.setZabbixSender( new ZabbixSender( host, port ) );
        sender.setZabbixApi( zabbixApi );
        sender.setbCreateNotExistHostGroup( false );
        IndyZabbixReporter.forRegistry( new MetricRegistry() ).build( sender ).start( 1000, TimeUnit.SECONDS );

        IndyZabbixSender s = IndyZabbixSender.create()
                                             .bCreateNotExistZabbixSender( true )
                                             .zabbixHostUrl( "https://zabbix.host.stage.eng.rdu2.redhat.com/zabbix/api_jsonrpc.php" )
                                             .zabbixUserName( "xiabai" )
                                             .zabbixUserPwd( "Mimashibxf@321" )
                                             .zabbixSender( zabbixSender )
                                             .build();
    }
}
