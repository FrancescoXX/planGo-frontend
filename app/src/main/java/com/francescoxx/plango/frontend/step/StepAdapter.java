package com.francescoxx.plango.frontend.step;

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
import com.francescoxx.plango.frontend.model.Step;

import java.util.List;

public class StepAdapter extends ArrayAdapter<Step> {

    private Context context;
    private List<Step> values;

    public StepAdapter(Context context, List<Step> values) {
        super(context, R.layout.activity_main2, values);
        this.context = context;
        this.values = values;
    }



    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_step, parent, false);
        }
        TextView tvStepName = row.findViewById(R.id.tvStepName);
        TextView tvStepDescription = row.findViewById(R.id.tvStepDescription);
        TextView tvStepLatitude = row.findViewById(R.id.tvStepLat);
        TextView tvStepLongitude = row.findViewById(R.id.tvStepLon);

        Step item = values.get(position);

        tvStepName.setText(item.getName());
        tvStepDescription.setText(item.getDescription());
        tvStepLatitude.setText(String.valueOf(item.getLatitude()));
        tvStepLongitude.setText(String.valueOf(item.getLongitude()));


        //Log.d("retro", "setting: " + item.getName() + " " + item.getDescription());
        return row;
    }
}
