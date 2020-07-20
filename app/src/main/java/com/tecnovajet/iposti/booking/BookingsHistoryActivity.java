package com.tecnovajet.iposti.booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingsHistoryActivity extends AppCompatActivity implements OnPrenotazioneClickListener{

    private ProgressBar loading;
    private RecyclerView recyclerView;
    private RelativeLayout noBookings;

    private ArrayList<BookingModel> list;

    private String urlPrenotazioni = "OMITTED/getprenotazionihistory.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cronologia prenotazioni");
        }

        loading = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        noBookings = findViewById(R.id.noBookings);

        getData();
    }

    public void getData() {

        if (Utils.isOnline(this)){
            //new getPrenotazioniHistory().execute();
            getPrenotazioniHistory();
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData();
                }
            });
            mySnackbar.show();
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


    @Override
    public void onItemClick(BookingModel prenotazione) {

    }

    private void getPrenotazioniHistory(){
        try {
            list = new ArrayList<>();
            loading.setVisibility(View.VISIBLE);

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("email", MainActivity.email);

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, urlPrenotazioni, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObj) {

                    try {
                        JSONArray prenotazioni = jsonObj.getJSONArray("items");
                        for (int i = 0; i < prenotazioni.length(); i++) {

                            JSONObject c = prenotazioni.getJSONObject(i);
                            int id = c.getInt("IDPrenotazione");
                            String oraInizio = c.getString("OraInizio");
                            String giorno = c.getString("Giorno");
                            int stato = c.getInt("Stato");
                            String nome = c.getString("Nome");
                            String categoria = c.getString("Categoria");
                            double prezzo = c.getDouble("Prezzo");
                            String nomeStruttura = c.getString("NomeStruttura");
                            int cancellazione = c.getInt("Cancellazione");

                            list.add(new BookingModel(id, oraInizio, giorno, nome, categoria, nomeStruttura, stato, prezzo, cancellazione));
                        }
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }

                    loading.setVisibility(View.GONE);

                    if (list.size() > 0){
                        noBookings.setVisibility(View.GONE);

                        PrenotazioniRecyclerAdapter adapter = new PrenotazioniRecyclerAdapter(BookingsHistoryActivity.this, list, BookingsHistoryActivity.this);
                        LinearLayoutManager llm = new LinearLayoutManager(BookingsHistoryActivity.this);
                        llm.setOrientation(RecyclerView.VERTICAL);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setAdapter(adapter);
                    }else{
                        noBookings.setVisibility(View.VISIBLE);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("token", MainActivity.token);
                    return headers;
                }
            };
            Volley.newRequestQueue(this).add(jsonOblect);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
