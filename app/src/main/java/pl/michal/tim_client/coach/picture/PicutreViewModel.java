package pl.michal.tim_client.coach.picture;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import pl.michal.tim_client.R;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.database.FeedReaderDbHelper;

import java.io.ByteArrayOutputStream;

import static pl.michal.tim_client.utils.database.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE;
import static pl.michal.tim_client.utils.database.FeedReaderContract.FeedEntry.TABLE_NAME;

public class PicutreViewModel extends ViewModel {
    private final String TAG = "PicutreViewModel";
    private Context context;
    private SQLiteDatabase db;


    public void init(Context context) {
        this.context = context;
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }


    void insertImg(Long id, Bitmap img) {
        clearTable();
        byte[] data = getBitmapAsByteArray(img);
        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + TABLE_NAME + " VALUES(?,?, ?);");
        stmt.bindLong(1, Connection.getUser().getId());
        stmt.bindLong(2, id);
        stmt.bindBlob(3, data);
        stmt.executeInsert();
        stmt.clearBindings();
        Toast.makeText(context, R.string.ToastSavedImage, Toast.LENGTH_LONG).show();
        Log.i(TAG, "Image" + img + "was saved");
    }

    private void clearTable() {
        SQLiteStatement stmt = db.compileStatement("DELETE  FROM  " + TABLE_NAME + " WHERE " + COLUMN_NAME_TITLE + "=" + Connection.getUser().getId() + ";");
        stmt.execute();
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
