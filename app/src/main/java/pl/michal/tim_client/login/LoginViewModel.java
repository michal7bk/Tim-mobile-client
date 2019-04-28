package pl.michal.tim_client.login;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import pl.michal.tim_client.R;
import pl.michal.tim_client.coach.MenuCoachActivity;
import pl.michal.tim_client.customer.MenuCustomerActivity;
import pl.michal.tim_client.domain.User;
import pl.michal.tim_client.utils.Connection;
import pl.michal.tim_client.utils.ObjRequestWithToken;

import java.io.UnsupportedEncodingException;


public class LoginViewModel extends ViewModel {

    private final String TAG = "LoginViewModel";
    Context context;

    EditText _usernameText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
     ProgressDialog progressDialog ;



    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        Connection.setUser(makeLoginRequest(username, password));

        new android.os.Handler().postDelayed(
                () -> {
                    if (Connection.getUser().isActive())
                        onLoginSuccess();
                    else
                        onLoginFailed();
                    progressDialog.dismiss();
                }, 3000);
    }


    private void onLoginSuccess() {
        _loginButton.setEnabled(true);
        if (Connection.getUser().getRoles() == User.Roles.CUSTOMER) {
            Intent intent = new Intent(context, MenuCustomerActivity.class);
            context.startActivity(intent);
        } else if (Connection.getUser().getRoles() == User.Roles.COACH) {
            Intent intent = new Intent(context, MenuCoachActivity.class);
            context.startActivity(intent);
        } else {
            Log.i(TAG, "Any Roles do not match to User.Roles " + Connection.getUser().getRoles().toString());
        }
    }

    private void onLoginFailed() {
        Toast.makeText(context, R.string.ToastIncorrectCredentials, Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(context, SignupActivity.class);
//        context.startActivityForResult(intent, REQUEST_SIGNUP);
//        _loginButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError(context.getString(R.string.ErrorValidUsername));
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError(context.getString(R.string.ErrorValidPassword));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private User makeLoginRequest(String name, String password) {
        RequestQueue queue = Volley.newRequestQueue(context);
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
                    error -> Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()) {
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
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Connection.url + "/users";
        Log.i(TAG, "Making request on :" + url);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("active", true);
            jsonBody.put("roles", user.getRoles());
            jsonBody.put("name", user.getUsername());
            jsonBody.put("id", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjRequestWithToken putRequest = new ObjRequestWithToken(Request.Method.PUT, url, jsonBody,
                response -> Log.d(TAG, "Set online "),
                error -> Log.e(TAG, "Error. Response from setting online."));
        queue.add(putRequest);
    }

    public void initComponent(EditText usernameText, EditText passwordText, Button loginButton, TextView signupLink) {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        this._usernameText = usernameText;
        this._passwordText = passwordText;
        this._loginButton = loginButton;
        this._signupLink = signupLink;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
