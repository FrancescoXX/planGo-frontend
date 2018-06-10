package com.francescoxx.plango.frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.francescoxx.plango.frontend.poi.Poi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.francescoxx.plango.frontend.FreeActivity.DEFAULT_LAT;

/**
 * Questa classe estende BaseAdapter
 * é una classe per visualizzare la lista di colonnine nella ListActivity all'interno della ListView
 * utilizza il layout riga list_row.xml
 */
public class ListaAdapter extends BaseAdapter {

    private List<Poi> listaAdapter = new ArrayList<>();
    private Context mycontext;
    private TextView nome;
    private TextView distanza;
    private TextView stato;
    private TextView tipo;

    public ListaAdapter(Context mycontext) {
        this.mycontext = mycontext;
    }

    @Override
    public int getCount() {
        return listaAdapter.size();
    }

    @Override
    public Object getItem(int position) {
        return listaAdapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Poi col = listaAdapter.get(position);
        LayoutInflater inflater = LayoutInflater.from(mycontext);
        View row;

        if (convertView == null) {
            inflater.inflate(R.layout.list_row, parent, false);
        }
        row = inflater.inflate(R.layout.list_row, parent, false);

        //RIFERIMENTI a list_row.xml
        nome = row.findViewById(R.id.elemView);
        distanza = row.findViewById(R.id.evDistanza);
        stato = row.findViewById(R.id.evStato);
        tipo = row.findViewById(R.id.evTipo);

        //Setta il nome e lo stato
        nome.setText(col.getName());
        stato.setText(col.getDescription());
        tipo.setText(col.getName());

        //Nuovo formato decimale prendendo solo 1 cifra dopo la virgola
        DecimalFormat decimalFormat = new DecimalFormat(); //format sono 1 cifre dopo la virgola
        decimalFormat.setMaximumFractionDigits(3);
        String distanzaFormattata = decimalFormat.format(col.getDistanzaDaUtente());
        distanza.setText(distanzaFormattata + " Km");

        if (FreeActivity.utentePOI.getLatitude() == DEFAULT_LAT) {
            distanza.setText("-");
        }
        notifyDataSetChanged();
        return row;
    }

    //AGGIUNGE L'ELEMENTO ALLA LISTA. metodo privato. non inserisce l'elemento se è già nella lista
    private void addElement(Poi newElement) {
        listaAdapter.add(newElement);
        notifyDataSetChanged();
    }

    //per aggiungere una List<Colonnina> alla lista dell'Adapter
    void addElements(List<Poi> colonninas) {
        pulisci();
        for (Poi item : colonninas) {
            addElement(item);
        }
        notifyDataSetChanged();
    }

    private void pulisci() {
        listaAdapter.clear();
    }
}
