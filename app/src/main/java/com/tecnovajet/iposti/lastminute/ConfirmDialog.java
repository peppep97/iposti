package com.tecnovajet.iposti.lastminute;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.badoualy.stepperindicator.StepperIndicator;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.Utils;
import com.tecnovajet.iposti.facilities.Servizio;

import java.util.Locale;

public class ConfirmDialog extends DialogFragment {

    //public static final String TAG = "example_dialog";

    private Toolbar toolbar;
    private StepperIndicator indicator;
    private TextView struttura, nome, tipo, prezzo, ora, giorno;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_confirm_booking, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        indicator = view.findViewById(R.id.indicator);
        struttura = view.findViewById(R.id.struttura);
        nome = view.findViewById(R.id.nome);
        tipo = view.findViewById(R.id.tipo);
        prezzo = view.findViewById(R.id.prezzo);
        ora = view.findViewById(R.id.ora);
        giorno = view.findViewById(R.id.giorno);

        if (getArguments() != null) {
            String nomeStruttura =  getArguments().getString("struttura");
            String nomeS =  getArguments().getString("nome");
            String tipoS =  getArguments().getString("tipo");
            double prezzoD = getArguments().getDouble("prezzo");
            double newPrezzoD = getArguments().getDouble("newprezzo");

            String oraS = getArguments().getString("ora");
            String giornoS = getArguments().getString("giorno");

            ora.setText(oraS);
            giorno.setText(giornoS);
            struttura.setText(nomeStruttura);
            nome.setText(nomeS);
            tipo.setText(tipoS);
            if (newPrezzoD != -1)
                prezzo.setText(String.format(Locale.ITALIAN, "%.2f€", newPrezzoD));
            else
                prezzo.setText(String.format(Locale.ITALIAN, "%.2f€", prezzoD));
        }

        indicator.setCurrentStep(1);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        toolbar.setTitle("La tua prenotazione");
//        toolbar.inflateMenu(R.menu.example_dialog);
//        toolbar.setOnMenuItemClickListener(item -> {
//            dismiss();
//            return true;
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    return true;
                }
                else
                    return false;
            }
        });
    }
}