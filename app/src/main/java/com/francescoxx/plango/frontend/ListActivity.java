package com.francescoxx.plango.frontend;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.francescoxx.plango.frontend.ListaAdapter;
import com.francescoxx.plango.frontend.model.Step;
import com.francescoxx.plango.frontend.poi.Poi;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.francescoxx.plango.frontend.FreeActivity.DEFAULT_LAT;

/*
 * ListActivity
 * Viene lanciata dalla una Map Activity attraverso la pressione del tasto in alto a destra
 * Ha una listview e utilizza un Adapter personalizzato
 * crea un Arraylist di <Poi> a partire dal bundle "array", che contiene i dati in forma JSON
 * utilizza la libreria GSON per decodificare il JSON
 *
 * chiama il metodo calcolaDist sull'istanza di ogni Poi
 * ogni istanza di Poi setta la sua distanza dall'utente
 *
 * si occupa di calcolare le distanze di tutti i i POI dall'utente e di visualizzarli in ordine di distanza crescente
 */
public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private List<Poi> listGenericPois = new ArrayList<>();
    private Step userListActivity;
    private AlphaAnimation itemClickAnimation = new AlphaAnimation(1F, 0.8F); //button animation on list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ListaAdapter adapter = new ListaAdapter(ListActivity.this);
        listView.setAdapter(adapter);

        //Prende i dati dell'utente dal bundlee crea un GenericPOI utente per calcolare tutte le distanze
        double uLat = getIntent().getExtras().getDouble("utenteLat");
        double uLon = getIntent().getExtras().getDouble("utenteLon");
        userListActivity = new Step("dummy", "dummy", uLat, uLon, null);

        //getIntent() prende l'intent che ha lanciato l'activity,
        // getExtras() prende la stringa "jsonAarray" dal bundle (è un JSONArray) e la assegna ad una variabile Stringa
        String jaInStringa = getIntent().getExtras().getString("jsonArray");

        //Crea un nuovo Gson. ha un metodo che trasforma in JSONArray in una lista di miei oggetti (GenericPois)
        Gson gson = new Gson();

        //CONVERSIONE con il metodo fromJson(String, Object[].class): converte la stringa contenente il JSONArray in un array di oggetti della classe Poi
        Poi[] arrayPois = gson.fromJson(jaInStringa, Poi[].class);

        //Assegna l'arrayColonnine all'ArrayList di colonnine della ListActivity
        listGenericPois = Arrays.asList(arrayPois);

        //Setta la distanza di tutte le colonnine nella lista dall'utente
        for (Poi c : listGenericPois) {
            double distanza = c.calcolaDist(userListActivity); //calcola la distanza con il metodo di Colonnina
            c.setDistanzaDaUtente(distanza); //setta la distanza di ogni Colonnina
        }

        //Ordina la lista secondo la distanza Colonnine. implementa Comparable per poter fare questo
        Collections.sort(listGenericPois);
        adapter.addElements(listGenericPois);


        //Al click dell'iesimo item naviga verso il punto. Prende le coordinate dell'utente
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userListActivity.getLatitude() == DEFAULT_LAT){
                    return;
                }

                //Setta il background dell'item Selezionato a grigio e tutti gli altri a bianco
                for (int j = 0; j < parent.getChildCount(); j++){
                    parent.getChildAt(j).setBackgroundColor(Color.WHITE);
                }
                view.setBackgroundColor(Color.LTGRAY);
                view.startAnimation(itemClickAnimation); //view animation

                //coordinate per il navigatore
                double latPoi = listGenericPois.get(position).getLatitude();
                double lonPoi = listGenericPois.get(position).getLongitude();

                //Lancia l'activity di Google Maps per navigare verso il punto.
                Intent intentGoogleMapNavigator = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?"
                        + "saddr=" + userListActivity.getLatitude() + "," + userListActivity.getLongitude()
                        + "&daddr=" + latPoi + "," + lonPoi));  //Coordinate punto cliccato
                intentGoogleMapNavigator.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intentGoogleMapNavigator);
            }
        });
    }

    //Back Button
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}