package com.francescoxx.plango.frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.francescoxx.plango.frontend.daytrip.DayTripAdapter;
import com.francescoxx.plango.frontend.model.Daytrip;
import com.francescoxx.plango.frontend.model.JwtUser;
import com.francescoxx.plango.frontend.model.Step;
import com.francescoxx.plango.frontend.remote.ApiUtils;
import com.francescoxx.plango.frontend.remote.DaytripService;
import com.francescoxx.plango.frontend.remote.JwtUserService;
import com.francescoxx.plango.frontend.remote.StepService;
import com.francescoxx.plango.frontend.step.StepAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    Logged Activity
    This activity represents an user logged

    Consente di effettuare gli use cases da loggati
 */
public class Main2Activity extends AppCompatActivity {

    //User Logged Fields
    private String tokenValue;
    private int userIdentification;
    private int daytripSelected;
    private String username;

    //Retrofit Client Services
    JwtUserService jwtUserService = ApiUtils.getJwtUserService();
    DaytripService daytripService = ApiUtils.getDayTripService();
    StepService stepService = ApiUtils.getStepService();

    //Layout Elements
    private ListView lvDaytrips;
    private ListView lvSteps;

    //Daytrip layout elements
    EditText etDaytripName;
    EditText etDayTripDescription;

    @Override
    protected void onStart() {
        super.onStart();
        getAllDayTrips();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this hides the top bar
        setContentView(R.layout.activity_main2);

        //BUNDLE INFO
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Get bundle info
            username = extras.getString("username");
            tokenValue = extras.getString("tokenValue");
            userIdentification = extras.getInt("loggedUserId");
            Toast.makeText(this, "secret name is " + username, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "logged as " + userIdentification + " with token " + tokenValue, Toast.LENGTH_LONG).show();
        }

        Button btnGetSecret = (Button) findViewById(R.id.btnGetSecretMessage);
        Button btnPostDaytrip = (Button) findViewById(R.id.btnPostDaytrip);
        Button btnGetAllDaytrips = (Button) findViewById(R.id.btnGetAllDaytrips);
        Button btnPlanActivity = (Button) findViewById(R.id.btnPlanActivity);
        Button btnGoActivity = (Button) findViewById(R.id.btnGoActivity);

        lvDaytrips = (ListView) findViewById(R.id.lvLoggedActivity);
        lvSteps = (ListView) findViewById(R.id.lvSteps);

        //Daytrip fields
        etDaytripName = (EditText) findViewById(R.id.etDaytripName);
        etDayTripDescription = (EditText) findViewById(R.id.etDaytripDescription);

