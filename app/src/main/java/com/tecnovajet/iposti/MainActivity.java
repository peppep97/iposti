package com.tecnovajet.iposti;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tecnovajet.iposti.account.SignUpInActivity;
import com.tecnovajet.iposti.facilities.FacilitiesFragment;
import com.tecnovajet.iposti.account.AccountFragment;
import com.tecnovajet.iposti.booking.BookingsFragment;
import com.tecnovajet.iposti.favorites.FavoritesFragment;
import com.tecnovajet.iposti.lastminute.LastMinuteFragment;

public class MainActivity extends AppCompatActivity implements LastMinuteFragment.OnFragmentInteractionListener, FacilitiesFragment.OnFragmentInteractionListener,
        BookingsFragment.OnFragmentInteractionListener, FavoritesFragment.OnFragmentInteractionListener, AccountFragment.OnFragmentInteractionListener{

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public static String email;
    public static String token;

    private static String urlToken = "OMITTED/addtoken.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mAuth = FirebaseAuth.getInstance();



            // Initialize Places.
            Places.initialize(getApplicationContext(), getString(R.string.places_api));

            // Create a new Places client instance.
            PlacesClient placesClient = Places.createClient(this);

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            frameLayout = findViewById(R.id.frameLayout);

            fragment = new LastMinuteFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    int itemId = menuItem.getItemId();

                    if (itemId == R.id.last_minute)
                        fragment = new LastMinuteFragment();
                    else if (itemId == R.id.facilities)
                        fragment = new FacilitiesFragment();
                    else if(itemId == R.id.bookings)
                        fragment = new BookingsFragment();
                    else if (itemId == R.id.favorites)
                        fragment = new FavoritesFragment();
                    else
                        fragment = new AccountFragment();

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.frameLayout, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();

                    return true;
                }
            });

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("firebase", "getInstanceId failed", task.getException());
                                return;
                            }
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser == null){
                                Intent i = new Intent(MainActivity.this, SignUpInActivity.class);
                                startActivity(i);
                            }else {
                                //mAuth.signOut();
                                Log.d("email", currentUser.getEmail());
                                email = currentUser.getEmail();

                                // Get new Instance ID token
                                token = task.getResult().getToken();

                                Log.d("firebase", token);

                                new addToken(email, token).execute();
                            }
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class addToken extends AsyncTask<Void, Void, Void> {

        String email, token;

        private addToken(String email, String token){
            this.email = email;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlToken + "?email=" + email + "&token=" + token;

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
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
