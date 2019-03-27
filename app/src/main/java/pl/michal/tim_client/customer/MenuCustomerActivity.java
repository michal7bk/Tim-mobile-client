package pl.michal.tim_client.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableRow;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.LoginActivity;
import pl.michal.tim_client.R;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LogoutUtils;

public class MenuCustomerActivity extends AppCompatActivity {

    @BindView(R.id.new_training)
    TableRow _newTraining;

    @BindView(R.id.current_trainings)
    TableRow _curentTrainings;

    @BindView(R.id.customer_profile)
    TableRow _customerProfile;

    @BindView(R.id.btn_CustomerLogout)
    FloatingActionButton _logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);
        ButterKnife.bind(this);
        _newTraining.setOnClickListener(x -> newTrainingClick());
        _curentTrainings.setOnClickListener(x -> currentTrainingsClick());
        _customerProfile.setOnClickListener(x -> customerProfileClick());
        _logout.setOnClickListener(x -> logout());
    }

    private void newTrainingClick() {
        Intent intent = new Intent(getApplicationContext(), CustomerNewTrainingsActive.class);
        startActivity(intent);
    }

    private void currentTrainingsClick() {
        Intent intent = new Intent(getApplicationContext(), CustomerCurrentTrainingsActivity.class);
        startActivity(intent);
    }


    private void customerProfileClick() {
        Intent intent = new Intent(getApplicationContext(), CustomerProfileActive.class);
        startActivity(intent);
    }

    private void logout() {
        //TODO CHECK IF ID IS OK ?!
        LogoutUtils.setOffline(Connection.getUser(), this);
        LogoutUtils.cleanUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


}
