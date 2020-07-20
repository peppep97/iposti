package com.tecnovajet.iposti.lastminute;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class LastMinuteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String urlLastminute= "OMITTED/getlastminute.php";

    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 101;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private androidx.appcompat.app.ActionBar toolbar;
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private TextView useCurrentPosition;
    private LinearLayout currentPosition;
    private RelativeLayout noLastMinutes;

    private OnFragmentInteractionListener mListener;

    //private boolean isVisible = false;
    //private int animDuration = 250;
    private ArrayList<LastMinute> list;

    private FusedLocationProviderClient mFusedLocationClient;

    private Snackbar mySnackbar = null;

    public abstract class OnGeocoderFinishedListener {
        public abstract void onFinished(List<Address> results);
    }

    public LastMinuteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FacilitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LastMinuteFragment newInstance(String param1, String param2) {
        LastMinuteFragment fragment = new LastMinuteFragment();
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

        final View content = inflater.inflate(R.layout.fragment_last_minute, container, false);

        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setSubtitle("Adesso");
        toolbar.setElevation(0);

        setHasOptionsMenu(true);

        loading = content.findViewById(R.id.progressBar);
        recyclerView = content.findViewById(R.id.recyclerView);
        currentPosition = content.findViewById(R.id.currentPosition);
        useCurrentPosition = content.findViewById(R.id.useCurrentPosition);
        noLastMinutes = content.findViewById(R.id.noLastMinutes);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //check permission
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        } else {
            // Permission has already been granted
            //TAKE THE CURRENT LOCATION
            try {
                searchPlacesByLocation();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        useCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPlacesByLocation();
            }
        });

        return content;
    }

    public void searchPlacesByLocation() throws SecurityException{

        if (Utils.isOnline(getActivity().getApplicationContext())){
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (location != null) {
                                //TAKE CITY NAME
                                getCityName(getActivity().getApplicationContext(), location, new OnGeocoderFinishedListener() {
                                    @Override
                                    public void onFinished(List<Address> results) {

                                        currentPosition.setVisibility(View.GONE);

                                        if (results.size() > 0){
                                            String currentLocation = results.get(0).getLocality();
                                            Log.d("PLACES", currentLocation);
                                            toolbar.setTitle(currentLocation);
                                        }

                                        new getJSON(location.getLatitude(), location.getLongitude()).execute();

                                    }
                                });
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
                    searchPlacesByLocation();
                }
            });
            mySnackbar.show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("PLACES", "Place: " + place.getName() + ", " + place.getId());

                if (currentPosition.getVisibility() != View.VISIBLE)
                    currentPosition.setVisibility(View.VISIBLE);
                toolbar.setTitle(place.getName());
                new getJSON(place.getLatLng().latitude, place.getLatLng().longitude).execute();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("PLACES", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(final Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        //TAKE CITY NAME
                                        getCityName(getActivity().getApplicationContext(), location, new OnGeocoderFinishedListener() {
                                            @Override
                                            public void onFinished(List<Address> results) {
                                                String currentLocation = results.get(0).getLocality();
                                                Log.d("PLACES", currentLocation);
                                                toolbar.setTitle(currentLocation);
                                                //searchPlace.setText(currentLocation);

                                                new getJSON(location.getLatitude(), location.getLongitude()).execute();
                                            }
                                        });
                                    }
                                }
                            });
                }
                 /*else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }*/
            }
        }
    }

    private static void getCityName(final Context context, final Location location, final OnGeocoderFinishedListener listener) {
        new AsyncTask<Void, Integer, List<Address>>() {
            @Override
            protected List<Address> doInBackground(Void... arg0) {
                Geocoder coder = new Geocoder(context, Locale.ITALIAN);
                List<Address> results = null;
                try {
                    results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return results;
            }

            @Override
            protected void onPostExecute(List<Address> results) {
                if (results != null && listener != null) {
                    listener.onFinished(results);
                }
            }
        }.execute();
    }

    private class getJSON extends AsyncTask<Void, Void, Void> {

        double latitude;
        double longitude;

        private getJSON(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //double latitude = location.getLatitude();
            //double longitude = location.getLongitude();

            String url = urlLastminute + "?latitude=" + latitude + "&longitude=" + longitude + "&email=" + MainActivity.email;
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
                        double distanza = c.getDouble("distance");
                        int preferito = c.getInt("preferito");

                        list.add(new LastMinute(idStruttura, nomeStruttura, tipologia, idLastMinute, ora, giorno, idServizio, newPrezzo, descrizioneLastminute, nomeServizio, descrizioneServizio, prezzo, categoria, distanza, preferito));
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("PLACES", "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            loading.setVisibility(View.GONE);

            if (list.size() > 0){
                noLastMinutes.setVisibility(View.GONE);

                LastMinuteRecyclerAdapter adapter = new LastMinuteRecyclerAdapter(getActivity(), list, null);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(llm);
                recyclerView.setAdapter(adapter);
            }else{
                noLastMinutes.setVisibility(View.VISIBLE);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.last_minute, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search :
                /*if (!isVisible) {
                    slideDown(searchMenuView);
                    TransitionDrawable transition = (TransitionDrawable) searchMenu.getBackground();
                    transition.startTransition(animDuration);
                }else {
                    slideUp(searchMenuView);
                    TransitionDrawable transition = (TransitionDrawable) searchMenu.getBackground();
                    transition.reverseTransition(animDuration);
                }*/

                /*try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setCountry("IT").setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES )
                            .build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(getActivity());
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e1) {
                    e1.printStackTrace();
                }*/

                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields).setCountry("IT").setTypeFilter(TypeFilter.CITIES)
                        .build(getActivity());
                startActivityForResult(intent, 1);
            default:
                return super.onOptionsItemSelected(item);
        }
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
