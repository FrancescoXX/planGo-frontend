package com.francescoxx.plango.frontend.step;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.francescoxx.plango.frontend.R;
import com.francescoxx.plango.frontend.model.Step;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Step> listaElementi = new ArrayList<>();
    private Context mycontext;

    public MyAdapter(Context mycontext) {
        this.mycontext = mycontext;
    }

    @Override
    public int getCount() {
        return listaElementi.size();
    }

    @Override
    public Object getItem(int position) {
        return listaElementi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0; //NON USATO
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Step el = listaElementi.get(position);
        LayoutInflater inflater = LayoutInflater.from(mycontext);
        View row;

        if (convertView == null) {
            row = inflater.inflate(R.layout.row_step, parent, false);
        } else{
            row = convertView;
        }

        row = inflater.inflate(R.layout.row_step, parent, false);

        //Row layout Fields
        TextView name = row.findViewById(R.id.tvStepName);
        TextView desc = row.findViewById(R.id.tvStepDescription);

        name.setText(el.getName());
        desc.setText(el.getDescription());

        return row;
    }

    //Metodi dell'adapter

    //AGGIUNGE L'ELEMENTO ALLA LISTA
    public void addElement(Step newElement){
        if (!newElement.getName().equals("")){ //non fa inserire l'elemento stringa vuota
            listaElementi.add(newElement);
            notifyDataSetChanged();
        }
    }

    //RIMUOVE TUTTI GLI ELEMENTI
    public  void removeAll(){
        listaElementi.clear();
        notifyDataSetChanged();
    }

    /*
    //NON USATO PER ORA, RIMUOVE TUTTI GLI ELEMENTI DELLA LISTA BARRATI
    public void removeActive(){

        List<ElementoLista> newList = new ArrayList<>();
        for (ElementoLista el: listaElementi) {
            if (!el.isActive()){
                newList.add(el);
            }
            listaElementi = newList;
            notifyDataSetChanged();
        }
    }

    /*POTREBBE ESSERE UTILE PER UN ALTRO ESEMPIO. COSTRUTTORE CON ALTRI PARAMETRI*//*
    public MyAdapter(List<ElementoLista> listaElementi, Context mycontext) {
        this.listaElementi = listaElementi;
        this.mycontext = mycontext;
    }*/
}
