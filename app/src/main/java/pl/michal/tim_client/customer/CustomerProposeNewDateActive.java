package pl.michal.tim_client.customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CustomerProposeNewDateActive extends AppCompatActivity {

    private final String TAG = "CustomerProposeNewDateActive";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BindView(R.id.btn_set_new_start_date)
    Button _newStartDate;
    @BindView(R.id.btn_set_new_end_date)
    Button _newEndDate;
    @BindView(R.id.btn_propose_new_date)
    Button _proposeNewDate;
    @BindView(R.id.text_startDate)
    TextView _textStartDate;
    @BindView(R.id.text_endDate)
    TextView _textEndDate;

    private LocalDateTime startDate = LocalDateTime.now().plusHours(23);
    private LocalDateTime endDate = LocalDateTime.now().plusHours(24);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_new_date);
        ButterKnife.bind(this);
        setTitle("Propose new Data for your training");
        Intent intent = getIntent();
        Long id = intent.getLongExtra("idTraining", 0);
        _proposeNewDate.setOnClickListener(view -> requestProposeNewDateTraining(id));
        _newStartDate.setOnClickListener(x -> openDataPicker(true));
        _newEndDate.setOnClickListener(x -> openDataPicker(false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openDataPicker(boolean isStartDate) {
        //isStartDate? true -> set startdate /false-> set enddate
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {

            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);

            LocalDateTime localDateTime = LocalDateTime.of(
                    datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
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

    private void requestProposeNewDateTraining(Long trainingId) {
        RequestQueue queue = Volley.newRequestQueue(this);
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
        //TODO valid date eg. if enttime is before starttime
        ObjRequestWithToken postRequest = new ObjRequestWithToken(Request.Method.PUT, url, newDateJson,
                response -> {
                    Toast.makeText(CustomerProposeNewDateActive.this, "New date for training was proposed", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "New date for training was proposed " + response);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                },
                error -> {
                    if (error.networkResponse.statusCode == 409) {
                        Toast.makeText(CustomerProposeNewDateActive.this, "Your propopsed new training date conflict with existing coach training," +
                                " please change date", Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG + "Error.Response on " + url, String.valueOf(error));
                }
        );
        queue.add(postRequest);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("message_return", "This data is returned when user click back menu in target activity.");
        setResult(RESULT_OK, intent);
        finish();
    }


}