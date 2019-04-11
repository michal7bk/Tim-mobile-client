package pl.michal.tim_client.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.michal.tim_client.R;
import pl.michal.tim_client.databinding.ActivityLoginBinding;
import pl.michal.tim_client.databinding.ActivitySignupBinding;
import pl.michal.tim_client.login.LoginViewModel;

public class SignupActivity extends AppCompatActivity {


    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_username)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    @BindView(R.id.roles_spinner)
    Spinner _rolesSpiner;
    @BindView(R.id.input_surname)
    EditText _surnameText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignupBinding signupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        SignupViewModel signupViewModel = ViewModelProviders.of(this).get(SignupViewModel.class);
        signupBinding.setSignupViewModel(signupViewModel);
        signupBinding.executePendingBindings();
        signupViewModel.setContext(this);
        ButterKnife.bind(this);
        signupViewModel.initComponent(_nameText,_emailText,_passwordText,_signupButton,_loginLink,_rolesSpiner,_surnameText);
        _signupButton.setOnClickListener(v -> signupViewModel.signup());

        _loginLink.setOnClickListener(v -> {
            finish();
        });
        signupViewModel.populateSpinner();
        signupViewModel.addListenerOnSpinnerItemSelection();

    }

}
