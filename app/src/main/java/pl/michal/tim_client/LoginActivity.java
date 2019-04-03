package pl.michal.tim_client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.coach.MenuCoachActivity;
import pl.michal.tim_client.customer.MenuCustomerActivity;
import pl.michal.tim_client.user.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_username)
    EditText _usernameText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        Connection.setUser(makeLoginRequest(username, password));

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //TODO MAKE LOGOUT WITH SET ACTIVE FALSE
                        if (Connection.getUser().isActive())
                            onLoginSuccess();
                        else
                            onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getBaseContext(), R.string.ToastNewAccount, Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onLoginSuccess() {
        _loginButton.setEnabled(true);
        if (Connection.getUser().getRoles() == User.Roles.CUSTOMER) {
            Intent intent = new Intent(getApplicationContext(), MenuCustomerActivity.class);
            startActivity(intent);
        } else if (Connection.getUser().getRoles() == User.Roles.COACH) {
            Intent intent = new Intent(getApplicationContext(), MenuCoachActivity.class);
            startActivity(intent);
        } else {
            Log.i(TAG, "Any Roles do not match to User.Roles " + Connection.getUser().getRoles().toString());
        }
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.ToastIncorrectCredentials, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        _loginButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError(getString(R.string.ErrorValidUsername));
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError(getString(R.string.ErrorValidPassword));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private User makeLoginRequest(String name, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final User user = new User();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("password", password);
            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, Connection.url + "/auth/user", jsonBody,
                    response -> {
                        Log.i(TAG, "Response:  " + response.toString());
                        try {
                            JSONObject headers = response.getJSONObject("headers");
                            String token = headers.getString("Authorization");
                            if (!token.isEmpty()) {
                                user.setToken(token);
                                user.setUsername(response.getString("name"));
                                user.setRoles(response.getString("roles"));
                                user.setId(response.getLong("id"));
                                user.setActive(true);
                            }
                            setOnline(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> LoginActivity.this.onBackPressed()) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {
                        String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                        JSONObject responseJSON = new JSONObject(jsonString);
                        responseJSON.put("headers", new JSONObject(response.headers));
                        return Response.success(responseJSON,
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException | JSONException e) {
                        return Response.error(new ParseError(e));
                    }
                }

            };
            queue.add(jsonOblect);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void setOnline(User user) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Connection.url + "/users/set-online";
        Log.i(TAG, "Making request on :" + url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("roles", user.getRoles());
            jsonBody.put("name", user.getUsername());
            jsonBody.put("id", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjRequestWithToken putRequest = new ObjRequestWithToken(Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d(TAG,"Set online ");
                },
                error -> Log.e(TAG,"Error. Response from setting online."));
        queue.add(putRequest);
    }
}


