package com.francescoxx.plango.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.francescoxx.plango.frontend.model.Step;
import com.francescoxx.plango.frontend.poi.Poi;
import com.francescoxx.plango.frontend.remote.ApiUtils;
import com.francescoxx.plango.frontend.remote.PoiService;
import com.francescoxx.plango.frontend.remote.StepService;
import com.francescoxx.plango.frontend.step.MyAdapter;
import com.francescoxx.plango.frontend.step.StepAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.francescoxx.plango.frontend.FreeActivity.utentePOI;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int currentUserId;
    private int currentDaytripId;

    List<Poi> listaPois = new ArrayList<>();
    List<Step> tempStepsToAdd = new ArrayList<>();
    ListView lvTempPois;
    MyAdapter myAdapter;

    //Services
    PoiService poiService = ApiUtils.getPoiService();
    StepService stepService = ApiUtils.getStepService();

    //Step fields
    TextView tvStepName;
    TextView tvStepDescription;

    //ADD STEP
    Step currentTempStep;
    List<Step> listCurrentTempSteps;
    List<Step> stepsFromMain2Activity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().hide(); //<< this hides the top bar
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //BUNDLE INFO
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentDaytripId = extras.getInt("daytripId");
            currentUserId = extras.getInt("jwtuserId");
        }

        //Layout Elements

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        ImageButton btnNewTempStep = (ImageButton) findViewById(R.id.btnNewTempStep);
        ImageButton btnListActivity = (ImageButton) findViewById(R.id.btnListActivity);
        Button btnOk = (Button) findViewById(R.id.btnOk);

        lvTempPois = (ListView) findViewById(R.id.lvTempPois);
        Button btnGetAllPoisMaps = (Button) findViewById(R.id.btnGetAllPoisMaps);

        //Step fields
        tvStepName = (TextView) findViewById(R.id.tvStepName);
        tvStepDescription = (TextView) findViewById(R.id.tvStepDescription);

        //Adapter initialization and set
        myAdapter = new MyAdapter(MapsActivity.this);
        lvTempPois.setAdapter(myAdapter);

        //Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MapsActivity.this, MapsActivity.class);
                intent.putExtra("loggedUserId", currentUserId);
                startActivity(intent);
            }
        });

        //BUTTON LISTENERS
        //Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MapsActivity.this, Main2Activity.class);
                intent.putExtra("loggedUserId", currentUserId);
                startActivity(intent);
            }
        });

        //OK Button: saves the new daytrip adding new pois to old ones
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tempStepsToAdd.isEmpty()) {

                    //ADD THE STEPS SELECTED IN THIS ACTIVITY!
                    for (int i = 0; i < tempStepsToAdd.size(); i++) {
                        Step step = tempStepsToAdd.get(i);
                        Call<Void> call = stepService.addStepToDaytrip(step, currentUserId, currentDaytripId);
                        call.enqueue(new Callback<Void>() {
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MapsActivity.this, "new steps added!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Log.d("retro", "ERROR!");
                                Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        //List Activity
        btnListActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Alla pressione del tasto converte i valori in un gson e li mette in un bundle che passa alla ListActivity
                Gson gson = new Gson(); //Crea un Gson
                String jsonArrayInStringa = gson.toJson(listaPois); //converte le lista in un JsonArray attraverso il metodo toJson
                Intent intentListActivity = new Intent(MapsActivity.this, ListActivity.class);
                intentListActivity.putExtra("jsonArray", jsonArrayInStringa); //mette la stringa in formato JSON con key "array" nel bundle

                //salva le coordinate dell'utente nel bundle
                intentListActivity.putExtra("utenteLat", utentePOI.getLatitude());
                intentListActivity.putExtra("utenteLon", utentePOI.getLongitude());

                Log.d("test", "ARRAY:" + jsonArrayInStringa);
                startActivity(intentListActivity);
            }
        });

        //Get-All Pois. Read only!
        btnGetAllPoisMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllPois();
            }
        });

        //New Step for a Daytrip (for the relative user)
        btnNewTempStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addTempStepToList();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllPois();
        getImmutableSteps(currentDaytripId); //list of steps from daytrip in last activity
        getAllSteps(currentDaytripId);
    }

    private void addTempStepToList() {

        //Devo prendere lo Step selezionato e la lista degli attuali steps, ed aggiungere lo step alla lista
        Step step = currentTempStep;

        //List ONLY wtih step to add on ok
        tempStepsToAdd.add(step);

        //Toast.makeText(this, "will add " + step + " to " + tempSteps, Toast.LENGTH_SHORT).show();

        //add step to temp daytrip list
        listCurrentTempSteps.add(step);
        lvTempPois.setAdapter(new StepAdapter(MapsActivity.this, listCurrentTempSteps));
    }

    private void getImmutableSteps(int daytripIndex) {
        Call<List<Step>> call = stepService.getAll(currentUserId, daytripIndex);
        call.enqueue(new Callback<List<Step>>() {
            public void onResponse(@NonNull Call<List<Step>> call, @NonNull Response<List<Step>> response) {
                List<Step> steps = response.body();

                stepsFromMain2Activity = steps;
            }

            public void onFailure(@NonNull Call<List<Step>> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(MapsActivity.this, "Error on getAllSteps " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllSteps(int daytripIndex) {
        Call<List<Step>> call = stepService.getAll(currentUserId, daytripIndex);
        call.enqueue(new Callback<List<Step>>() {
            public void onResponse(@NonNull Call<List<Step>> call, @NonNull Response<List<Step>> response) {
                List<Step> steps = response.body();

                //temp step list
                listCurrentTempSteps = steps;
                //Toast.makeText(MapsActivity.this, "polists is " + steps, Toast.LENGTH_SHORT).show();
                lvTempPois.setAdapter(new StepAdapter(MapsActivity.this, steps));
            }

            public void onFailure(@NonNull Call<List<Step>> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(MapsActivity.this, "Error on getAllSteps " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //PRESENTATION METHODS

    //Fetch all Pois from the server and show them on Map as Markers
    private void getAllPois() {
        Call<List<Poi>> call = poiService.getAll();
        call.enqueue(new Callback<List<Poi>>() {
            public void onResponse(@NonNull Call<List<Poi>> call, @NonNull Response<List<Poi>> response) {
                List<Poi> pois = response.body();
                if (pois != null) {
                    for (Poi poi : pois) {
                        LatLng poiPosition = new LatLng(poi.getLatitude(), poi.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(poiPosition).title(poi.getName()).icon(defaultMarker()).snippet(poi.getDescription()));
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Error: cant find any point of interest!", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(@NonNull Call<List<Poi>> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        LatLng rome = new LatLng(41.902783, 12.496366);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rome));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rome, 11));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                Step temp = new Step(marker.getTitle(), marker.getSnippet(), position.latitude, position.longitude, null);
                currentTempStep = temp;
                tvStepName.setText(temp.getName());
                tvStepDescription.setText(temp.getDescription());
                return false;
            }
        });
    }
}
