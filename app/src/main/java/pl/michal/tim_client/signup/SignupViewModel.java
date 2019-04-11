package pl.michal.tim_client.signup;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.domain.User;
import pl.michal.tim_client.login.LoginActivity;
import pl.michal.tim_client.utils.Connection;

import java.util.HashMap;
import java.util.Map;

public class SignupViewModel extends ViewModel {
    private final String TAG = "SignupViewModel";
    private Context context;

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;
    Spinner _rolesSpiner;
    EditText _surnameText;

    void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        User.Roles roles = (User.Roles) _rolesSpiner.getSelectedItem();
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
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private void onSignupFailed() {
        Toast.makeText(context, R.string.LoginFailed, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError(context.getString(R.string.ErrorValidUsername));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(context.getString(R.string.ErrorValidEmail));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError(context.getString(R.string.ErrorValidPassword));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    void populateSpinner() {
        _rolesSpiner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, User.Roles.values()));
    }

    void addListenerOnSpinnerItemSelection() {
        _rolesSpiner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private void makeSignupRequest(String name, String email, String password, User.Roles roles, String surname) {
        String url = Connection.url + "/users/sign-up";
        RequestQueue queue = Volley.newRequestQueue(context);
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
                response -> Log.d("Response", response.toString()),
                error -> Log.d("Error.Response", String.valueOf(error))
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

    void setContext(Context context) {
        this.context = context;
    }

    public void initComponent(EditText nameText, EditText emailText, EditText passwordText, Button signupButton, TextView loginLink, Spinner rolesSpiner, EditText surnameText) {
        this._nameText = nameText;
        this._emailText = emailText;
        this._surnameText = surnameText;
        this._passwordText = passwordText;
        this._signupButton = signupButton;
        this._loginLink = loginLink;
        this._rolesSpiner = rolesSpiner;
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
