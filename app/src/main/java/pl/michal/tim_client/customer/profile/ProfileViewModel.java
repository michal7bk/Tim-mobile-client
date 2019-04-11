package pl.michal.tim_client.customer.profile;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import pl.michal.tim_client.R;
import pl.michal.tim_client.customer.model.Customer;
import pl.michal.tim_client.domain.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProfileViewModel extends ViewModel {

    private final String TAG = "ProfileViewModel";

    Customer customer;

    public ObservableField<String> nameWithSurname =  new ObservableField<>();
    public ObservableField<String> completedTrainings =  new ObservableField<>();
    public ObservableField<String> plannedTrainings =  new ObservableField<>();
    public ObservableField<String> uniqueCoaches =  new ObservableField<>();
    public ObservableField<String> email =  new ObservableField<>();
    public ObservableField<String> name =  new ObservableField<>();
    public ObservableField<String> surname =  new ObservableField<>();
    public ObservableField<String> roles =  new ObservableField<>();

    @SuppressLint("SetTextI18n")
    private void setUpValue(Customer customer) {

        nameWithSurname.set(customer.getName() + " " + customer.getSurname());
        email.set(customer.getEmail());
        name.set(customer.getName());
        surname.set(customer.getSurname());
        roles.set(String.valueOf(R.string.Customer));
    }

    private void setNumberOfCompletedTrainings(Customer customer, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/customers/" + customer.getId() + "/planned-trainings";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        completedTrainings.set(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response", String.valueOf(error))

        );
        queue.add(getRequest);
    }

    private void setNumberOfPlannedTrainings(Customer customer, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/customers/" + customer.getId() + "/completed-trainings";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        plannedTrainings.set(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on " + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setNumberOfUniqueCoaches(Customer customer, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/customers/" + customer.getId() + "/unique-coaches";
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        uniqueCoaches.set(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on  " + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void init(User user, Context context) {
        String url = Connection.url + "/customers/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.i(TAG, "Making request on : " + url);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .serializeNulls()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .create();
                    customer = gson.fromJson(String.valueOf(response), Customer.class);
                    Log.i(TAG, "Read coach : " + response);
                    setUpValue(customer);
                    setNumberOfPlannedTrainings(customer, context);
                    setNumberOfCompletedTrainings(customer, context);
                    setNumberOfUniqueCoaches(customer, context);
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

    public ObservableField<String> getNameWithSurname() {
        return nameWithSurname;
    }

    public ObservableField<String> getCompletedTrainings() {
        return completedTrainings;
    }

    public ObservableField<String> getPlannedTrainings() {
        return plannedTrainings;
    }

    public ObservableField<String> getUniqueCoaches() {
        return uniqueCoaches;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public ObservableField<String> getName() {
        return name;
    }

    public ObservableField<String> getSurname() {
        return surname;
    }

    public ObservableField<String> getRoles() {
        return roles;
    }
}
