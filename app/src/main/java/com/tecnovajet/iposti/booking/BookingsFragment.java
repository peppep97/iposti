package com.tecnovajet.iposti.booking;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.facilities.ListRecyclerAdapter;
import com.tecnovajet.iposti.facilities.Struttura;
import com.tecnovajet.iposti.favorites.FavoritesFragment;
import com.tecnovajet.iposti.lastminute.ConfirmDialog;
import com.tecnovajet.iposti.lastminute.LastMinuteDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingsFragment extends Fragment implements OnPrenotazioneClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private androidx.appcompat.app.ActionBar toolbar;
    private ProgressBar loading;
    private RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;

    private ArrayList<BookingModel> list;

    PrenotazioniRecyclerAdapter adapter;

    private String urlPrenotazioni = "OMITTED/getprenotazioni.php";
    private String urlDeletePrenotazione = "OMITTED/deleteprenotazione.php";

    private RelativeLayout noBookings;

    public BookingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingsFragment newInstance(String param1, String param2) {
        BookingsFragment fragment = new BookingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View content = inflater.inflate(R.layout.fragment_bookings, container, false);

        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setTitle("Le mie prenotazioni");
        toolbar.setSubtitle(null);
        toolbar.setElevation(0);

        setHasOptionsMenu(true);

        loading = content.findViewById(R.id.progressBar);

        recyclerView = content.findViewById(R.id.recyclerView);
        noBookings = content.findViewById(R.id.noBookings);

        getData();

        return content;
    }

    public void getData() {

        if (Utils.isOnline(Objects.requireNonNull(getActivity()).getApplicationContext())){
            //new getPrenotazioni().execute();
            getPrenotazioni();

        }else{
            View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
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
    public void onItemClick(BookingModel prenotazione) {
        //delete prenotazione
        deletePrenotazione(prenotazione);
    }

    public void deletePrenotazione(final BookingModel prenotazione){

        if (Utils.isOnline(getActivity())){
            new deletePrenotazione(prenotazione).execute();
        }else{
            View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePrenotazione(prenotazione);
                }
            });
            mySnackbar.show();
        }

    }

    private void getPrenotazioni(){
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
                    adapter = new PrenotazioniRecyclerAdapter(getActivity(), list, BookingsFragment.this);

                    if (list.size() > 0){
                        noBookings.setVisibility(View.GONE);

                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
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
            Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(jsonOblect);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class deletePrenotazione extends AsyncTask<Void, Void, String> {

        BookingModel prenotazione;

        private deletePrenotazione(BookingModel prenotazione){
            this.prenotazione = prenotazione;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlDeletePrenotazione + "?idprenotazione=" + prenotazione.getId();
            String result = sh.makeServiceCall(url);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.trim().equals("ok1")){
                prenotazione.setStato(2);
                adapter.notifyDataSetChanged();

                View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar mySnackbar = Snackbar.make(rootView,
                        "Prenotazione cancellata", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }else if (result.trim().equals("ok2")){
                prenotazione.setCancellazione(1);
                adapter.notifyDataSetChanged();

                View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar mySnackbar = Snackbar.make(rootView,
                        "Cancellazione richiesta", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
