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

public class DipendenteAdapter extends ArrayAdapter<DipendenteModel> {

    private final ArrayList<DipendenteModel> dipendenti;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView hourText;
        ImageView selected;
    }

    public DipendenteAdapter(Context context, ArrayList<DipendenteModel> dipendenti) {

        super(context, R.layout.item_hour, dipendenti);
        this.dipendenti = dipendenti;

        inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_hour, parent, false);

            viewHolder.hourText = convertView.findViewById(R.id.hourText);
            viewHolder.selected = convertView.findViewById(R.id.selected);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.hourText.setText(dipendenti.get(position).getName());

        if (dipendenti.get(position).isSelected()){
            viewHolder.selected.setVisibility(View.VISIBLE);
        }else{
            viewHolder.selected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}