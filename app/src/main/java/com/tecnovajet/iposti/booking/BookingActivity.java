package com.tecnovajet.iposti.booking;

import androidx.appcompat.app.AppCompatActivity;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import android.content.Intent;
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

public class BookingActivity extends AppCompatActivity {

    private ProgressBar loading;
    private Servizio servizio;
    private ListView hoursList;
    private int previousSelected = -1;

    private ArrayList<HourModel> orariList;

    private HourAdapter itemsAdapter;

    private TextView nome, tipo, prezzo, giorno, ora;
    private Button next, previous;

    private String oraInizio;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loading = findViewById(R.id.progressBar);
        hoursList = findViewById(R.id.hoursList);

        nome = findViewById(R.id.nome);
        tipo = findViewById(R.id.tipo);
        prezzo = findViewById(R.id.prezzo);
        giorno = findViewById(R.id.giorno);
        ora = findViewById(R.id.ora);

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        servizio = (Servizio) getIntent().getSerializableExtra("Servizio");

        nome.setText(servizio.getNome());
        tipo.setText(servizio.getCategoria());
        prezzo.setText(String.format(Locale.ITALIAN, "%.2f€", servizio.getPrezzo()));

        next.setEnabled(false);

        Calendar startDate = Calendar.getInstance();
        selectedDate = startDate.getTimeInMillis();
        String month = org.apache.commons.lang3.StringUtils.capitalize(Utils.getMonth(startDate.getTime()));
        getSupportActionBar().setTitle(month);

        /* ends after 3 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 3);

        int currentDay = Utils.formatDayOfWeek(startDate.get(Calendar.DAY_OF_WEEK));

        setDate(startDate.get(Calendar.DAY_OF_MONTH), month);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()    // starts configuration.
                .showTopText(false)              // show or hide TopText (default to true)
                .end()
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                selectedDate = date.getTimeInMillis();

                String month = org.apache.commons.lang3.StringUtils.capitalize(Utils.getMonth(date.getTime()));
                getSupportActionBar().setTitle(month);

                setDate(date.get(Calendar.DAY_OF_MONTH), month);

                next.setEnabled(false);
                ora.setText("");

                getData(servizio.getIdStruttura(), Utils.formatDayOfWeek(date.get(Calendar.DAY_OF_WEEK)));
            }
        });

        hoursList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                oraInizio = orariList.get(i).getHour();

                if (previousSelected != -1)
                    orariList.get(previousSelected).setSelected(false);
                orariList.get(i).setSelected(true);

                previousSelected = i;

                itemsAdapter.notifyDataSetChanged();

                ora.setText(oraInizio);
                next.setEnabled(true);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingActivity.this.finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectActivity = new Intent(BookingActivity.this, SelectDipendenteActivity.class);
                selectActivity.putExtra("Servizio", servizio);
                selectActivity.putExtra("oraInizio", oraInizio);
                selectActivity.putExtra("dateLong", selectedDate);
                selectActivity.putExtra("dateString", giorno.getText().toString());

                startActivity(selectActivity);
            }
        });

        getData(servizio.getIdStruttura(), currentDay);
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

    public void setDate(int giornoInt, String mese){
        giorno.setText(giornoInt + " " + mese);
    }

    public void getData(final int strutturaId, final int day){
        //IF INTERNET CONNECTION IS OK
        if (Utils.isOnline(BookingActivity.this)){
            new getOrario(strutturaId, day).execute();
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData(strutturaId, day);
                }
            });
            mySnackbar.show();
        }

    }

    private class getOrario extends AsyncTask<Void, Void, Void> {

        int id, day;

        String urlOrario = "OMITTED/getorariostrutturabyid.php";

        private getOrario(int id, int day){
            this.id = id;
            this.day = day;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);

            orariList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlOrario + "?id=" + id + "&day=" + day;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                Date timeStep = Utils.stringToTime(servizio.getDurata());

                Calendar stepTime = Calendar.getInstance();
                stepTime.setTime(timeStep);

                int nHours = stepTime.get(Calendar.HOUR);
                int nMinutes = stepTime.get(Calendar.MINUTE);

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray orari = jsonObj.getJSONArray("items");
                    for (int i = 0; i < orari.length(); i++) {

                        JSONObject c = orari.getJSONObject(i);
                        String inizio = c.getString("inizio");
                        String fine = c.getString("fine");

                        Date startTime = Utils.stringToTime(inizio);
                        Date endTime = Utils.stringToTime(fine);

                        Calendar time = Calendar.getInstance();
                        time.setTime(startTime);

                        while(time.getTime().before(endTime)){

                            orariList.add(new HourModel(Utils.timeToString(time.getTime()), servizio.getPrezzo()));

                            time.add(Calendar.MINUTE, nMinutes);
                            time.add(Calendar.HOUR, nHours);
                        }

                        //struttura = new Struttura(id, nome, descrizione, via, numero, cap, citta, tipologia);
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

            itemsAdapter = new HourAdapter(BookingActivity.this, orariList);

            hoursList.setAdapter(itemsAdapter);
        }
    }
}
