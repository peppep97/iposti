package com.tecnovajet.iposti.lastminute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class LastMinuteDetailActivity extends AppCompatActivity {

    private static String urlLastminute = "OMITTED/getlastminutebyid.php";
    private static String urlPrenotazione = "OMITTED/addprenotazionelastminute.php";

    private ProgressBar loading;

    private TextView nomeStrutturaText;
    private TextView indirizzoText;
    private TextView nomeText;
    private TextView prezzoText;
    private TextView oldPrezzoText;
    private TextView oraText;
    private TextView giornoText;

    private Button prenota;

    private CardView card;
    private LastMinute lastMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_minute_detail);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int lastminuteId = getIntent().getIntExtra("id", -1);

        loading = findViewById(R.id.progressBar);

        card = findViewById(R.id.card);

        nomeStrutturaText = findViewById(R.id.nomeText);
        indirizzoText = findViewById(R.id.indirizzoText);
        nomeText = findViewById(R.id.nome);
        prezzoText = findViewById(R.id.prezzo);
        oldPrezzoText = findViewById(R.id.oldPrezzo);
        giornoText = findViewById(R.id.giorno);
        oraText = findViewById(R.id.ora);

        prenota = findViewById(R.id.prenota);

        getData(lastminuteId);

        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPrenotazione();
            }
        });
    }

    public void getData(final int lastminuteId){
        //IF INTERNET CONNECTION IS OK
        if (Utils.isOnline(LastMinuteDetailActivity.this)){
            new getLastMinute(lastminuteId).execute();
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData(lastminuteId);
                }
            });
            mySnackbar.show();
        }
    }

    public void addPrenotazione(){

        if (Utils.isOnline(LastMinuteDetailActivity.this)){
            new addPrenotazioneLastminute(lastMinute.getOra(), lastMinute.getGiorno(), lastMinute.getOra(), -1, lastMinute.getIdServizio(), MainActivity.email, lastMinute.getIdLastMinute()).execute();
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPrenotazione();
                }
            });
            mySnackbar.show();
        }

    }

    private class addPrenotazioneLastminute extends AsyncTask<Void, Void, Void> {

        String orainizio, giorno, orafine, email;
        int idDipendente, idServizio, idLastminute;
        boolean res;

        private addPrenotazioneLastminute(String orainizio, String giorno, String orafine, int idDipendente, int idServizio, String email, int idLastminute){
            this.orainizio = orainizio;
            this.giorno = giorno;
            this.orafine = orafine;
            this.idDipendente = idDipendente;
            this.idServizio = idServizio;
            this.email = email;
            this.idLastminute = idLastminute;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlPrenotazione + "?orainizio=" + orainizio + "&giorno=" + giorno
                    + "&orafine=" + orafine + "&idservizio=" + idServizio + "&email=" + email + "&idlastminute=" + idLastminute;
            String result = sh.makeServiceCall(url);

            Log.d("aaa", url);

           if (result != null) {
                Log.d("result", result);
                if (result.trim().equals("ok"))
                    res = true;
                else
                    res = false;
            } else {
                Log.e("JSON", "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (res){
                ConfirmDialog dialog = new ConfirmDialog();

                Bundle bundle = new Bundle();
                bundle.putString("struttura", lastMinute.getNomeStruttura());
                bundle.putString("nome", lastMinute.getNomeServizio());
                bundle.putString("tipo", lastMinute.getCategoria());
                bundle.putDouble("prezzo", lastMinute.getPrezzo());
                bundle.putDouble("newprezzo", lastMinute.getNewPrezzo());
                bundle.putString("ora", oraText.getText().toString());
                bundle.putString("giorno", giornoText.getText().toString());

                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "ConfirmDialog");
            }
        }
    }

    private class getLastMinute extends AsyncTask<Void, Void, Void> {

        int id;

        private getLastMinute(int id){
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlLastminute + "?id=" + id;
            String jsonStr = sh.makeServiceCall(url);

            Log.d("url", url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray lastminutes = jsonObj.getJSONArray("items");
                    for (int i = 0; i < lastminutes.length(); i++) {

                        JSONObject c = lastminutes.getJSONObject(i);
                        int idStruttura = c.getInt("IDStruttura");
                        String nomeStruttura = c.getString("NomeStruttura");
                        String tipologia = c.getString("Tipo");
                        int idLastMinute = c.getInt("IDLastminute");
                        String ora = c.getString("Ora");
                        String giorno = c.getString("Giorno");
                        int idServizio = c.getInt("IDServizio");
                        double newPrezzo = c.getDouble("Sconto");
                        String descrizioneLastminute = c.getString("DescrizioneLM");
                        String nomeServizio = c.getString("NomeServizio");
                        String descrizioneServizio = c.getString("DescrizioneS");
                        double prezzo = c.getDouble("Prezzo");
                        String categoria = c.getString("Categoria");

                        String via = c.getString("Via");
                        String numero = c.getString("Numero");
                        String cap = c.getString("CAP");
                        String citta = c.getString("Citta");

                        String indirizzo = String.format("%s, %s, %s %s", via, numero, cap, citta);

                        Log.d("aaa", nomeStruttura);

                        lastMinute = new LastMinute(idStruttura, nomeStruttura, tipologia, indirizzo, idLastMinute, ora, giorno, idServizio, newPrezzo, descrizioneLastminute, nomeServizio, descrizioneServizio, prezzo, categoria);
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON", "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            loading.setVisibility(View.GONE);

            setData(lastMinute);
        }
    }

    public void setData(LastMinute lastMinute){

        nomeStrutturaText.setText(lastMinute.getNomeStruttura());
        indirizzoText.setText(lastMinute.getIndirizzo());

        String time = Utils.timeToString(Utils.stringToTime(lastMinute.getOra()));
        String date = Utils.formatDateForDb(Utils.parseDateForDb(lastMinute.getGiorno()));

        nomeText.setText(String.format("%s (%s)", lastMinute.getNomeServizio(), lastMinute.getCategoria()));
        giornoText.setText(date);
        oraText.setText(time);

        if (lastMinute.getNewPrezzo() == -1) {
            prezzoText.setText(String.format(Locale.ITALIAN, "€ %.2f", lastMinute.getPrezzo()));
            oldPrezzoText.setVisibility(View.INVISIBLE);
        }else {
            prezzoText.setText(String.format(Locale.ITALIAN, "€ %.2f", lastMinute.getNewPrezzo()));
            oldPrezzoText.setText(String.format(Locale.ITALIAN, "era € %.2f", lastMinute.getPrezzo()), TextView.BufferType.SPANNABLE);
            oldPrezzoText.setVisibility(View.VISIBLE);
            Spannable spannable = (Spannable) oldPrezzoText.getText();
            spannable.setSpan(new StrikethroughSpan(), 4, oldPrezzoText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        card.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
