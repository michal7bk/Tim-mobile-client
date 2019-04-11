package pl.michal.tim_client.customer.newTraining;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.CustomerNewtrainingBinding;
import pl.michal.tim_client.utils.Connection;

public class NewTrainingActivity extends AppCompatActivity {

    @BindView(R.id.btn_set_end_date)
    Button _setEndDate;
    @BindView(R.id.btn_set_start_date)
    Button _setStartDate;
    @BindView(R.id.btn_propose_training)
    Button _proposeTraining;
    @BindView(R.id.coches_spinner)
    Spinner _coachesSpinner;
    @BindView(R.id.input_info)
    EditText _inputInfo;
    @BindView(R.id.text_startDate)
    TextView _textStartDate;
    @BindView(R.id.text_endDate)
    TextView _textEndDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomerNewtrainingBinding customerNewtrainingBinding = DataBindingUtil.setContentView(this, R.layout.customer_newtraining);
        NewTrainingViewModel newTrainingViewModel = ViewModelProviders.of(this).get(NewTrainingViewModel.class);
        customerNewtrainingBinding.setNewTrainingViewModel(newTrainingViewModel);
        customerNewtrainingBinding.executePendingBindings();
        ButterKnife.bind(this);
        newTrainingViewModel.initComponent(_setEndDate,_setStartDate,_proposeTraining,_coachesSpinner,_inputInfo,_textStartDate,_textEndDate);
        setUpListiners(newTrainingViewModel);
        newTrainingViewModel.init(Connection.getUser(), this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpListiners(NewTrainingViewModel viewModel) {
        _setStartDate.setOnClickListener(x -> viewModel.openDataPicker(true));
        _setEndDate.setOnClickListener(x -> viewModel.openDataPicker(false));
        _proposeTraining.setOnClickListener(x -> viewModel.propopseNewTraining());
        viewModel.addListenerOnSpinnerItemSelection();
    }

}
