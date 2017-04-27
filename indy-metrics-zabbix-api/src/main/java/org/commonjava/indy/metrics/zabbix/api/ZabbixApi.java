package org.commonjava.indy.metrics.zabbix.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface ZabbixApi
{

    void init();

    void destroy();

    String apiVersion();

    JsonNode call( Request request );

    boolean login( String user, String password );

    boolean hostExists( String name );

    String hostCreate( String host, String groupId, String ip );

    boolean hostgroupExists( String name );

    String hostgroupCreate( String name );

    String createItem( String host, String item, String hostid, int valueType );

    String getItem( String host, String item, String hostCache );

    String getHost( String name );

    String getHostgroup( String name );
}
