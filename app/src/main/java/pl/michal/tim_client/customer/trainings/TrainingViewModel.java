package pl.michal.tim_client.customer.trainings;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.customer.model.Customer;
import pl.michal.tim_client.customer.trainings.proposeNewDate.ProposeNewDateActivity;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.domain.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingViewModel extends ViewModel {


    private final String TAG = "TrainingViewModel";
    private Context context;
    private List<Training> trainings = new ArrayList<>();

    TableLayout _tableLayout;
    private Customer customer;
    ProgressDialog mProgressBar ;

    @RequiresApi(api = Build.VERSION_CODES.O)
     void init(User user,Context context) {
        this.context = context;
        String url = Connection.url + "/customers/" + user.getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.i(TAG, "Making request on:" + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .serializeNulls()
                            .create();
                    Log.i(TAG, String.valueOf(response));
                    customer = gson.fromJson(String.valueOf(response), Customer.class);
                    Log.i(TAG, "Read customer : " + customer);
                    getTrainings(customer);
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTrainings(Customer customer) {
        List<Training> result = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/customers/" + customer.getId() + "/trainings";
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JsonParser parser = new JsonParser();
                        Gson gson = new Gson();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject training = response.getJSONObject(i);
                            JsonElement customerElement = parser.parse(training.getString("customer"));
                            Customer customerResult = gson.fromJson(customerElement, Customer.class);
                            JsonElement coachElement = parser.parse(training.getString("coach"));
                            Coach coachResult = gson.fromJson(coachElement, Coach.class);
                            LocalDateTime startTime = LocalDateTime.parse(training.getString("startTime"));
                            LocalDateTime endTime = LocalDateTime.parse(training.getString("endTime"));
                            String info = training.getString("info");
                            boolean accepted = training.getBoolean("accepted");
                            Long id = training.getLong("id");
                            result.add(new Training(id, customerResult, coachResult, startTime, endTime, info, accepted));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    trainings = result;
                    startLoadData();
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


    private void startLoadData() {
        mProgressBar= new ProgressDialog(context);
        mProgressBar.setCancelable(false);
        mProgressBar.setMessage("Fetching Trainings..");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        new LoadDataTask().execute(0);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadData() {

        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize = 0;
        int mediumTextSize;
        textSize = (int) context.getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) context.getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) context.getResources().getDimension(R.dimen.font_size_medium);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        int rows = trainings.size();
        TextView textSpacer;

        _tableLayout.removeAllViews();

        // -1 means heading row
        for (int i = -1; i < rows; i++) {
            Training row = null;
            if (i > -1)
                row = trainings.get(i);
            else {
                textSpacer = new TextView(context);
                textSpacer.setText("");
            }
            // data columns
            final TextView tv = new TextView(context);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));



                    tv.setGravity(Gravity.LEFT);

            tv.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv.setText(R.string.textAccepted);
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            } else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(String.valueOf(row.accepted));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            final TextView tv2 = new TextView(context);
            if (i == -1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv2.setGravity(Gravity.LEFT);

            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText(R.string.textCoach);
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setText(row.coach.toString());
            }

            final LinearLayout layCustomer = new LinearLayout(context);
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            layCustomer.setPadding(0, 10, 0, 10);
            layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tv3 = new TextView(context);
            if (i == -1) {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setPadding(5, 5, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            } else {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setPadding(5, 0, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            tv3.setGravity(Gravity.TOP);

            if (i == -1) {
                tv3.setText(R.string.textDate);
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setTextColor(Color.parseColor("#000000"));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv3.setText(row.startTime.format(formatter));
            }
            layCustomer.addView(tv3);

            if (i > -1) {
                final TextView tv3b = new TextView(context);
                tv3b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv3b.setGravity(Gravity.RIGHT);
                tv3b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv3b.setPadding(50, 1, 50, 5);
                tv3b.setTextColor(Color.parseColor("#aaaaaa"));
                tv3b.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3b.setText(row.endTime.format(formatter));
                layCustomer.addView(tv3b);
            }

            final LinearLayout layAmounts = new LinearLayout(context);
            layAmounts.setOrientation(LinearLayout.VERTICAL);
            layAmounts.setGravity(Gravity.RIGHT);
            layAmounts.setPadding(0, 10, 0, 10);
            layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));


            final TextView tv4 = new TextView(context);
            if (i == -1) {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv4.setPadding(5, 5, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setPadding(5, 0, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            tv4.setGravity(Gravity.RIGHT);
            if (i == -1) {
                tv4.setText(R.string.textInformation);
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
            } else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                tv4.setText(row.info);
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            layAmounts.addView(tv4);
            // add table row
            final TableRow tr = new TableRow(context);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0, 0, 0, 0);
            tr.setLayoutParams(trParams);
            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(layCustomer);
            tr.addView(layAmounts);
            tr.setTag(i);
            if (i > -1) {
                tr.setOnClickListener(v -> {
                    TableRow tr1 = (TableRow) v;
                    Training training = trainings.get((Integer) tr1.getTag());
                    Long idTraining = training.getId();
                    Intent intent = new Intent(context, ProposeNewDateActivity.class);
                    intent.putExtra("idTraining", idTraining);
                    context.startActivity(intent);
                });
            }
            _tableLayout.addView(tr, trParams);

            if (i > -1) {
                // add separator row
                final TableRow trSep = new TableRow(context);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(context);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 4;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                _tableLayout.addView(trSep, trParamsSep);
            }
        }
    }

    void initComponent(TableLayout tableLayout) {
        this._tableLayout=tableLayout;
    }

    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "Task Completed.";
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            mProgressBar.hide();
            loadData();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }


}






