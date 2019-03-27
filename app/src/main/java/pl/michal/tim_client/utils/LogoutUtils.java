package pl.michal.tim_client.utils;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.user.User;

public class LogoutUtils {

    public static void setOffline(User user, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/user/set-offline";
        Log.i("LogoutUtils", "Making request on :" + url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("roles", user.getRoles());
            jsonBody.put("id", user.getId());
            jsonBody.put("name", user.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjRequestWithToken putRequest = new ObjRequestWithToken(Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("Set offline : ", response.toString());
                },
                error -> Log.e("Error. Response from setting offline : ", String.valueOf(error)));
        queue.add(putRequest);
    }

    public static void cleanUser() {
        Connection.getUser().setToken("");
        Connection.getUser().setActive(false);
        Connection.getUser().setUsername("");
        Connection.getUser().setId(0L);
    }
}
