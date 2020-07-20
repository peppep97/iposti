package com.tecnovajet.iposti.facilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecnovajet.iposti.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder>{

    private ArrayList<Struttura> list;
    private Context context;
    private OnItemClickListener clickListener;

    public ListRecyclerAdapter(Context context, ArrayList<Struttura> list, OnItemClickListener clickListener){
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_struttura, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Struttura struttura = list.get(position);

        holder.bind(struttura, clickListener);

        holder.miniatura.setVisibility(View.VISIBLE);

        String url = "OMITTED/strutture/" + struttura.getId() + ".jpeg";

        new DownloadImageTask(holder.miniatura).execute(url);
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
        private TextView nome, tipo, distanza;
        private ImageView preferito;
        private ImageView miniatura;

        private ViewHolder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nome);
            tipo = itemView.findViewById(R.id.tipo);
            distanza = itemView.findViewById(R.id.distanza);
            preferito = itemView.findViewById(R.id.preferito);
            miniatura = itemView.findViewById(R.id.imageView);
        }

        public void bind (final Struttura struttura, final OnItemClickListener clickListener){
            nome.setText(struttura.getNome());
            tipo.setText(struttura.getTipologia());

            String distanzaText;
            double distanzaD = struttura.getDistance();
            distanzaText = String.format(Locale.ITALIAN, "%.2f", distanzaD) + " km dalla tua posizione";
            if (distanzaD < 1) {
                distanzaD = distanzaD * 1000;
                distanzaText = (int) distanzaD + " m dalla tua posizione";
            }

            distanza.setText(distanzaText);

            if (struttura.getPreferito() == 1)
                preferito.setVisibility(View.VISIBLE);
            else
                preferito.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(struttura);
                }
            });
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            bmImage.setImageBitmap(null);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}