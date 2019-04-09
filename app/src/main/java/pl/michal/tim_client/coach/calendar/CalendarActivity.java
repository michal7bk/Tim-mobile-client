package pl.michal.tim_client.coach.calendar;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ImageButton;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.ActivityCalendarBinding;
import pl.michal.tim_client.utils.Connection;

public class CalendarActivity extends AppCompatActivity {
    private ImageButton previous;
    private ImageButton next;
    private GridView gridview;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCalendarBinding calendarBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);
        CalendarViewModel calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        calendarBinding.setCalendarViewModel(calendarViewModel);
        calendarBinding.executePendingBindings();
        initComponent();
        calendarViewModel.init(Connection.getUser(), this, gridview, previous, next);
    }


    private void initComponent(){
        gridview = findViewById(R.id.gv_calendar);
        next = findViewById(R.id.Ib_next);
        previous = findViewById(R.id.ib_prev);
    }


}
