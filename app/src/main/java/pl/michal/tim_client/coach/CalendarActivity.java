package pl.michal.tim_client.coach;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
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
import pl.michal.tim_client.coach.calendar.EventsCollection;
import pl.michal.tim_client.coach.calendar.HwAdapter;
import pl.michal.tim_client.domain.Coach;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.ArrRequestWithToken;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarActivity extends AppCompatActivity {
    private final String TAG = "CalendarActivity";
    Coach coach;
    private List<Training> allTrainings = new ArrayList<>();
    public GregorianCalendar cal_month, cal_month_copy;
    private HwAdapter hwAdapter;
    private TextView tv_month;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        matchCoachWithUser(Connection.getUser());
    }

    private void getTrainings(Coach coach){
        String acceptedUrl = Connection.url + "/coaches/" + coach.getId() + "/accepted-trainings-list";
        Log.i(TAG, "Making request on :" + acceptedUrl);
        RequestQueue queue = Volley.newRequestQueue(this);
        ArrRequestWithToken getAccepted = new ArrRequestWithToken(Request.Method.GET, acceptedUrl, null,
                    this::readTrainingsResponse,
                    error -> Log.d("Error.Response when reading trainings: ", String.valueOf(error))
            );
            String proposedUrl = Connection.url + "/coaches/" + coach.getId() + "/proposed-trainings-list";
            ArrRequestWithToken getProposed = new ArrRequestWithToken(Request.Method.GET, proposedUrl, null,
                    this::readTrainingsResponse,
                    error -> Log.d("Error.Response when reading trainings: ", String.valueOf(error))
            );
            queue.add(getAccepted);
            queue.add(getProposed);
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readTrainingsResponse(JSONArray response) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                .create();
        List<Training> trainingList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject training = response.getJSONObject(i);
                trainingList.add(gson.fromJson(String.valueOf(training), Training.class));
            } catch (JSONException e) {
                Log.e(TAG,"Error read training");
            }
        }
        if (allTrainings.size() ==0){
            allTrainings.addAll(trainingList);
        }else{
            allTrainings.addAll(trainingList);
            populateCalendar(allTrainings);
        }
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
                    getTrainings(coach);
                    Log.i(TAG, "Read coach : " + coach);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void populateCalendar(List<Training> trainings){
        EventsCollection.date_collection_arr = new ArrayList<>();
        for (Training training: trainings){
            EventsCollection.date_collection_arr.add(new EventsCollection(
                training.getStartTime().format(dateFormatter),
                training.getCustomer().toString(),
                training.getInfo(),
                training.getStartTime().format(timeFormatter),
                training.getEndTime().format(timeFormatter),
                training.isAccepted()
                ));
        }



        getSupportActionBar().setTitle("Your calendar");
        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_month_copy = (GregorianCalendar) cal_month.clone();
        hwAdapter = new HwAdapter(this, cal_month, EventsCollection.date_collection_arr);

        tv_month = findViewById(R.id.tv_month);
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));


        ImageButton previous = findViewById(R.id.ib_prev);
        previous.setOnClickListener(v -> {
            if (cal_month.get(GregorianCalendar.MONTH) == Calendar.MAY && cal_month.get(GregorianCalendar.YEAR) == 2017) {
                //cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
                Toast.makeText(CalendarActivity.this, "Event Detail is available for current session only.", Toast.LENGTH_SHORT).show();
            } else {
                setPreviousMonth();
                refreshCalendar();
            }


        });
        ImageButton next = findViewById(R.id.Ib_next);
        next.setOnClickListener(v -> {
            if (cal_month.get(GregorianCalendar.MONTH) == Calendar.JUNE && cal_month.get(GregorianCalendar.YEAR) == 2018) {
                //cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
                Toast.makeText(CalendarActivity.this, "Event Detail is available for current session only.", Toast.LENGTH_SHORT).show();
            } else {
                setNextMonth();
                refreshCalendar();
            }
        });
        GridView gridview = findViewById(R.id.gv_calendar);
        gridview.setAdapter(hwAdapter);
        gridview.setOnItemClickListener(
                (parent, v, position, id) -> {
                    String selectedGridDate = HwAdapter.day_string.get(position);
                    ((HwAdapter) parent.getAdapter()).getPositionList(selectedGridDate, CalendarActivity.this);
                });
    }


    private void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    private void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH, cal_month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    private void refreshCalendar() {
        hwAdapter.refreshDays();
        hwAdapter.notifyDataSetChanged();
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
    }


}
