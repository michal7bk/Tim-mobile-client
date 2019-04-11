package pl.michal.tim_client.utils;

import pl.michal.tim_client.domain.User;

public class Connection {

    private static final String ip = "10.9.110.149";
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
