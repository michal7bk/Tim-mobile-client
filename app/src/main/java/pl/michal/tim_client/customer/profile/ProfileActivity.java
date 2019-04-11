package pl.michal.tim_client.customer.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CustomerProfileBinding;
import pl.michal.tim_client.utils.Connection;

public class ProfileActivity extends AppCompatActivity {
    private final String TAG = "ProfileActivity";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomerProfileBinding profileBinding = DataBindingUtil.setContentView(this, R.layout.customer_profile);
        ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileBinding.setProfileViewModel(profileViewModel);
        profileBinding.executePendingBindings();
        profileViewModel.init(Connection.getUser(), this);
    }


}
