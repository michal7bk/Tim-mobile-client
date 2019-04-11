package pl.michal.tim_client.customer.findCoach;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CustomerCoachesListBinding;
import pl.michal.tim_client.utils.Connection;

public class FindCoachActivity extends AppCompatActivity {
    private final String TAG = "FindCoachActivity";

    @BindView(R.id.listView)
    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomerCoachesListBinding coachesListBinding = DataBindingUtil.setContentView(this, R.layout.customer_coaches_list);
        FindCoachViewModel findCoachViewModel = ViewModelProviders.of(this).get(FindCoachViewModel.class);
        coachesListBinding.setFindCoachViewModel(findCoachViewModel);
        ButterKnife.bind(this);
        findCoachViewModel.initComponent(listView);
        coachesListBinding.executePendingBindings();
        findCoachViewModel.init(Connection.getUser(), this);

    }
}
