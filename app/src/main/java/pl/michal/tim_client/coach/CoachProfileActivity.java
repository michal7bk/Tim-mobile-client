package pl.michal.tim_client.coach;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import pl.michal.tim_client.utils.ObjRequestWithToken;
import pl.michal.tim_client.R;
import pl.michal.tim_client.domain.Coach;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;

import java.time.LocalDateTime;

public class CoachProfileActivity extends AppCompatActivity {
    private final String TAG = "CoachProfileActivity";
    private Coach coach;

    @BindView(R.id.input_CoachNameWithSurname)
    TextView _nameWithSurname;
    @BindView(R.id.input_acceptedTrainings)
    TextView _acceptedTrainings;
    @BindView(R.id.input_proposedTrainings)
    TextView _proposedTrainings;
    @BindView(R.id.input_uniqueCustomers)
    TextView _uniqueCustomers;
    @BindView(R.id.input_CoachEmail)
    TextView _email;
    @BindView(R.id.input_CoachName)
    TextView _name;
    @BindView(R.id.input_CoachSurname)
    TextView _surname;
    @BindView(R.id.input_CoachRoles)
    TextView _roles;
    @BindView(R.id.button_CoachEdit)
    Button _buttonEdit;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_profile);
        ButterKnife.bind(this);
        matchCoachWithUser(Connection.getUser());
    }

    private void setNumberOfAcceptedTrainings(Coach coach) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/coaches/" + coach.getId() + "/accepted-trainings";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        _acceptedTrainings.setText(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response : ", String.valueOf(error))

        );
        queue.add(getRequest);
    }

    private void setNumberOfProposedTrainings(Coach coach) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/coaches/" + coach.getId() + "/proposed-trainings";
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        _proposedTrainings.setText(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on : " + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setNumberOfUniqueCoaches(Coach coach) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/coaches/" + coach.getId() + "/unique-customers";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        _uniqueCustomers.setText(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on" + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setUpValue(Coach coach) {

        _email.setText(coach.getEmail());
        _nameWithSurname.setText(coach.getName() + " " + coach.getSurname());
        _name.setText(coach.getName());
        _roles.setText("Coach");
        _surname.setText(coach.getSurname());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void matchCoachWithUser(User user) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/coaches/" + user.getId();
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .serializeNulls()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .create();
                    coach = gson.fromJson(String.valueOf(response), Coach.class);
                    Log.i(TAG, "Read coach : " + coach);
                    setUpValue(coach);
                    setNumberOfAcceptedTrainings(coach);
                    setNumberOfProposedTrainings(coach);
                    setNumberOfUniqueCoaches(coach);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }


}
