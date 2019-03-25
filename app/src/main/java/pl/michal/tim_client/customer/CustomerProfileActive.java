package pl.michal.tim_client.customer;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import pl.michal.tim_client.R;
import pl.michal.tim_client.ObjRequestWithToken;
import pl.michal.tim_client.domain.Customer;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomerProfileActive extends AppCompatActivity {
    private static final String TAG = "CustomerProfileActive";
    private Customer customer;

    //TODO EDITTEXT maybe for update date
    @BindView(R.id.input_nameWithSurname)
    TextView _nameWithSurname;
    @BindView(R.id.input_completedTrainings)
    TextView _completedTrainings;
    @BindView(R.id.input_plannedTrainings)
    TextView _plannedTrainings;
    @BindView(R.id.input_uniqueCoaches)
    TextView _uniqueCoaches;
    @BindView(R.id.input_email)
    TextView _email;
    @BindView(R.id.input_name)
    TextView _name;
    @BindView(R.id.input_surname)
    TextView _surname;
    @BindView(R.id.input_roles)
    TextView _roles;
    @BindView(R.id.button_edit)
    Button _buttonEdit;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile);
        ButterKnife.bind(this);
        matchCustomerWithUser(Connection.getUser());
    }


    private void setUpValue(Customer customer) {

        _nameWithSurname.setText(customer.getName() + " " + customer.getSurname());
        _email.setText(customer.getEmail());
        _name.setText(customer.getName());
        _surname.setText(customer.getSurname());
        _roles.setText("Customer");
    }

    private void setNumberOfCompletedTrainings(Customer customer) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/customers/" + customer.getId() + "/planned-trainings";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        _completedTrainings.setText(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response", String.valueOf(error))

        );
        queue.add(getRequest);
    }

    private void setNumberOfPlannedTrainings(Customer customer) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/customers/" + customer.getId() + "/completed-trainings";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        _plannedTrainings.setText(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on " + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setNumberOfUniqueCoaches(Customer customer) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/customers/" + customer.getId() + "/unique-coaches";
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        _uniqueCoaches.setText(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on  " + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void matchCustomerWithUser(User user) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/customers/" + user.getId();
        Log.i(TAG, "Making request on : " + url);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .serializeNulls()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .create();
                    customer = gson.fromJson(String.valueOf(response), Customer.class);
                    Log.i(TAG, String.valueOf(response));
                    setUpValue(customer);
                    setNumberOfPlannedTrainings(customer);
                    setNumberOfCompletedTrainings(customer);
                    setNumberOfUniqueCoaches(customer);
                    Log.i(TAG, "Read customer : " + customer);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", Connection.getAuthorizationToken());
                return headers;
            }
        };
        queue.add(getRequest);
    }
}
