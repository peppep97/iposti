package com.tecnovajet.iposti.booking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecnovajet.iposti.R;

import java.util.ArrayList;

public class HourAdapter extends ArrayAdapter<HourModel> {

    private final ArrayList<HourModel> hours;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView hourText, priceText;
        ImageView selected;
    }

    public HourAdapter(Context context, ArrayList<HourModel> hours) {

        super(context, R.layout.item_hour, hours);
        this.hours = hours;

        inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_hour, parent, false);

            viewHolder.hourText = convertView.findViewById(R.id.hourText);
            //viewHolder.priceText = convertView.findViewById(R.id.priceText);
            viewHolder.selected = convertView.findViewById(R.id.selected);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.hourText.setText(hours.get(position).getHour());

        //String prezzo = String.format(Locale.ITALIAN, "€ %.2f", hours.get(position).getPrice());
        //viewHolder.priceText.setText(prezzo);
        if (hours.get(position).isSelected()){
            viewHolder.selected.setVisibility(View.VISIBLE);
        }else{
            viewHolder.selected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}