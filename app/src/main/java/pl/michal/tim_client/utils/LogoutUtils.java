package pl.michal.tim_client.utils;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.domain.User;

import java.util.HashMap;
import java.util.Map;

public class LogoutUtils {
    private final static String TAG = "LogoutUtils";

    public static void setOffline(User user, Context context) {
        String token = user.getToken();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/users/" + Connection.getUser().getId()+ "?status=false";
        Log.i(TAG, "Making request on :" + url);
        ObjRequestWithToken putRequest = new ObjRequestWithToken(Request.Method.PUT, url, null,
                response -> Log.d("LogoutUtils", ": status set offline "),
                error -> Log.e(TAG, "error message : " + error)){
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        queue.add(putRequest);
    }


    public static void cleanUser() {
        Connection.getUser().setToken("");
        Connection.getUser().setActive(false);
        Connection.getUser().setUsername("");
        Connection.getUser().setId(0L);
    }
}
