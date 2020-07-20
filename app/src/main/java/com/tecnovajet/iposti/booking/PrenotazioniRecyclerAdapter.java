package com.tecnovajet.iposti.booking;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.badoualy.stepperindicator.StepperIndicator;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;

import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PrenotazioniRecyclerAdapter extends RecyclerView.Adapter<PrenotazioniRecyclerAdapter.ViewHolder>{

    private ArrayList<BookingModel> list;
    private Context context;
    private OnPrenotazioneClickListener clickListener;

    public PrenotazioniRecyclerAdapter(Context context, ArrayList<BookingModel> list, OnPrenotazioneClickListener clickListener){
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prenotazione, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final BookingModel booking = list.get(position);

        holder.bind(booking, clickListener, context);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView struttura, nome, tipo, prezzo, ora, giorno;
        private StepperIndicator indicator;
        private ImageView menu;

        private ViewHolder(View itemView) {
            super(itemView);
            indicator = itemView.findViewById(R.id.indicator);
            struttura = itemView.findViewById(R.id.struttura);
            nome = itemView.findViewById(R.id.nome);
            tipo = itemView.findViewById(R.id.tipo);
            prezzo = itemView.findViewById(R.id.prezzo);
            ora = itemView.findViewById(R.id.ora);
            giorno = itemView.findViewById(R.id.giorno);
            menu = itemView.findViewById(R.id.menu);
        }

        public void bind(final BookingModel booking, final OnPrenotazioneClickListener clickListener, final Context context) {
            struttura.setText(booking.getNomeStruttura());
            nome.setText(booking.getNome());
            tipo.setText(booking.getCategoria());
            prezzo.setText(String.format(Locale.ITALIAN, "%.2f€", booking.getPrezzo()));
            ora.setText(Utils.timeToString(Utils.stringToTime(booking.getOraInizio())));
            giorno.setText(Utils.formatDateForDb(Utils.parseDateForDb(booking.getGiorno())));

            if (booking.getStato() == 2) {
                indicator.setLabels(context.getResources().getTextArray(R.array.stepLabelsError));
                indicator.setCurrentStep(2);
                indicator.setDoneIcon(ContextCompat.getDrawable(context, R.drawable.ic_close_white));
            }else if(booking.getCancellazione() == 1){
                indicator.setLabels(context.getResources().getTextArray(R.array.stepLabelsDeleteRequested));
                indicator.setCurrentStep(1);
            } else
                indicator.setCurrentStep(booking.getStato() + 1);

            if(booking.getStato() == 2 || booking.getCancellazione() == 1)
                menu.setVisibility(View.INVISIBLE);
            else
                menu.setVisibility(View.VISIBLE);

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, menu);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_prenotazione, popup.getMenu());

                    if (booking.getStato() == 0)
                        popup.getMenu().getItem(0).setTitle("Cancella prenotazione");
                    else
                        popup.getMenu().getItem(0).setTitle("Richiedi cancellazione");

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if (menuItem.getItemId() == R.id.deleteBooking){

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        clickListener.onItemClick(booking);
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });

                                final AlertDialog dialog = builder.create();
                                dialog.setMessage("Vuoi annullare la prenotazione?\n\nSe la prenotazione è già stata confermata, l'annullamento deve essere approvato dalla struttura.");
                                dialog.setTitle("Attenzione");
                                dialog.show();
                            }


                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }
    }
}