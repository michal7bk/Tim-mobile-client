package pl.michal.tim_client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.user.User.Roles;
import pl.michal.tim_client.utils.Connection;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        _signupButton.setOnClickListener(v -> signup());

        _loginLink.setOnClickListener(v -> {
            finish();
        });
        populateSpinner();
        addListenerOnSpinnerItemSelection();
    }


    private void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        Roles roles = (Roles) _rolesSpiner.getSelectedItem();
        String surname = _surnameText.getText().toString();


        makeSignupRequest(name, email, password, roles, surname);

        new android.os.Handler().postDelayed(
                () -> {
                    // depending on success
                    onSignupSuccess();
                    // onSignupFailed();
                    progressDialog.dismiss();
                }, 3000);
    }


    private void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.LoginFailed, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError(getString(R.string.ErrorValidUsername));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.ErrorValidEmail));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError(getString(R.string.ErrorValidPassword));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void populateSpinner() {
        _rolesSpiner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Roles.values()));
    }

    private void addListenerOnSpinnerItemSelection() {
        _rolesSpiner = findViewById(R.id.roles_spinner);
        _rolesSpiner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private void makeSignupRequest(String name, String email, String password, Roles roles, String surname) {
        String url = Connection.url + "/users/sign-up";
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("roles", roles.toString());
            jsonBody.put("surname", surname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("Response", response.toString());
                },
                error -> {
                    Log.d("Error.Response", String.valueOf(error));
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        Log.i(TAG, "making request " + postRequest);
        queue.add(postRequest);
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Log.i(TAG, "OnItemSelectedListiner" + parent.getItemAtPosition(pos).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

}
