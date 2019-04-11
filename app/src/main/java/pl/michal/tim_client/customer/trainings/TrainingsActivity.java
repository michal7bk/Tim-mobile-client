package pl.michal.tim_client.customer.trainings;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CustomerCurrenttrainingsBinding;
import pl.michal.tim_client.utils.Connection;

import java.util.Objects;

public class TrainingsActivity extends AppCompatActivity {

    @BindView(R.id.table_layout)
    TableLayout _tableLayout;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomerCurrenttrainingsBinding customerCurrenttrainingsBinding = DataBindingUtil.setContentView(this, R.layout.customer_currenttrainings);
        TrainingViewModel trainingViewModel = ViewModelProviders.of(this).get(TrainingViewModel.class);
        customerCurrenttrainingsBinding.setCurenTrainingsViewModel(trainingViewModel);
        ButterKnife.bind(this);
        customerCurrenttrainingsBinding.executePendingBindings();
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.CustomerTrainingsTitle);
        trainingViewModel.initComponent(_tableLayout);
        trainingViewModel.init(Connection.getUser(), this);
    }




}
