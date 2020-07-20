package com.tecnovajet.iposti.favorites;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.facilities.ListRecyclerAdapter;
import com.tecnovajet.iposti.facilities.OnItemClickListener;
import com.tecnovajet.iposti.facilities.Struttura;
import com.tecnovajet.iposti.facilities.StrutturaActivity;
import com.tecnovajet.iposti.lastminute.LastMinuteFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String urlPreferitiPost = "OMITTED/getpreferiti.php";

    private androidx.appcompat.app.ActionBar toolbar;
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private RelativeLayout noFavorites;

    private OnFragmentInteractionListener mListener;

    private FusedLocationProviderClient mFusedLocationClient;

    private ArrayList<Struttura> list;

    private Snackbar mySnackbar = null;

    public FavoritesFragment() {
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
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        final View content = inflater.inflate(R.layout.fragment_favorite, container, false);

        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setTitle("I miei preferiti");
        toolbar.setSubtitle(null);
        toolbar.setElevation(0);

        setHasOptionsMenu(true);

        loading = content.findViewById(R.id.progressBar);

        recyclerView = content.findViewById(R.id.recyclerView);
        noFavorites = content.findViewById(R.id.noFavorites);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        getData();

        return content;
    }

    @Override
    public void onItemClick(final Struttura struttura) {
        if (Utils.isOnline(getActivity().getApplicationContext())){

            if (mySnackbar != null && mySnackbar.isShown())
                mySnackbar.dismiss();

            Intent i = new Intent(getActivity(), StrutturaActivity.class)
                    .putExtra("id", struttura.getId())
                    .putExtra("name", struttura.getNome());
            startActivity(i);

        }else{
            View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(struttura);
                }
            });
            mySnackbar.show();
        }

    }

    public void getData() {

        if (Utils.isOnline(getActivity().getApplicationContext())){

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (location != null) {
                                //new getPreferiti(location.getLatitude(), location.getLongitude()).execute();
                                getPreferiti(location.getLatitude(), location.getLongitude());
                            }else{
                                Log.d("PLACES", "location null");
                            }
                        }
                    });


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

    private void getPreferiti(final double latitude, final double longitude){
        try {
            list = new ArrayList<>();
            loading.setVisibility(View.VISIBLE);

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("email", MainActivity.email);
            jsonBody.put("latitude", String.valueOf(latitude));
            jsonBody.put("longitude", String.valueOf(longitude));

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, urlPreferitiPost, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObj) {

                    try {
                        JSONArray strutture = jsonObj.getJSONArray("items");
                        for (int i = 0; i < strutture.length(); i++) {

                            JSONObject c = strutture.getJSONObject(i);
                            int id = c.getInt("IDStruttura");
                            String nome = c.getString("Nome");
                            String tipologia = c.getString("Tipo");
                            double distanza = c.getDouble("distance");
                            int preferito = 0;

                            list.add(new Struttura(id, nome, tipologia, distanza, preferito));
                        }
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }

                    loading.setVisibility(View.GONE);

                    if (list.size() > 0){
                        noFavorites.setVisibility(View.GONE);

                        ListRecyclerAdapter adapter = new ListRecyclerAdapter(getActivity(), list, FavoritesFragment.this);

                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        llm.setOrientation(RecyclerView.VERTICAL);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setAdapter(adapter);
                    }else{
                        noFavorites.setVisibility(View.VISIBLE);
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
