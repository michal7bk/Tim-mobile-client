package pl.michal.tim_client;

import android.support.annotation.Nullable;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import pl.michal.tim_client.utils.Connection;

import java.util.HashMap;
import java.util.Map;

public class ArrRequestWithToken extends JsonArrayRequest {

    @Override
    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Connection.getAuthorizationToken());
        return headers;
    }

    public ArrRequestWithToken(String url, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public ArrRequestWithToken(int method, String url, @Nullable JSONArray jsonRequest, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }
}
