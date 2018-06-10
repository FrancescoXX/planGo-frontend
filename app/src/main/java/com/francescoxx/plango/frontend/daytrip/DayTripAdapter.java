package com.francescoxx.plango.frontend.daytrip;

/*
    Topics Adapter:
    used to show Topic Objects (usually on a listView)
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.francescoxx.plango.frontend.R;
import com.francescoxx.plango.frontend.model.Daytrip;

import java.util.List;

public class DayTripAdapter extends ArrayAdapter<Daytrip> {

    private Context context;
    private List<Daytrip> values;

    public DayTripAdapter(Context context, List<Daytrip> values) {
        super(context, R.layout.activity_main2, values);
        this.context = context;
        this.values = values;
    }

    @Nullable
    @Override
    public Daytrip getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_daytrip, parent, false);
        }
        TextView tvDaytripName = row.findViewById(R.id.tvDaytripName);
        TextView tvDaytripDescription = row.findViewById(R.id.tvDayTripDescription);
        Daytrip item = values.get(position);

        tvDaytripName.setText(item.getName());
        tvDaytripDescription.setText(item.getDescription());

        //Log.d("retro", "setting: " + item.getName() + " " + item.getDescription());
        return row;
    }
}
