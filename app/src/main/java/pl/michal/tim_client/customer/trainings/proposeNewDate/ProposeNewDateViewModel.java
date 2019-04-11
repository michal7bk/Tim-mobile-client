package pl.michal.tim_client.customer.trainings.proposeNewDate;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.customer.trainings.TrainingViewModel;
import pl.michal.tim_client.customer.trainings.TrainingsActivity;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static android.app.Activity.RESULT_OK;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProposeNewDateViewModel extends ViewModel {
    private final String TAG = "ProposeNewDateViewModel";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private LocalDateTime startDate = LocalDateTime.now().plusHours(23);
    private LocalDateTime endDate = LocalDateTime.now().plusHours(24);
    Context context;
    Button _newStartDate;
    Button _newEndDate;
    Button _proposeNewDate;
    TextView _textStartDate;
    TextView _textEndDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    void openDataPicker(boolean isStartDate) {
        //isStartDate? true -> set startdate /false-> set enddate
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        final View dialogView = View.inflate(context, R.layout.date_time_picker, null);

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
            int monthNumber = (datePicker.getMonth() + 1) % 12;

            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

            LocalDateTime localDateTime = LocalDateTime.of(
                    datePicker.getYear(), monthNumber, datePicker.getDayOfMonth(),
                    timePicker.getHour(), timePicker.getMinute());
            alertDialog.dismiss();
            if (isStartDate) {
                _textStartDate.setText(localDateTime.format(formatter));
                startDate = localDateTime;
            } else {
                _textEndDate.setText(localDateTime.format(formatter));
                endDate = localDateTime;
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    void requestProposeNewDateTraining(Long trainingId) {
        if (!validate()) {
            Toast.makeText(context, R.string.ToastDataNotValidate, Toast.LENGTH_LONG).show();
        } else {

            RequestQueue queue = Volley.newRequestQueue(context);
            String url = Connection.url + "/trainings/" + trainingId + "/propose";
            Log.i(TAG, "Making request on propose new Training date :  " + url);
            JSONObject newDateJson = new JSONObject();
            try {
                newDateJson.put("startTime", startDate);
                newDateJson.put("endTime", endDate);
                Log.i(TAG, "Request Body to update date:  " + newDateJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ObjRequestWithToken postRequest = new ObjRequestWithToken(Request.Method.PUT, url, newDateJson,
                    response -> {
                        Toast.makeText(context, R.string.ToastNewDataProposed, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "New date for training was proposed " + response);
                    },
                    error -> {
                        if (error.networkResponse.statusCode == 409) {
                            Toast.makeText(context, R.string.ToastTrainingConflict, Toast.LENGTH_LONG).show();
                        }
                        Log.d(TAG + "Error.Response on " + url, String.valueOf(error));
                    }
            );
            queue.add(postRequest);
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (endDate.isBefore(startDate)) {
            Toast.makeText(context, R.string.ToastEndAfterBefore, Toast.LENGTH_SHORT).show();
            _textStartDate.setError(context.getString(R.string.ErrorEndAfterStart));
            valid = false;
        }
        return valid;
    }

    public void initComponent(Button newStartDate, Button newEndDate, TextView textEndDate, TextView textStartDate) {
        this._newEndDate = newEndDate;
        this._newStartDate = newStartDate;
        this._textEndDate = textEndDate;
        this._textStartDate = textStartDate;


    }

    public void init(Context context) {
        this.context = context;
    }
}
