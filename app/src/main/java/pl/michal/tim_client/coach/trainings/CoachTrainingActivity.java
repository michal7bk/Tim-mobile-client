package pl.michal.tim_client.coach.trainings;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CoachTrainingsBinding;
import pl.michal.tim_client.utils.Connection;


public class CoachTrainingActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CoachTrainingsBinding coachTrainingsBinding = DataBindingUtil.setContentView(this, R.layout.coach_trainings);
        CoachTrainingViewModel coachTrainingViewModel = ViewModelProviders.of(this).get(CoachTrainingViewModel.class);
        coachTrainingsBinding.setTrainingViewModel(coachTrainingViewModel);
        coachTrainingsBinding.executePendingBindings();
        coachTrainingViewModel.init(Connection.getUser(), this, findViewById(R.id.listView));
    }

}
