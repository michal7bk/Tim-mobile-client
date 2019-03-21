package pl.michal.tim_client.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableRow;
import butterknife.BindView;
import pl.michal.tim_client.R;
import butterknife.ButterKnife;

public class MenuCustomerActivity extends AppCompatActivity {

    @BindView(R.id.new_training)
    TableRow _newTraining;

    @BindView(R.id.current_trainings)
    TableRow _curentTrainings;

    @BindView(R.id.customer_profile)
    TableRow _customerProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);
        ButterKnife.bind(this);
        _newTraining.setOnClickListener(x -> newTrainingClick());
        _curentTrainings.setOnClickListener(x -> currentTrainingsClick());
        _customerProfile.setOnClickListener(x -> customerProfileClick());

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


}
