package pl.michal.tim_client.coach;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.domain.Coach;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.ArrRequestWithToken;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CoachTrainingsActivity extends AppCompatActivity {
    private final String TAG = "CoachTrainingsActivity";
    private Coach coach;
    private ListView listView;
    private CoachesTrainingArrayAdapter trainingsArrayAdapter;
    private List<Training> allTrainings = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_trainings);
        matchCoachWithUser(Connection.getUser());
    }

    //TODO DOES NOT WORKING <- Both button has same customer<- one should have accept second decline
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAcceptedTrainings(Coach coach) {
        //first call this fnc, when response getProposedTraining, then populateTable
        RequestQueue queue = Volley.newRequestQueue(this);
        String acceptedUrl = Connection.url + "/coaches/" + coach.getId() + "/accepted-trainings-list";
        Log.i(TAG, "Making request on : " + acceptedUrl);
        ArrRequestWithToken getAccepted = new ArrRequestWithToken(Request.Method.GET, acceptedUrl, null,
                response -> {
                    readTrainingsResponse(response);
                    getProposedTrainings(coach);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getAccepted);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getProposedTrainings(Coach coach) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String proposedUrl = Connection.url + "/coaches/" + coach.getId() + "/proposed-trainings-list";
        Log.i(TAG, "Making request on: " + proposedUrl);
        ArrRequestWithToken getProposed = new ArrRequestWithToken(Request.Method.GET, proposedUrl, null,
                response -> {
                    readTrainingsResponse(response);
                    populateTable(allTrainings);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getProposed);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTable(List<Training> trainings) {
        trainingsArrayAdapter = new CoachesTrainingArrayAdapter(
                CoachTrainingsActivity.this, R.layout.list_item, trainings);
        listView = findViewById(R.id.listView);
        listView.setItemsCanFocus(false);
        listView.setAdapter(trainingsArrayAdapter);
        listView.setOnItemClickListener(
                (parent, view, position, id) -> Toast.makeText(
                        CoachTrainingsActivity.this, "List Item Clicked: " + position, Toast.LENGTH_LONG).show());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readTrainingsResponse(JSONArray response) {
        List<Training> trainings = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                .create();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject training = response.getJSONObject(i);
                trainings.add(gson.fromJson(String.valueOf(training), Training.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        allTrainings.addAll(trainings);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void matchCoachWithUser(User user) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/coaches/" + user.getId();
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    coach = gson.fromJson(String.valueOf(response), Coach.class);
                    Log.i(TAG, "Read coach : " + coach);
                    getAcceptedTrainings(coach);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }
}
