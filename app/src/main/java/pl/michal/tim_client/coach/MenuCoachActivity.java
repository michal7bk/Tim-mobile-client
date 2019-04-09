package pl.michal.tim_client.coach;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableRow;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.LoginActivity;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.calendar.CalendarActivity;
import pl.michal.tim_client.coach.profile.ProfileActivity;
import pl.michal.tim_client.coach.trainings.CoachTrainingActivity;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.LogoutUtils;

public class MenuCoachActivity extends AppCompatActivity {

    @BindView(R.id.coach_profile)
    TableRow _profile;

    @BindView(R.id.coach_trainings)
    TableRow _trainings;

    @BindView(R.id.coach_calendar)
    TableRow _calendar;

    @BindView(R.id.btn_CoachLogout)
    FloatingActionButton _logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_menu);
        ButterKnife.bind(this);
        _profile.setOnClickListener(x -> openCoachProfile());
        _trainings.setOnClickListener(x -> openCoachTrainings());
        _calendar.setOnClickListener(x -> openCalendar());
        _logout.setOnClickListener(x -> logout());
    }

    private void openCoachProfile() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    private void openCoachTrainings() {
        Intent intent = new Intent(getApplicationContext(), CoachTrainingActivity.class);
        startActivity(intent);
    }

    private void openCalendar() {
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        startActivity(intent);
    }

    private void logout() {
        LogoutUtils.setOffline(Connection.getUser(), this);
        LogoutUtils.cleanUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


}
