package com.tecnovajet.iposti.facilities;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.lastminute.LastMinute;
import com.tecnovajet.iposti.lastminute.LastMinuteDetailActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LastMinuteFacilityRecyclerAdapter extends RecyclerView.Adapter<LastMinuteFacilityRecyclerAdapter.ViewHolder>{

    private ArrayList<LastMinute> list;
    private Context context;

    public LastMinuteFacilityRecyclerAdapter(Context context, ArrayList<LastMinute> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_last_minute_struttura, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final LastMinute lastMinute = list.get(position);

        holder.bind(lastMinute, context, position, list.size());
    }

    /*@Override
    public int getItemViewType(int position) {
        return list.get(position).getTipo();
    }*/

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nome, nomeLM, dataLM, newPrezzo, oldPrezzo;
        private View divider;

        private ViewHolder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nome);
            nomeLM = itemView.findViewById(R.id.nomeLM);
            dataLM = itemView.findViewById(R.id.dataLM);
            newPrezzo = itemView.findViewById(R.id.newPrezzo);
            oldPrezzo = itemView.findViewById(R.id.oldPrezzo);
            divider = itemView.findViewById(R.id.divider7);
        }

        public void bind (final LastMinute lastMinute, final Context context, final int position, final int size){
            nome.setText(lastMinute.getDescrizioneLastminute());

            String time = Utils.timeToString(Utils.stringToTime(lastMinute.getOra()));
            String date = "";
            if (lastMinute.getGiorno().equals(Utils.dateForDb(new Date())))
                date = "Oggi";
            else
                date = Utils.formatDateForDb(Utils.parseDateForDb(lastMinute.getGiorno()));

            nomeLM.setText(String.format("%s (%s)", lastMinute.getNomeServizio(), lastMinute.getCategoria()));
            dataLM.setText(String.format("%s | %s", date, time));

            if (lastMinute.getNewPrezzo() == -1) {
                newPrezzo.setText(String.format(Locale.ITALIAN, "€ %.2f", lastMinute.getPrezzo()));
                oldPrezzo.setVisibility(View.INVISIBLE);
            }else {
                newPrezzo.setText(String.format(Locale.ITALIAN, "€ %.2f", lastMinute.getNewPrezzo()));
                oldPrezzo.setText(String.format(Locale.ITALIAN, "era € %.2f", lastMinute.getPrezzo()), TextView.BufferType.SPANNABLE);
                oldPrezzo.setVisibility(View.VISIBLE);
                //oldPrezzo.setPaintFlags(oldPrezzo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Spannable spannable = (Spannable) oldPrezzo.getText();
                spannable.setSpan(new StrikethroughSpan(), 4, oldPrezzo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                newPrezzo.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }

            if (position == size-1)
                divider.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, LastMinuteDetailActivity.class);
                    i.putExtra("id", lastMinute.getIdLastMinute());
                    context.startActivity(i);
                }
            });
        }
    }
}