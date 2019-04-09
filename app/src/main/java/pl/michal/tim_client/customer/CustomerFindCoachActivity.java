package pl.michal.tim_client.customer;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.customer.model.Customer;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.ArrRequestWithToken;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerFindCoachActivity extends AppCompatActivity {
    Customer customer;
    private final String TAG = "CustomerFindCoachActivity";
    private CoachesArrayAdapter coachesArrayAdapter;
    private ListView listView;
    private List<Coach> coaches = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_coaches_list);
        ButterKnife.bind(this);
        matchCustomerWithUser(Connection.getUser());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCoaches() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String acceptedUrl = Connection.url + "/coaches";
        Log.i(TAG, "Making request on : " + acceptedUrl);
        ArrRequestWithToken getAccepted = new ArrRequestWithToken(Request.Method.GET, acceptedUrl, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .serializeNulls()
                            .create();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject coach = response.getJSONObject(i);
                            coaches.add(gson.fromJson(String.valueOf(coach), Coach.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    populateTable(coaches);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getAccepted);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTable(List<Coach> coaches) {
        coachesArrayAdapter = new CoachesArrayAdapter(
                CustomerFindCoachActivity.this, R.layout.list_item_coaches_list, coaches);
        listView = findViewById(R.id.listView);
        listView.setAdapter(coachesArrayAdapter);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(
                (parent, view, position, id) -> Log.i(TAG, "List Item Clicked: " + position));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void matchCustomerWithUser(User user) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/customers/" + user.getId();
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .create();
                    customer = gson.fromJson(String.valueOf(response), Customer.class);
                    Log.i(TAG, "Read customer : " + customer);
                    getCoaches();
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }
}
