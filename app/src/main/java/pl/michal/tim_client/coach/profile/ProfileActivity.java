package pl.michal.tim_client.coach.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CoachProfileBinding;
import pl.michal.tim_client.utils.Connection;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.coach_picture)
    ImageView picture;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CoachProfileBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.coach_profile);
        ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        activityMainBinding.setProfileViewModel(profileViewModel);
        ButterKnife.bind(this);
        activityMainBinding.executePendingBindings();
        profileViewModel.init(Connection.getUser(), this, picture);
    }

}
