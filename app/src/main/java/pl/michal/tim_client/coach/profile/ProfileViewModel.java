package pl.michal.tim_client.coach.profile;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.model.Coach;
import pl.michal.tim_client.domain.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LocalDateTimeJsonConverter;
import pl.michal.tim_client.utils.ObjRequestWithToken;
import pl.michal.tim_client.utils.database.FeedReaderContract;
import pl.michal.tim_client.utils.database.FeedReaderDbHelper;

import java.time.LocalDateTime;

public class ProfileViewModel extends ViewModel {

    private final String TAG = "ProfileViewModel";

    public ObservableField<String> nameWithSurname = new ObservableField<>();
    public ObservableField<String> acceptedTrainings = new ObservableField<>();
    public ObservableField<String> proposedTrainings = new ObservableField<>();
    public ObservableField<String> uniqueCustomers = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> surname = new ObservableField<>();
    public ObservableField<String> roles = new ObservableField<>();

    private Coach coach;
    private Context context;
    private ImageView imageView;


    private void setNumberOfAcceptedTrainings(Coach coach, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/coaches/" + coach.getId() + "/accepted-trainings";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        acceptedTrainings.set(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response : ", String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setNumberOfProposedTrainings(Coach coach, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/coaches/" + coach.getId() + "/proposed-trainings";
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        proposedTrainings.set(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on : " + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setNumberOfUniqueCoaches(Coach coach, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/coaches/" + coach.getId() + "/unique-customers";
        Log.i(TAG, "Making request on : " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    try {
                        uniqueCustomers.set(response.getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response on" + url, String.valueOf(error))
        );
        queue.add(getRequest);
    }

    private void setUpValue(Coach coach) {
        nameWithSurname.set(coach.getName() + " " + coach.getSurname());
        email.set(coach.getEmail());
        name.set(coach.getName());
        surname.set(coach.getSurname());
        roles.set(String.valueOf(R.string.Coach));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void init(User user, Context context, ImageView imageView) {
        this.imageView = imageView;
        this.context = context;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/coaches/" + user.getId();
        Log.i(TAG, "Making request on: " + url);
        ObjRequestWithToken getRequest = new ObjRequestWithToken(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonConverter())
                            .create();
                    coach = gson.fromJson(String.valueOf(response), Coach.class);
                    Log.i(TAG, "Read coach : " + coach);
                    setUpValue(coach);
                    setNumberOfAcceptedTrainings(coach, context);
                    setNumberOfProposedTrainings(coach, context);
                    setNumberOfUniqueCoaches(coach, context);
                    readPicture();
                },
                error -> Log.d("Error.Response", String.valueOf(error))
        );
        queue.add(getRequest);
    }


    public ObservableField<String> getNameWithSurname() {
        return nameWithSurname;
    }

    public ObservableField<String> getAcceptedTrainings() {
        return acceptedTrainings;
    }

    public ObservableField<String> getProposedTrainings() {
        return proposedTrainings;
    }

    public ObservableField<String> getUniqueCustomers() {
        return uniqueCustomers;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public ObservableField<String> getName() {
        return name;
    }

    public ObservableField<String> getSurname() {
        return surname;
    }

    public ObservableField<String> getRoles() {
        return roles;
    }

    public void readPicture() {
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE
        };
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {Connection.getUser().getId().toString()};
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";
        Cursor cur = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        if (cur.moveToFirst()) {
            byte[] imgByte = cur.getBlob(2);
            cur.close();
            Bitmap result = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            imageView.setImageBitmap(result);
        }
        if (!cur.isClosed()) {
            cur.close();
        }
    }

}
