package com.mulesoft.services.elk.pojos;

import java.net.MalformedURLException;
import java.net.URL;

public class Host {

    private String protocol;
    private String host;
    private Integer port;

    public Host(String hostString) throws MalformedURLException {
        URL hostUrl = new URL(hostString);
        protocol = hostUrl.getProtocol();
        host = hostUrl.getHost();
        port = hostUrl.getPort();
    }

    public String toString() {
        return protocol+"://"+host+':'+port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
