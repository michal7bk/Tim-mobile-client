package pl.michal.tim_client.coach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableRow;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.customer.CustomerCurrentTrainingsActivity;
import pl.michal.tim_client.customer.CustomerNewTrainingsActive;

public class MenuCoachActivity extends AppCompatActivity {

    @BindView(R.id.coach_profile)
    TableRow _profile;

    @BindView(R.id.coach_trainings)
    TableRow _trainings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_menu);
        ButterKnife.bind(this);
        _profile.setOnClickListener(x ->openCoachProfile());
        _trainings.setOnClickListener(x->openCoachTrainings());
    }

    private void openCoachProfile() {
        Intent intent = new Intent(getApplicationContext(), CoachProfileActive.class);
        startActivity(intent);
    }

    private void openCoachTrainings() {
        Intent intent = new Intent(getApplicationContext(), CoachTrainingsActivity.class);
        startActivity(intent);
    }

}
