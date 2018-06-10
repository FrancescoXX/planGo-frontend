package com.francescoxx.plango.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.francescoxx.plango.frontend.model.Step;
import com.francescoxx.plango.frontend.remote.ApiUtils;
import com.francescoxx.plango.frontend.remote.StepService;
import com.francescoxx.plango.frontend.step.StepAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class GoMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //State
    private GoogleMap googleMap;
    private int currentUserId;
    private int currentDaytripId;

    //Services
    StepService stepService = ApiUtils.getStepService();

    //Layout Elements
    ListView lvGoDaytrip;
    ImageButton btnLista;
    ImageButton btnBack;

    //MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // hide the top bar
        setContentView(R.layout.activity_go_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //BUNDLE INFO
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUserId = extras.getInt("jwtuserId");
            currentDaytripId = extras.getInt("daytripId");
        }

        btnLista = (ImageButton) findViewById(R.id.btnLista);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        lvGoDaytrip = (ListView) findViewById(R.id.lvGoDaytrip);


        //BUTTON LISTENERS
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GoMapsActivity.this, "lista is coming", Toast.LENGTH_SHORT).show();
            }
        });

        //Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(GoMapsActivity.this, com.francescoxx.plango.frontend.Main2Activity.class);
                intent.putExtra("loggedUserId", currentUserId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllSteps(currentDaytripId);
    }

    private void getAllSteps(int daytripIndex) {
        Call<List<Step>> call = stepService.getAll(currentUserId, daytripIndex);
        call.enqueue(new Callback<List<Step>>() {
            public void onResponse(@NonNull Call<List<Step>> call, @NonNull Response<List<Step>> response) {
                List<Step> steps = response.body();
                lvGoDaytrip.setAdapter(new StepAdapter(GoMapsActivity.this, steps));
                //Toast.makeText(GoMapsActivity.this, "polists is " + steps, Toast.LENGTH_SHORT).show();

                //draw the list of Steps on the Map as Markers
                for (Step step : steps) {
                    LatLng poiPosition = new LatLng(step.getLatitude(), step.getLongitude());
                    //Toast.makeText(GoMapsActivity.this, "ADDING" + poiPosition, Toast.LENGTH_SHORT).show();
                    googleMap.addMarker(new MarkerOptions().position(poiPosition).title(step.getName()).icon(defaultMarker(HUE_GREEN)).snippet(step.getDescription()));
                }
            }
            public void onFailure(@NonNull Call<List<Step>> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(GoMapsActivity.this, "Error on getAllSteps " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        final LatLng rome = new LatLng(41.902783, 12.496366);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(rome));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rome, 11));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                Step temp = new Step(marker.getTitle(), marker.getSnippet(), position.latitude, position.longitude, null);

                //zoom to relative step
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 11));

                return false;
            }
        });
    }

}
