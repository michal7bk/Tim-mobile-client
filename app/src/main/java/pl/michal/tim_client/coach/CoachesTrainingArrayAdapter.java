package pl.michal.tim_client.coach;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import pl.michal.tim_client.R;
import pl.michal.tim_client.domain.Training;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CoachesTrainingArrayAdapter extends ArrayAdapter<Training> {
    private final String TAG = "CoachesTrainingArrayAdapter";


    private Context context;
    private int layoutResourceId;
    private List<Training> trainings;
    private final String CANCEL = "CANCEL";
    private final String ACCEPT = "ACCEPT";

    CoachesTrainingArrayAdapter(Context context, int layoutResourceId,
                                List<Training> studs) {
        super(context, layoutResourceId, studs);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.trainings = studs;
    }

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        TrainingWrapper trainingWrapper;
        if (item == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(layoutResourceId, parent, false);
            trainingWrapper = new TrainingWrapper();
            trainingWrapper.customer = item.findViewById(R.id.textName);
            trainingWrapper.info = item.findViewById(R.id.textAge);
            trainingWrapper.time = item.findViewById(R.id.textAddr);
            trainingWrapper.edit = item.findViewById(R.id.btnEdit);
            trainingWrapper.delete = item.findViewById(R.id.btnDelete);
            item.setTag(trainingWrapper);
        } else {
            trainingWrapper = (TrainingWrapper) item.getTag();
        }
        Training training = trainings.get(position);
        trainingWrapper.customer.setText(training.getCustomer().toString());
        trainingWrapper.info.setText(training.getInfo());
        trainingWrapper.time.setText("from : " + training.getStartTime().format(formatter) + " to: " + training.getEndTime().format(formatter));
        Log.i(TAG, training.toString());
        if (training.isAccepted()) {
            trainingWrapper.edit.setText(CANCEL);
        } else if (!training.isAccepted()) {
            trainingWrapper.edit.setText(ACCEPT);
        }

        trainingWrapper.edit.setOnClickListener(v -> {
            if (training.isAccepted()) {
                cancelTraining(training.id);
                Toast.makeText(context, R.string.ToastTrainingCanceled, Toast.LENGTH_SHORT).show();
                trainingWrapper.edit.setText(CANCEL);
            } else if (!training.isAccepted()) {
                acceptTraining(training.id);
                Toast.makeText(context, R.string.ToastTrainingAccepted, Toast.LENGTH_SHORT).show();
                trainingWrapper.edit.setText(ACCEPT);
            }
        });
        trainingWrapper.delete.setOnClickListener(v -> {
            Toast.makeText(context, R.string.ToastTrainingDeleted, Toast.LENGTH_SHORT).show();
            deleteTraining(training);
            trainings.remove(training);
        });
        return item;
    }

    static class TrainingWrapper {
        TextView customer;
        TextView info;
        TextView time;
        Button edit;
        Button delete;
    }


    private void acceptTraining(Long trainingId) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/trainings/" + trainingId + "/accept";
        Log.i(TAG, "Making request on :" + url);
        ObjRequestWithToken putRequest = new ObjRequestWithToken(Request.Method.PUT, url, null,
                response -> {
                },
                error -> Log.e("Error. Response from accept : ", String.valueOf(error)));
        queue.add(putRequest);
    }

    private void cancelTraining(Long trainingId) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/trainings/" + trainingId + "/cancel";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken putRequest = new ObjRequestWithToken(Request.Method.PUT, url, null,
                response -> {
                    Log.d("Response from cancel : ", response.toString());
                },
                error -> Log.e("Error. Response cancel : ", String.valueOf(error)));
        queue.add(putRequest);
    }

    private void deleteTraining(Training training) {
        String url = Connection.url + "/trainings/" + training.getId();
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken deleteRequest = new ObjRequestWithToken(Request.Method.DELETE, url, null,
                response -> Log.i("Response from cancel : ", response.toString()),
                error -> Log.e("Error. Response cancel : ", String.valueOf(error)));
        queue.add(deleteRequest);
    }

}
