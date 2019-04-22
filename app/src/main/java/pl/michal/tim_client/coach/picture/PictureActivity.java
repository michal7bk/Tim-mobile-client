package pl.michal.tim_client.coach.picture;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CoachPictureBinding;
import pl.michal.tim_client.utils.Connection;

public class PictureActivity extends AppCompatActivity {


    private static int RESULT_LOAD_IMAGE = 1;
    private static final int READ_PERMISSION = 0x01;
    private final String TAG = "PictureActivity";
    @BindView(R.id.buttonLoadPicture)
    Button buttonLoadPicture;

    @BindView(R.id.buttonSavePicture)
    Button buttonSavePicture;

    @BindView(R.id.imgView)
    ImageView picture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CoachPictureBinding coachPictureBinding = DataBindingUtil.setContentView(this, R.layout.coach_picture);
        PicutreViewModel picutreViewModel = ViewModelProviders.of(this).get(PicutreViewModel.class);
        coachPictureBinding.setPictureViewModel(picutreViewModel);
        coachPictureBinding.executePendingBindings();
        picutreViewModel.init(this);
        ButterKnife.bind(this);
        buttonLoadPicture.setOnClickListener(x -> loadPicture());
        buttonSavePicture.setOnClickListener(x -> savePictue(picutreViewModel));
        requestWritePermission();
    }

    private void loadPicture() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void savePictue(PicutreViewModel viewModel) {
        picture.buildDrawingCache();
        Bitmap bmap = picture.getDrawingCache();
        viewModel.insertImg(Connection.getUser().getId(), bmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Write Permission Failed");
                finish();
            }
        }
    }

    private void requestWritePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }
    }
}