        //BUTTON LISTENERS
        btnGoActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, GoMapsActivity.class);
                intent.putExtra("daytripId", daytripSelected);
                intent.putExtra("jwtuserId", userIdentification);
                startActivity(intent);
            }
        });

        btnPlanActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (daytripSelected == 0){
                    Toast.makeText(Main2Activity.this, "Please select a daytrip", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(Main2Activity.this, com.francescoxx.plango.frontend.MapsActivity.class);
                    intent.putExtra("daytripId", daytripSelected);
                    intent.putExtra("jwtuserId", userIdentification);
                    //Toast.makeText(Main2Activity.this, "passing go activity with daytripId: " + daytripSelected + " userId: " + userIdentification, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

            }
        });

        btnGetSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSecret("Token " + tokenValue);
            }
        });

        btnGetAllDaytrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllDayTrips();
            }
        });

        btnPostDaytrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDaytrips();
            }
        });
    }
    //Presentation Methods

    //Get-ALL Daytrips of this specific user
    //Show this Daytrips on a lvDaytrips (remember this is a personal interface,
    //Each JwtUser must show HIS daytrips on logging
    private void getAllDayTrips() {
        Call<List<Daytrip>> call = daytripService.getAllDayTrips(userIdentification);
        call.enqueue(new Callback<List<Daytrip>>() {
            public void onResponse(@NonNull Call<List<Daytrip>> call, @NonNull Response<List<Daytrip>> response) {
                List<Daytrip> daytrips = response.body();
                //reverse order in list
                if (daytrips != null) {
                    Collections.reverse(daytrips);
                }
                lvDaytrips.setAdapter(new DayTripAdapter(Main2Activity.this, daytrips));
                lvDaytrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Toast.makeText(getApplicationContext(), tweets[position], Toast.LENGTH_SHORT).show();
                        Daytrip something = (Daytrip) parent.getAdapter().getItem(position);
                        int daytripIndex = something.getId();
                        daytripSelected = something.getId();
                        getAllSteps(daytripIndex);

                        //ITEM SELECTED HANDLER
                        for (int j = 0; j < parent.getChildCount(); j++){
                            parent.getChildAt(j).setBackgroundColor(Color.WHITE);
                        }
                        view.setBackgroundColor(Color.LTGRAY);


                        Toast.makeText(Main2Activity.this, something.getName() + " selected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            public void onFailure(@NonNull Call<List<Daytrip>> call, Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(Main2Activity.this, "Error on createDaytrips " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Create-one Daytrip (for this user)
    private void createDaytrips() {

        //Create a new Daytrip object to save in the db
        String name = etDaytripName.getText().toString();
        String description = etDayTripDescription.getText().toString();
        final Daytrip d = new Daytrip(name, description);

        //Presentation form validation
        if ((Objects.equals(name, "")) || (Objects.equals(description, ""))){
            Toast.makeText(this, "inserire nome e descrizione!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Retrofit call.enqueue
        Call<Void> call = daytripService.createDaytrip(userIdentification, d);
        call.enqueue(new Callback<Void>() {
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Main2Activity.this, d.getName() + " is a new daytrip!", Toast.LENGTH_SHORT).show();
                    getAllDayTrips();
                } else {
                    Toast.makeText(Main2Activity.this, "Error cant insert" + response.body(), Toast.LENGTH_SHORT).show();
                }
            }
            public void onFailure(@NonNull Call<Void> call, Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(Main2Activity.this, "Error on createDaytrips " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDaytrip() {
        //show relative Daytrip on map as a list of steps (Converted to Marks)
        Intent intent = new Intent(Main2Activity.this, com.francescoxx.plango.frontend.MapsActivity.class);
        startActivity(intent);
    }

    /*VERY IMPORTANT!
        needs an id (maybe a token too someday)
        and returns all Daytrips for THAT particular id
     */

    //GET-ALL steps (of a particular daytip and specific user)
    // triggered onClick() a particular Daytrip. shows the daytrip steps
    private void getAllSteps(int daytripIndex) {//pposition+1 of item in daytrip listview
        Call<List<Step>> call = stepService.getAll(userIdentification, daytripIndex);
        call.enqueue(new Callback<List<Step>>() {
            public void onResponse(@NonNull Call<List<Step>> call, @NonNull Response<List<Step>> response) {
                List<Step> steps = response.body();
                lvSteps.setAdapter(new StepAdapter(Main2Activity.this, steps));
            }
            public void onFailure(@NonNull Call<List<Step>> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(Main2Activity.this, "Error on getAllSteps " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    //secret Area Test
    public void getSecret(String tok) {
        Call<JwtUser> call = jwtUserService.getSecretId(tok, username);
        call.enqueue(new Callback<JwtUser>() {
            @Override
            public void onResponse(@NonNull Call<JwtUser> call, @NonNull Response<JwtUser> response) {
                String code = String.valueOf(response.code());
                //Toast.makeText(Main2Activity.this, code, Toast.LENGTH_SHORT).show();
                //get the secret id of the user(could be any personal info)
                String body = String.valueOf(response.body());
                if (body != null){
                    JwtUser s = response.body();
                    Toast.makeText(Main2Activity.this, "Secret: " + s.getId(), Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(Main2Activity.this, "message is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JwtUser> call, Throwable t) {
                Toast.makeText(Main2Activity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
