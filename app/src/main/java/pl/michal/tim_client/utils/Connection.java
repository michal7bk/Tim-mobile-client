package pl.michal.tim_client.utils;

import pl.michal.tim_client.user.User;

public class Connection {

    private static final String ip = "192.168.43.116";
    private static final String port = "8080";
    public static final String url = "http://" + ip + ":" + port;
    private static User user;


    public static String getUrl() {
        return url;
    }

    public static void setUser(User user) {
        Connection.user = user;
    }
    public static User getUser(){
        return user;
    }

    public static String getAuthorizationToken() {
        return Connection.user.getToken();
    }
}
