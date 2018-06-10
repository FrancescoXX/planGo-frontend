package com.francescoxx.plango.frontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.francescoxx.plango.frontend.model.Step;
import com.francescoxx.plango.frontend.poi.Poi;
import com.francescoxx.plango.frontend.remote.ApiUtils;
import com.francescoxx.plango.frontend.remote.PoiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class FreeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 10; //per gestire i permessi
    public static final double DEFAULT_LAT = 41.850142;
    public static final double DEFAULT_LON = 12.597873;

    protected static Step utentePOI = new Step("user", "test", DEFAULT_LAT, DEFAULT_LON, null); //posizione default utenteMarker
    private Marker utenteMarker = null; //marker dell'utenteMarker. deve essere cancellato ogni volta che aggiorno la posizione
    private List<Poi> listaPois;

    //services da utilizzare
    PoiService poiService = ApiUtils.getPoiService();

    //Layout Elements
    ImageButton btnLista;
    ImageButton btnFocus;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnLista = findViewById(R.id.btnLista);
        btnFocus = findViewById(R.id.btnFocus);
        btnLogin = findViewById(R.id.btnLogin);

        //BUTTON LISTENERS
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Alla pressione del tasto converte i valori in un gson e li mette in un bundle che passa alla ListActivity
                Gson gson = new Gson(); //Crea un Gson
                String jsonArrayInStringa = gson.toJson(listaPois); //converte le lista in un JsonArray attraverso il metodo toJson
                Intent intentListActivity = new Intent(FreeActivity.this, ListActivity.class);
                intentListActivity.putExtra("jsonArray", jsonArrayInStringa); //mette la stringa in formato JSON con key "array" nel bundle

                //salva le coordinate dell'utente nel bundle
                intentListActivity.putExtra("utenteLat", utentePOI.getLatitude());
                intentListActivity.putExtra("utenteLon", utentePOI.getLongitude());

                Log.d("log", " FreeActivity -> ListActivity with: " + jsonArrayInStringa);
                startActivity(intentListActivity);
            }
        });
        btnFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                focusUtente();
                getAllPois();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FreeActivity.this, com.francescoxx.plango.frontend.LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);

        //request permission for pre 6.0
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //chedi la permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
        } else {
            //ok
            connessioneService();
        }
        */
        getAllPois();
    }

    //Fetch all Pois from the server and show them on Map as Markers
    private void getAllPois() {
        Call<List<Poi>> call = poiService.getAll();
        call.enqueue(new Callback<List<Poi>>() {
            public void onResponse(@NonNull Call<List<Poi>> call, @NonNull Response<List<Poi>> response) {
                List<Poi> pois = response.body();
                listaPois = pois;
                if (pois != null) {
                    //Add Marks from a List<Poi>. could be almost same to add Markers for a List<Step>
                    for (Poi poi : pois) {
                        LatLng poiPosition = new LatLng(poi.getLatitude(), poi.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(poiPosition).title(poi.getName()).icon(defaultMarker()).snippet(poi.getDescription()));
                    }
                } else {
                    Toast.makeText(FreeActivity.this, "Error: cant find any point of interest!", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(@NonNull Call<List<Poi>> call, @NonNull Throwable t) {
                Log.d("retro", "ERROR!");
                Toast.makeText(FreeActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo chiamato dopo aver avuto i risultati di onRequestPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permesso ok
                onStartUseLocation(); //metodo che crea il listener del gps e temporizza le chiamate
                focusUtente();
                //connessioneService(); //Connessione al servizio
            } else {
                Toast.makeText(FreeActivity.this, "Serve permesso gps", Toast.LENGTH_LONG).show();
                Log.d("test", "Permesso negato - chiusura Applicazione");
                finish();//chiude l'activity
            }
        }
    }

    //Crea il Listener e assegna la location
    private void onStartUseLocation() {
        Log.d("test", "onStartUseLocation");

        //sa a questo punto la mappa è ancora null bisogna riavviare l'activity per avere l'aggiornamento periodico
        if (mMap == null) {
            Log.d("test", "Mappa non pronta");
            return;
        }
        //focusUtente();

        //Creo un nuovo LocationListener
        LocationListener myLocationListener = new LocationListener() {
            //Snackbar snackBarGps;

            @Override
            public void onLocationChanged(Location location) {
                //cambiato ogni volta che cambiano i valori
                //Toast.makeText(FreeActivity.this, "LOCATION CHANGED", Toast.LENGTH_SHORT).show();
                useLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // da degli stati intermedi ad esempio numero di satelliti usati
            }

            @Override
            public void onProviderEnabled(String provider) {
                //gps abilitato
                /*
                if (snackBarGps != null) {
                    snackBarGps.dismiss(); //disabilita la snackbar se l'utente setta il gps da solo
                }
                */
            }

            @Override
            public void onProviderDisabled(String provider) {
                //gps disabilitato
                Log.d("PJDM", "GPS DISATTIVO");

                Toast.makeText(FreeActivity.this, "GPS disabilitato!", Toast.LENGTH_SHORT).show();
                /*

                snackBarGps = Snackbar.make(coordinatorLayout, "Gps Disattivo!", 20000).setAction("Attiva", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentSetGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intentSetGps);
                    }
                });
                snackBarGps.show();
                */
            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); //definisco il location Manager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//prende in argomento una stringa che è un provider
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7000, 0, myLocationListener); //aggiornamento
    }

    //Focus sull'ultima posizione conosciuta
    private void focusUtente() {
        //riscarica anche i punti
        getAllPois();
        LatLng temp = new LatLng(utentePOI.getLatitude(), utentePOI.getLongitude());
        addMarkerUtente(utentePOI); //ridisegna utente
        mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 11));
    }

    private void useLocation(Location location) {
        utentePOI.setLatitude(location.getLatitude());
        utentePOI.setLongitude(location.getLongitude());
        addMarkerUtente(utentePOI);
    }

    public void addMarkerUtente(Step p) {
        if (utenteMarker != null) {
            utenteMarker.remove();
        }
        LatLng utentePosition = new LatLng(p.getLatitude(), p.getLongitude());

        if (utentePosition.latitude != DEFAULT_LAT) {
            utenteMarker = mMap.addMarker(new MarkerOptions() //creazione marker
                    .position(utentePosition)//posizione utente
                    .title("sei qui") //"nome" dell'utenteMarker è "Sei qui"
                    .snippet("info") //snippet con info sul marker
                    .icon(defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }
}
