package com.francescoxx.plango.frontend.poi;

/*
    Topics Adapter:
    used to show Topic Objects (usually on a listView)
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.francescoxx.plango.frontend.R;

import java.util.List;

public class PoiAdapter extends ArrayAdapter<Poi> {

    private Context context;
    private List<Poi> values;

    public PoiAdapter(Context context, List<Poi> values) {
        super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_two_texts, parent, false);
        }
        TextView tv1 = row.findViewById(R.id.tvTwoTexts1);
        TextView tv2 = row.findViewById(R.id.tvTwoTexts2);
        Poi item = values.get(position);

        tv1.setText(item.getName());
        tv2.setText(item.getDescription());

        //Log.d("retro", "setting: " + item.getName() + " " + item.getDescription());
        return row;
    }
}
