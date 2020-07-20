package com.mulesoft.services.elk.pojos;


public class Config {
    private String name;
    private String index;
    private Host server;
    private String username;
    private String password;

    public Config(String name, String index, Host server, String username, String password) {
        this.name = name;
        this.index = index;
        this.server = server;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Host getServer() {
        return server;
    }

    public void setServer(Host server) {
        this.server = server;
    }

}
