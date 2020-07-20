package com.tecnovajet.iposti.facilities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tecnovajet.iposti.R;

public class StrutturaInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Struttura struttura;
    private TextView descrizioneText;
    private TextView addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_struttura_info);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        struttura = (Struttura) getIntent().getSerializableExtra("struttura");

        descrizioneText = findViewById(R.id.descrizioneText);
        addressText = findViewById(R.id.addressText);

        descrizioneText.setText(struttura.getDescrizione());
        addressText.setText(String.format("%s, %s, %s %s", struttura.getVia(), struttura.getNumero(), struttura.getCap(), struttura.getCitta()));

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap mMap) {

        LatLng sydney = new LatLng(struttura.getLat(), struttura.getLon());
        mMap.addMarker(new MarkerOptions().position(sydney).title(struttura.getNome()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(16.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);

        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(StrutturaInfoActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
