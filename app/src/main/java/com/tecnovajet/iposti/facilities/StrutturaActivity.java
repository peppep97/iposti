package com.tecnovajet.iposti.facilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.booking.BookingActivity;
import com.tecnovajet.iposti.expandablelist.AnimatedExpandableListView;
import com.tecnovajet.iposti.lastminute.LastMinute;
import com.tecnovajet.iposti.lastminute.LastMinuteFragment;
import com.tecnovajet.iposti.lastminute.LastMinuteRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrutturaActivity extends AppCompatActivity {

    private static String urlStruttura = "OMITTED/getstrutturabyid.php";
    private static String urlServizi = "OMITTED/getservizibyid.php";
    private static String urlLastMinutes = "OMITTED/getlastminutestruttura.php";
    private static String urlPreferito = "OMITTED/setpreferito.php";

    private ProgressBar loading;
    private TextView nomeText;
    private TextView indirizzoText;
    private TextView descrizioneText;
    private TextView tipoText;
    private FloatingActionButton addPreferito;
    private ImageView header;

    private RecyclerView lastMinuteList;
    private RelativeLayout noLastMinutes;

    private AnimatedExpandableListView listView;
    private List<GroupItem> serviziGroups;

    private int cheight;
    private double lat;
    private double lon;
    private String strutturaName;
    private int preferito;

    private Struttura struttura;

    private ArrayList<LastMinute> lastMinutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_struttura);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int strutturaId = getIntent().getIntExtra("id", -1);
        strutturaName = getIntent().getStringExtra("name");

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(strutturaName);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        addPreferito = findViewById(R.id.fab);
        addPreferito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (preferito == 1)
                    new setPreferito(strutturaId, false).execute();
                else
                    new setPreferito(strutturaId, true).execute();

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        loading = findViewById(R.id.progressBar);

        nomeText = findViewById(R.id.nomeText);
        indirizzoText = findViewById(R.id.indirizzoText);
        descrizioneText = findViewById(R.id.descrizioneText);
        tipoText = findViewById(R.id.tipoText);
        listView = findViewById(R.id.expandableListView);
        header = findViewById(R.id.header);
        lastMinuteList = findViewById(R.id.lastminuteList);
        noLastMinutes = findViewById(R.id.noLastMinutes);

        getData(strutturaId);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                int n = serviziGroups.get(groupPosition).childServizi.size();

                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroup(groupPosition);

                    cheight = UIUtils.setListViewHeightBasedOnItems(listView, n, cheight, false);
                } else {
                    listView.expandGroup(groupPosition);
                    cheight = UIUtils.setListViewHeightBasedOnItems(listView, n, cheight, true);
                }
                return true;
            }

        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                Servizio servizio = serviziGroups.get(i).childServizi.get(i1);

                Intent bookActivity = new Intent(StrutturaActivity.this, BookingActivity.class);
                bookActivity.putExtra("Servizio", servizio);

                startActivity(bookActivity);

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_struttura, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_info:
                Intent i = new Intent(StrutturaActivity.this, StrutturaInfoActivity.class);
                i.putExtra("struttura", struttura);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getData(final int strutturaId){
        //IF INTERNET CONNECTION IS OK
        if (Utils.isOnline(StrutturaActivity.this)){
            new getStrutturaInfo(strutturaId).execute();
            //new getServizi(strutturaId).execute();
            getServizi(strutturaId);
            getLastMinutes(strutturaId);
            new DownloadImageTask(header).execute("OMITTED/strutture/" + strutturaId + ".jpeg");
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getData(strutturaId);
                }
            });
            mySnackbar.show();
        }

    }

    private class getStrutturaInfo extends AsyncTask<Void, Void, Void> {

        int id;

        private getStrutturaInfo(int id){
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

            String url = urlStruttura + "?id=" + id + "&email=" + MainActivity.email;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray strutture = jsonObj.getJSONArray("items");
                    for (int i = 0; i < strutture.length(); i++) {

                        JSONObject c = strutture.getJSONObject(i);
                        int id = c.getInt("IDStruttura");
                        String nome = c.getString("Nome");
                        String descrizione = c.getString("Descrizione");
                        String via = c.getString("Via");
                        String numero = c.getString("Numero");
                        String cap = c.getString("CAP");
                        String citta = c.getString("Citta");
                        String tipologia = c.getString("Tipo");
                        //double distanza = c.getDouble("distance");
                        lat = c.getDouble("Latitudine");
                        lon = c.getDouble("Longitudine");
                        preferito = c.getInt("preferito");

                        struttura = new Struttura(id, nome, descrizione, via, numero, cap, citta, tipologia, lat, lon, preferito);
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

            setData(struttura);
        }
    }

    public void setData(Struttura struttura){

        nomeText.setText(struttura.getNome());
        indirizzoText.setText(String.format("%s, %s, %s %s", struttura.getVia(), struttura.getNumero(), struttura.getCap(), struttura.getCitta()));
        //descrizioneText.setText(struttura.getDescrizione());
        tipoText.setText(struttura.getTipologia());

        if (struttura.getPreferito() == 1)
            addPreferito.setImageResource(R.drawable.ic_favorite_white);
        else
            addPreferito.setImageResource(R.drawable.ic_favorite_border_white);

    }

    private void getServizi(final int id){
        try {
            serviziGroups = new ArrayList<>();
            loading.setVisibility(View.VISIBLE);

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", String.valueOf(id));

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, urlServizi, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObj) {

                    try {
                        JSONArray categorie = jsonObj.getJSONArray("items");
                        for (int i = 0; i < categorie.length(); i++) {

                            JSONObject c = categorie.getJSONObject(i);
                            String categoria = c.getString("Categoria");
                            JSONArray servizi = c.getJSONArray("subitems");

                            Log.d("item", categoria);

                            GroupItem group = new GroupItem(categoria);

                            for (int j = 0; j < servizi.length(); j++){

                                JSONObject s = servizi.getJSONObject(j);
                                int idStruttura = s.getInt("IDStruttura");
                                int idServizio = s.getInt("IDServizio");
                                String nome = s.getString("Nome");
                                String descrizione = s.getString("Descrizione");
                                double prezzo = -1;
                                if (!s.isNull("Prezzo"))
                                    prezzo = s.getDouble("Prezzo");

                                String durata = s.getString("Durata");

                                group.addServizio(new Servizio(idStruttura, idServizio, nome, descrizione, prezzo, durata, categoria, strutturaName));
                            }

                            serviziGroups.add(group);

                        }
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }

                    loading.setVisibility(View.GONE);
                    if (serviziGroups.size() > 0){
                        ServiziAdapter adapter = new ServiziAdapter(StrutturaActivity.this, serviziGroups);

                        listView.setAdapter(adapter);

                        View item = listView.getAdapter().getView(0, null, listView);
                        item.measure(0, 0);

                        cheight = serviziGroups.size()*item.getMeasuredHeight();
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

    private void getLastMinutes(final int id){
        try {
            lastMinutes = new ArrayList<>();
            loading.setVisibility(View.VISIBLE);

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", String.valueOf(id));

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, urlLastMinutes, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObj) {

                    try {
                        JSONArray lastminutes = jsonObj.getJSONArray("items");
                        for (int i = 0; i < lastminutes.length(); i++) {

                            JSONObject c = lastminutes.getJSONObject(i);
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

                            lastMinutes.add(new LastMinute(idLastMinute, ora, giorno, idServizio, newPrezzo, descrizioneLastminute, nomeServizio, descrizioneServizio, prezzo, categoria));
                        }
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }

                    loading.setVisibility(View.GONE);

                    LastMinuteFacilityRecyclerAdapter adapter = new LastMinuteFacilityRecyclerAdapter(StrutturaActivity.this, lastMinutes);

                    LinearLayoutManager llm = new LinearLayoutManager(StrutturaActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    lastMinuteList.setLayoutManager(llm);
                    lastMinuteList.setAdapter(adapter);

                    if (lastMinutes.size() > 0){
                        noLastMinutes.setVisibility(View.GONE);
                    }else{
                        noLastMinutes.setVisibility(View.VISIBLE);
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

    private class setPreferito extends AsyncTask<Void, Void, Void> {

        int id;
        boolean add;

        private setPreferito(int id, boolean add){
            this.id = id;
            this.add = add;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String action = add ? "add" : "remove";
            String url = urlPreferito + "?idstruttura=" + id + "&email=" + MainActivity.email + "&action=" + action;
            String result = sh.makeServiceCall(url);

            if (result != null) {
                Log.d("result", result);
            } else {
                Log.e("JSON", "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (add){
                preferito = 1;
                addPreferito.setImageResource(R.drawable.ic_favorite_white);
            }else{
                preferito = 0;
                addPreferito.setImageResource(R.drawable.ic_favorite_border_white);
            }
            //setData(struttura);
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