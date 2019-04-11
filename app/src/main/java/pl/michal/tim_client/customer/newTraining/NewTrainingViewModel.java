package pl.michal.tim_client.customer.newTraining;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.customer.MenuCustomerActivity;
import pl.michal.tim_client.customer.model.Customer;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewTrainingViewModel extends ViewModel {
    private final String TAG = "NewTrainingViewModel";
    private Context context;

    private Customer customer;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private LocalDateTime startDate = LocalDateTime.now().plusHours(23);
    private LocalDateTime endDate = LocalDateTime.now().plusHours(24);

     Button _setEndDate;
     Button _setStartDate;
     Button _proposeTraining;
     Spinner _coachesSpinner;
     TextView _textStartDate;
     TextView _textEndDate;
     EditText _inputInfo;

    private List<Coach> coaches = new ArrayList<>();


    void propopseNewTraining() {
        if (!validate()) {
            Toast.makeText(context, "Data isn't valid", Toast.LENGTH_LONG).show();
        } else {

            Coach coach = (Coach) _coachesSpinner.getSelectedItem();
            String info = String.valueOf(_inputInfo.getText());
            requestProposeNewTraining(new Training(customer, coach, startDate, endDate, info));
        }
    }

    private void requestProposeNewTraining(Training training) {
        Gson gson = new Gson();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/trainings/propose";
        Log.i(TAG, "Making request on propose new Training :  " + url);
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("customerId", gson.toJson(training.getCustomer().getId()));
            jsonBody.put("coachId", gson.toJson(training.getCoach().getId()));
            jsonBody.put("startTime", training.getStartTime());
            jsonBody.put("endTime", training.getEndTime());
            jsonBody.put("info", training.getInfo());
            Log.i(TAG, "Request Body :  " + jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjRequestWithToken postRequest = new ObjRequestWithToken(Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(context, R.string.ToastTrainingProposed, Toast.LENGTH_LONG).show();
                    Log.i(TAG, "New training was proposed " + response);
                    Intent intent = new Intent(context, MenuCustomerActivity.class);
                    context.startActivity(intent);
                },
                error -> {
                    if (error.networkResponse.statusCode == 409) {
                        Toast.makeText(context, context.getString(R.string.ToastTrainingConflict) +
                                " please change date", Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG + "Error.Response on " + url, String.valueOf(error));
                }
        );
        queue.add(postRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void init(User user, Context context) {
        this.context = context;
        _textEndDate.setText(endDate.format(formatter));
        _textStartDate.setText(startDate.format(formatter));
        String url = Connection.url + "/customers/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.i(TAG, "Making request on  : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .serializeNulls()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .create();
                    Log.i(TAG, String.valueOf(response));
                    customer = gson.fromJson(String.valueOf(response), Customer.class);
                    requestGetCoaches();
                    Log.i(TAG, "Read customer : " + customer);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void openDataPicker(boolean isStartDate) {
        //isStartDate? true -> set startdate /false-> set enddate
        final View dialogView = View.inflate(context, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

            int monthNumber = (datePicker.getMonth() + 1) % 12;
            LocalDateTime localDateTime = LocalDateTime.of(
                    datePicker.getYear(), monthNumber, datePicker.getDayOfMonth(),
                    timePicker.getHour(), timePicker.getMinute());
            alertDialog.dismiss();

            if (isStartDate) {

                startDate = localDateTime;
                _textStartDate.setText(localDateTime.format(formatter));
            } else {
                endDate = localDateTime;
                _textEndDate.setText(localDateTime.format(formatter));
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }


    private void populateSpinner() {
        _coachesSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, coaches));
    }

    private boolean validate() {
        boolean valid = true;
        String info = String.valueOf(_inputInfo.getText());
        if (info.length() < 3) {
            _inputInfo.setError(context.getString(R.string.ErrorEmptyDetails));
            valid = false;
        }

        if (endDate.isBefore(startDate)) {
            _textStartDate.setError(context.getString(R.string.ErrorEndAfterStart));
            Toast.makeText(context, R.string.ToastEndAfterBefore, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void requestGetCoaches() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/coaches";
        Log.i(TAG, "Making request on : " + url);
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject coachJson = response.getJSONObject(i);
                            Coach coach = gson.fromJson(coachJson.toString(), Coach.class);
                            coaches.add(new Coach(coach.id, coach.name, coach.surname, coach.email));
                        }
                        Log.i(TAG, "coaches" + coaches);
                        populateSpinner();
                    } catch (JSONException e) {
                        Log.e(TAG, "Cannot parse response from : " + url);
                    }
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Connection.getAuthorizationToken());
                return headers;
            }
        };
        queue.add(getRequest);
    }

    void addListenerOnSpinnerItemSelection() {
        _coachesSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void initComponent(Button setEndDate, Button setStartDate, Button proposeTraining,
                              Spinner coachesSpinner, EditText inputInfo, TextView textStartDate, TextView textEndDate) {
        this._setEndDate = setEndDate;
        this._setStartDate = setStartDate;
        this._proposeTraining = proposeTraining;
        this._coachesSpinner = coachesSpinner;
        this._inputInfo = inputInfo;
        this._textStartDate = textStartDate;
        this._textEndDate = textEndDate;

    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

}
