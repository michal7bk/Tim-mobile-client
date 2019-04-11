package pl.michal.tim_client.coach.trainings;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ListView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.domain.User;
import pl.michal.tim_client.utils.ArrRequestWithToken;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoachTrainingViewModel extends ViewModel {

    private final String TAG = "CoachTrainingViewModel";
    private Coach coach;
    public ObservableField<ListView> listView = new ObservableField<>();
    private CoachesTrainingArrayAdapter trainingsArrayAdapter;
    private List<Training> allTrainings = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAcceptedTrainings(Coach coach, Context context) {
        //first call this fnc, when response getProposedTraining, then populateTable
        RequestQueue queue = Volley.newRequestQueue(context);
        String acceptedUrl = Connection.url + "/coaches/" + coach.getId() + "/accepted-trainings-list";
        Log.i(TAG, "Making request on : " + acceptedUrl);
        ArrRequestWithToken getAccepted = new ArrRequestWithToken(Request.Method.GET, acceptedUrl, null,
                response -> {
                    readTrainingsResponse(response);
                    getProposedTrainings(coach, context);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getAccepted);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getProposedTrainings(Coach coach, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String proposedUrl = Connection.url + "/coaches/" + coach.getId() + "/proposed-trainings-list";
        Log.i(TAG, "Making request on: " + proposedUrl);
        ArrRequestWithToken getProposed = new ArrRequestWithToken(Request.Method.GET, proposedUrl, null,
                response -> {
                    readTrainingsResponse(response);
                    populateTable(allTrainings, context);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getProposed);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateTable(List<Training> trainings, Context context) {
        trainingsArrayAdapter = new CoachesTrainingArrayAdapter(
                context, R.layout.list_item, trainings);

        Objects.requireNonNull(listView.get()).setItemsCanFocus(false);
        Objects.requireNonNull(listView.get()).setAdapter(trainingsArrayAdapter);
        Objects.requireNonNull(listView.get()).setOnItemClickListener(
                (parent, view, position, id) -> Log.i(TAG, "List Item Clicked: " + position));

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
    void init(User user, Context context, ListView list) {
        listView.set(list);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/coaches/" + user.getId();
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    coach = gson.fromJson(String.valueOf(response), Coach.class);
                    Log.i(TAG, "Read coach : " + coach);
                    getAcceptedTrainings(coach, context);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }

    public ObservableField<ListView> getListView() {
        return listView;
    }

}
