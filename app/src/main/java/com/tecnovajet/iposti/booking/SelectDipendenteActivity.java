package com.tecnovajet.iposti.booking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.facilities.Servizio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SelectDipendenteActivity extends AppCompatActivity {

    private ProgressBar loading;
    private ListView dipendentiListView;
    private TextView nome, tipo, prezzo, giorno, ora;
    private Button next, previous;
    private int previousSelected = -1;

    private ArrayList<DipendenteModel> dipendentiList;
    private DipendenteAdapter adapter;

    private Servizio servizio;

    private static String urlPrenotazione = "OMITTED/addprenotazione.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dipendente);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        servizio = (Servizio) getIntent().getSerializableExtra("Servizio");
        final String oraInizio = getIntent().getStringExtra("oraInizio");
        final String data = getIntent().getStringExtra("dateString");
        long date = getIntent().getLongExtra("dateLong", 0);

        final String giornoString = Utils.dateForDb(new Date(date));

        loading = findViewById(R.id.progressBar);
        dipendentiListView = findViewById(R.id.dipendentiList);

        nome = findViewById(R.id.nome);
        tipo = findViewById(R.id.tipo);
        prezzo = findViewById(R.id.prezzo);
        giorno = findViewById(R.id.giorno);
        ora = findViewById(R.id.ora);

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        nome.setText(servizio.getNome());
        tipo.setText(servizio.getCategoria());
        prezzo.setText(String.format(Locale.ITALIAN, "%.2f€", servizio.getPrezzo()));

        giorno.setText(data);
        ora.setText(oraInizio);

        next.setEnabled(false);

        Calendar c = Calendar.getInstance();
        int currentDay = Utils.formatDayOfWeek(c.get(Calendar.DAY_OF_WEEK));

        Date timeStep = Utils.stringToTime(servizio.getDurata());
        c.setTime(timeStep);

        int nHours = c.get(Calendar.HOUR);
        int nMinutes = c.get(Calendar.MINUTE);

        c.setTime(Utils.stringToTime1(oraInizio));
        c.add(Calendar.MINUTE, nMinutes);
        c.add(Calendar.HOUR, nHours);

        final String oraFine = Utils.timeToString(c.getTime());

        getData(servizio.getIdServizio(), currentDay, Utils.dateForDb(new Date()), oraInizio, oraFine);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new addPrenotazione(oraInizio, giornoString, oraFine, dipendentiList.get(previousSelected).getId(), servizio.getIdServizio(), MainActivity.email, data).execute();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDipendenteActivity.this.finish();
            }
        });

        dipendentiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (previousSelected != -1)
                    dipendentiList.get(previousSelected).setSelected(false);
                dipendentiList.get(i).setSelected(true);

                previousSelected = i;

                adapter.notifyDataSetChanged();

                next.setEnabled(true);
            }
        });
    }

    public void getData(final int servizioId, final int nGiorno, final String giorno, final String oraInizio, final String oraFine){
        //IF INTERNET CONNECTION IS OK
        if (Utils.isOnline(SelectDipendenteActivity.this)){
            new SelectDipendenteActivity.getDipendenti(servizioId, nGiorno, giorno, oraInizio, oraFine).execute();
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData(servizioId, nGiorno, giorno, oraInizio, oraFine);
                }
            });
            mySnackbar.show();
        }
    }

   /* @Override
    public void onItemClick(DipendenteModel dipendente) {
        Log.d("selected", dipendente.getName());
        selectedDipendente = dipendente;
        next.setEnabled(true);
    }*/

    private class getDipendenti extends AsyncTask<Void, Void, Void> {

        int idServizio, nGiorno;
        String giorno, oraInizio, oraFine;

        String urlDipendenti = "OMITTED/getdipendentiforprenotazione.php";

        private getDipendenti(int idServizio, int nGiorno, String giorno, String oraInizio, String oraFine){
            this.idServizio = idServizio;
            this.nGiorno = nGiorno;
            this.giorno = giorno;
            this.oraInizio = oraInizio;
            this.oraFine = oraFine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);

            dipendentiList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlDipendenti + "?idservizio=" + idServizio + "&ngiorno=" + nGiorno + "&giorno=" + giorno + "&orainizio=" + oraInizio + "&orafine=" + oraFine;
            String jsonStr = sh.makeServiceCall(url);
            Log.d("aaa", url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray orari = jsonObj.getJSONArray("items");
                    for (int i = 0; i < orari.length(); i++) {

                        JSONObject c = orari.getJSONObject(i);
                        int id = c.getInt("IDDipendente");
                        String nome = c.getString("Nome");

                        Log.d("aaa", id + " - " + nome);

                        dipendentiList.add(new DipendenteModel(id, nome, false));

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

            if (dipendentiList.size() > 0)
                dipendentiList.add(0, new DipendenteModel(-1, "Qualsiasi dipendente", false));

            loading.setVisibility(View.GONE);

            adapter = new DipendenteAdapter(SelectDipendenteActivity.this, dipendentiList);

            dipendentiListView.setAdapter(adapter);
        }
    }

    private class addPrenotazione extends AsyncTask<Void, Void, Void> {

        String orainizio, giorno, orafine, email, data;
        int idDipendente, idServizio;
        boolean res;

        private addPrenotazione(String orainizio, String giorno, String orafine, int idDipendente, int idServizio, String email, String data){
            this.orainizio = orainizio;
            this.giorno = giorno;
            this.orafine = orafine;
            this.idDipendente = idDipendente;
            this.idServizio = idServizio;
            this.email = email;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlPrenotazione + "?orainizio=" + orainizio + "&giorno=" + giorno
                    + "&orafine=" + orafine + "&iddipendente=" + idDipendente + "&idservizio=" + idServizio + "&email=" + email;
            String result = sh.makeServiceCall(url);

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
                //ExampleDialog.display(getSupportFragmentManager());
                ExampleDialog dialog = new ExampleDialog();

                Bundle bundle = new Bundle();
                bundle.putSerializable("servizio", servizio);
                bundle.putString("ora", orainizio);
                bundle.putString("giorno", data);

                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "ConfirmDialog");
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
