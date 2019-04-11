package pl.michal.tim_client.coach.calendar;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.domain.User;
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
public class CalendarViewModel extends ViewModel {

    private final String TAG = "CalendarViewModel";
    private Coach coach;
    private List<Training> allTrainings = new ArrayList<>();
    public GregorianCalendar cal_month, cal_month_copy;
    private CalendarAdapter calendarAdapter;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    Context context;
    ObservableField<String> tv_month = new ObservableField<>();
    GridView gridview;
    ImageButton previous;
    ImageButton next;


    private void getTrainings(Coach coach) {
        String acceptedUrl = Connection.url + "/coaches/" + coach.getId() + "/accepted-trainings-list";
        Log.i(TAG, "Making request on :" + acceptedUrl);
        RequestQueue queue = Volley.newRequestQueue(context);
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
                Log.e(TAG, "Error read training");
            }
        }
        if (allTrainings.size() == 0) {
            allTrainings.addAll(trainingList);
        } else {
            allTrainings.addAll(trainingList);
            populateCalendar(allTrainings);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void init(User user, Context context, GridView gridView, ImageButton ibPrevious, ImageButton ibNext) {
        this.gridview = gridView;
        this.previous = ibPrevious;
        this.next = ibNext;
        this.context = context;
        RequestQueue queue = Volley.newRequestQueue(context);
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

    private void populateCalendar(List<Training> trainings) {
        EventsCollection.date_collection_arr = new ArrayList<>();
        for (Training training : trainings) {
            EventsCollection.date_collection_arr.add(new EventsCollection(
                    training.getStartTime().format(dateFormatter),
                    training.getCustomer().toString(),
                    training.getInfo(),
                    training.getStartTime().format(timeFormatter),
                    training.getEndTime().format(timeFormatter),
                    training.isAccepted()
            ));
        }


//TODO try to settitle

//        getSupportActionBar().setTitle("Your calendar");
        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_month_copy = (GregorianCalendar) cal_month.clone();
        calendarAdapter = new CalendarAdapter((Activity) context, cal_month, EventsCollection.date_collection_arr);

//        tv_month. = findViewById(R.id.tv_month);
        String month_name = (String) android.text.format.DateFormat.format("MMMM yyyy", cal_month);
        tv_month.set(month_name);

        previous.setOnClickListener(v -> {
            if (cal_month.get(GregorianCalendar.MONTH) == Calendar.MAY && cal_month.get(GregorianCalendar.YEAR) == 2017) {
                //cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
            } else {
                setPreviousMonth();
                refreshCalendar();
            }
        });
        next.setOnClickListener(v -> {
            if (cal_month.get(GregorianCalendar.MONTH) == Calendar.JUNE && cal_month.get(GregorianCalendar.YEAR) == 2018) {
                //cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
            } else {
                setNextMonth();
                refreshCalendar();
            }
        });

        gridview.setAdapter(calendarAdapter);
        gridview.setOnItemClickListener(
                (parent, v, position, id) -> {
                    String selectedGridDate = CalendarAdapter.day_string.get(position);
                    ((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, (Activity) context);
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
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        String month_name = (String) android.text.format.DateFormat.format("MMMM yyyy", cal_month);
        tv_month.set(month_name);
    }

    public ObservableField<String> getTv_month() {
        return tv_month;
    }
}
