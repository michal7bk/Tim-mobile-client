package pl.michal.tim_client.customer.findCoach;

import android.app.Activity;
import android.content.Context;
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
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.util.List;

public class CoachesArrayAdapter extends ArrayAdapter<Coach> {
    private final String TAG = "CoachesArrayAdapter";


    private Context context;
    private int layoutResourceId;
    private List<Coach> coaches;

    CoachesArrayAdapter(Context context, int layoutResourceId,
                        List<Coach> studs) {
        super(context, layoutResourceId, studs);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.coaches = studs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        CoachWrapper coachWrapper;
        if (item == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(layoutResourceId, parent, false);
            coachWrapper = new CoachWrapper();
            coachWrapper.name = item.findViewById(R.id.textName_coachesList);
            coachWrapper.contact = item.findViewById(R.id.btnContact);
            item.setTag(coachWrapper);
        } else {
            coachWrapper = (CoachWrapper) item.getTag();
        }
        Coach coach = coaches.get(position);
        coachWrapper.name.setText(coach.getName()+ " " + coach.getSurname());
        coachWrapper.contact.setText("Ask for contact");

        coachWrapper.contact.setOnClickListener(v -> {
            askForContact(coach);
            Toast.makeText(context, R.string.ToastAskForContact , Toast.LENGTH_LONG).show();
        });
        return item;
    }

    static class CoachWrapper {
        TextView name;
        TextView surname;
        TextView email;
        Button contact;
    }


    private void askForContact(Coach coach) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/customers/" + Connection.getUser().getId() + "/ask-for-contact?email=" + coach.getEmail();
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {

                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }


}
