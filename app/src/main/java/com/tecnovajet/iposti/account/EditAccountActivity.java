package com.tecnovajet.iposti.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.tecnovajet.iposti.HttpHandler;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.booking.BookingModel;
import com.tecnovajet.iposti.booking.BookingsFragment;

public class EditAccountActivity extends AppCompatActivity {

    private String urlEditProfile = "OMITTED/updateprofile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String nome = getIntent().getStringExtra("nome");
        String cognome = getIntent().getStringExtra("cognome");
        String cellulare = getIntent().getStringExtra("cellulare");

        final TextInputLayout name = findViewById(R.id.name);
        final TextInputLayout surname = findViewById(R.id.surname);
        final TextInputLayout phone = findViewById(R.id.phone);

        name.getEditText().setText(nome);
        surname.getEditText().setText(cognome);
        phone.getEditText().setText(cellulare);

        Button edit = findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(name.getEditText().getText().toString().trim(), surname.getEditText().getText().toString().trim(), phone.getEditText().getText().toString().trim());
            }
        });
    }

    public void updateProfile(final String name, final String surname, final String phone){

        if (Utils.isOnline(EditAccountActivity.this)){
            new updateProfile(name, surname, phone).execute();
        }else{
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar mySnackbar = Snackbar.make(rootView,
                    "Nessuna connessione ad internet", Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction("Riprova", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateProfile(name, surname, phone);
                }
            });
            mySnackbar.show();
        }

    }

    private class updateProfile extends AsyncTask<Void, Void, String> {

        private String name, surname, phone;

        private updateProfile(String name, String surname, String phone){
            this.name = name;
            this.surname = surname;
            this.phone = phone;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = urlEditProfile + "?email=" + MainActivity.email + "&nome=" + name + "&cognome=" + surname + "&cellulare=" + phone;
            String result = sh.makeServiceCall(url);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.trim().equals("ok")){
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar mySnackbar = Snackbar.make(rootView,
                        "Profilo aggiornato", Snackbar.LENGTH_SHORT);
                mySnackbar.show();

                EditAccountActivity.this.finish();
            }else{
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar mySnackbar = Snackbar.make(rootView,
                        "Errore", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
