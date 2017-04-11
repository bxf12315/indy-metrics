package org.commonjava.indy.metrics.zabbix.sender;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ZabbixApi
{

    void init();

    void destroy();

    String apiVersion();

    JsonNode call( Request request );

    boolean login( String user, String password );

    boolean hostExists( String name );

    String hostCreate( String host, String groupId );

    boolean hostgroupExists( String name );

    String hostgroupCreate( String name );

    String createItem( String host, String item, Map<String, String> hostCache );

    JsonNode getItem( String host, String item, Map<String, String> hostCache );
}
