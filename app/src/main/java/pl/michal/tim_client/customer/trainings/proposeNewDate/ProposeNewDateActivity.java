package pl.michal.tim_client.customer.trainings.proposeNewDate;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.ActivityCustomerNewDateBinding;

public class ProposeNewDateActivity extends AppCompatActivity {

    @BindView(R.id.btn_set_new_start_date)
    Button _newStartDate;
    @BindView(R.id.btn_set_new_end_date)
    Button _newEndDate;
    @BindView(R.id.btn_propose_new_date)
    Button _proposeNewDate;
    @BindView(R.id.text_startDate)
    TextView _textStartDate;
    @BindView(R.id.text_endDate)
    TextView _textEndDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCustomerNewDateBinding customerNewDateBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_new_date);
        ProposeNewDateViewModel proposeNewDateViewModel = ViewModelProviders.of(this).get(ProposeNewDateViewModel.class);
        customerNewDateBinding.setNewDateVieModel(proposeNewDateViewModel);
        ButterKnife.bind(this);
        setTitle(getString(R.string.TittleProposeTraining));
        Intent intent = getIntent();
        Long id = intent.getLongExtra("idTraining", 0);
        customerNewDateBinding.executePendingBindings();
        setUpListiners(proposeNewDateViewModel, id);
        proposeNewDateViewModel.initComponent(_newStartDate, _newEndDate, _textEndDate, _textStartDate);
        proposeNewDateViewModel.init(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpListiners(ProposeNewDateViewModel proposeNewDateViewModel, Long id) {
        _proposeNewDate.setOnClickListener(view -> proposeNewDateViewModel.requestProposeNewDateTraining(id));
        _newStartDate.setOnClickListener(x -> proposeNewDateViewModel.openDataPicker(true));
        _newEndDate.setOnClickListener(x -> proposeNewDateViewModel.openDataPicker(false));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("message_return", "This data is returned when user click back menu in target activity.");
        setResult(RESULT_OK, intent);
        finish();
    }


}
