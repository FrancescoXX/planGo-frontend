package com.francescoxx.plango.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.francescoxx.plango.frontend.model.JwtUser;
import com.francescoxx.plango.frontend.model.Token;
import com.francescoxx.plango.frontend.poi.Poi;
import com.francescoxx.plango.frontend.poi.PoiAdapter;
import com.francescoxx.plango.frontend.remote.ApiUtils;
import com.francescoxx.plango.frontend.remote.JwtUserService;
import com.francescoxx.plango.frontend.remote.PoiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    Maps Activity -> Login Activity -> Logged Activity

    1st use case: Login to a Restful web service with "existing" username and password (server will check db) to get token (JWT)
    in this server there is an existing user: "assa"//"zerg" with role "admin"
    will send this JwtUser to the server TO GET a TOKEN back, displayed on a TOAST (or TextEdit)
    will do a POST request to /token

    2nd use case:
    user accesses to SECRET AREA (/secret) with token as header to get the SECRET MESSAGE
    (if trying to access without token or wrong tokn will get an error)
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    //Services needed for this Activity
    private JwtUserService jwtUserService = ApiUtils.getJwtUserService();
    private PoiService poiService = ApiUtils.getPoiService();

    //private int idToPass = 0;

    private ListView lvPois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this hides the top bar
        setContentView(R.layout.activity_main);


        lvPois = (ListView) findViewById(R.id.lvPois);

        //Layout Elements
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Button btnGetUserIdByName = (Button) findViewById(R.id.btnGetUsernameId);
        Button btnGoMapsActivity = (Button) findViewById(R.id.btnGoMapsActivity);

        Button btnGetAllPois = (Button) findViewById(R.id.btnGetAllPois);

        //BUTTONS LISTENERS

        btnGetAllPois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllPois();
            }
        });

        //go maps activity
        btnGoMapsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, com.francescoxx.plango.frontend.MapsActivity.class);
                startActivity(intent);
            }
        });


        //Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                //1-validate login-form
                if (validateLogin(username, password)) {
                    //2-doLogin
                    doLogin(username, password);
                }
            }
        });

        //Register Button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                //1-validate register-form: user should not exist already
                if (validateRegister(username, password)) {
                    //2-doRegister
                    doRegister(username, password);
                }
            }

        });

    }

    private void getAllPois() {

        //Toast.makeText(this, "pois are coming..", Toast.LENGTH_SHORT).show();
        Call<List<Poi>> call = poiService.getAll();
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                //Toast.makeText(LoginActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                List<Poi> pois = response.body();
                lvPois.setAdapter(new PoiAdapter(LoginActivity.this, pois));
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateRegister(String username, String password) {
        return validateLogin(username, password);
    }

    private void doRegister(String username, String password) {

        //create jwtUser and return nothing for now
        //Post request with Object in the body
        final JwtUser j = new JwtUser(username, password, "ADMIN", "info aggiuntive");

        //Log&Toast check
        Log.d("retro", "Register request for: " + j);
        //Toast.makeText(this, j.toString(), Toast.LENGTH_SHORT).show();

        //potrei tornare un messaggio con informazioni riguardanti l'operazione
        Call<Void> call = jwtUserService.register(j);
        call.enqueue(new Callback<Void>() {
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, j.getUserName() + " registered as new user!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "User already exist!", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(LoginActivity.this, "Error on doLogin " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //PRESENTATION Form-Control
    private boolean validateLogin(String username, String password) {

        if ((username == null || username.trim().length() == 0) && (password == null || password.trim().length() == 0)) {
            Toast.makeText(this, "Username and Password required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (username == null || username.trim().length() == 0) {
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (password == null || password.trim().length() == 0) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //Presentation form-control VALIDATED
        return true;
    }

    //LOGIN
    private void doLogin(final String username, final String password) {

        //create jwt user//pass to login
        final JwtUser jwtUser = new JwtUser(username, password, "ADMIN", "ciao");

        //Log
        Log.d("retro", "Logging for: " + jwtUser);

        //Post request with Object in the body
        //Parameter is a JwtUser and returns Token object
        Call<Token> call = jwtUserService.login(jwtUser);
        call.enqueue(new Callback<Token>() {
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {

                Log.d("retro", "response code is: " + response.code());

                if (response.isSuccessful()) {

                    //Retrofit assign the response.body to the return type of Call method
                    final Token token = response.body();
                    Log.d("retro", "Token is: " + token);

                    if (token != null) {

                        final String name = edtUsername.getText().toString();
                        Call<JwtUser> call2 = jwtUserService.getJwtUserByName(name);
                        call2.enqueue(new Callback<JwtUser>() {
                            public void onResponse(@NonNull Call<JwtUser> call2, @NonNull Response<JwtUser> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "welcome " + name, Toast.LENGTH_SHORT).show();
                                    int id = response.body().getId();
                                    String user = response.body().getUserName();

                                    Intent intent = new Intent(LoginActivity.this, com.francescoxx.plango.frontend.Main2Activity.class);
                                    intent.putExtra("loggedUserId", id);
                                    intent.putExtra("tokenValue", token.getTheToken()); //set token
                                    intent.putExtra("username", user);
                                    Toast.makeText(LoginActivity.this, "logged with token: " + token.getTheToken(), Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error cant login" + response.body(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            public void onFailure(Call<JwtUser> call, Throwable t) {
                                Log.d("retro", "ERROR!");
                                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(LoginActivity.this, "Password not valid" + token, Toast.LENGTH_SHORT).show();
                    }

                    //Store token in local

                } else {
                    Toast.makeText(LoginActivity.this, "Error! Please try again" + response.body(), Toast.LENGTH_SHORT).show();
                }
            }
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(LoginActivity.this, "Error on Login ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
