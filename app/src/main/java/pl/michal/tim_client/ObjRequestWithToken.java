package pl.michal.tim_client;

import android.support.annotation.Nullable;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import pl.michal.tim_client.utils.Connection;

import java.util.HashMap;
import java.util.Map;

public class ObjRequestWithToken extends JsonObjectRequest {
    @Override
    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Connection.getAuthorizationToken());
        return headers;
    }

    public ObjRequestWithToken(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);


    }

    public ObjRequestWithToken(String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }
}
