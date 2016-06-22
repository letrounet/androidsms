

import android.animation.AnimatorSet;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Button voitureArrive, voitureAccepte;
    private final String voitureAcc = " Votre demande a été acceptée. Votre chauffeur est en chemin. ";
    private String voitureArr = " Votre chauffeur est là. Informations:  ";
    String nomChauffeur, immatriculation, modele,numero;
    EditText numeroEdittext, nomChauffeurEdittext, immatriculationEditText, modeleEditText;
    Button envoyerSmsButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voitureArrive = (Button) findViewById(R.id.voiture_arrive);
        voitureAccepte = (Button) findViewById(R.id.course_accepte);

        voitureArrive.setOnClickListener(this);
        voitureAccepte.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.voiture_arrive){

            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.popup_chauffeur,null);
            numeroEdittext = (EditText) dialogView.findViewById(R.id.numero);
            nomChauffeurEdittext = (EditText) dialogView.findViewById(R.id.chauffeur);
            immatriculationEditText = (EditText) dialogView.findViewById(R.id.immatriculation);
            modeleEditText = (EditText) dialogView.findViewById(R.id.modele);
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
            dialogbuilder.setView(dialogView);
            dialogbuilder.setPositiveButton(R.string.envoyesms, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    numero = numeroEdittext.getText().toString();
                    nomChauffeur = nomChauffeurEdittext.getText().toString();
                    immatriculation = immatriculationEditText.getText().toString();
                    modele = modeleEditText.getText().toString();

                    if (!nomChauffeur.trim().isEmpty() && !immatriculation.trim().isEmpty() && !modele.trim().isEmpty() && !numero.trim().isEmpty()) {
                        voitureArr = voitureArr + " Nom: " + nomChauffeur + ", immat: " + immatriculation + " modele: " + modele;
                        sendSms(voitureArr, numeroEdittext.getText().toString());
                    }
                    dialog.dismiss();
                }
            });
            dialogbuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogbuilder.create().show();



        }
        else if(v.getId() == R.id.course_accepte){

            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.popup_accepte,null);
            // on affiche un boite de dialogue
            numeroEdittext = (EditText) dialogView.findViewById(R.id.numero);
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
            dialogbuilder.setView(dialogView);
            dialogbuilder.setPositiveButton(R.string.envoyesms, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sendSms(voitureAcc,numeroEdittext.getText().toString());
                    dialog.dismiss();

                }
            });
            dialogbuilder.setCancelable(true);
            dialogbuilder.create().show();

        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public void sendSms(String message,String numero){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sendIntent = PendingIntent.getBroadcast(getApplicationContext(), 1234, new Intent("SENT"), PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 1234, new Intent("DELIVER"), PendingIntent.FLAG_CANCEL_CURRENT);
            // on crée le receiver

            getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch(getResultCode()){

                        case RESULT_OK:

                            // ca veut dire que le message a été envoyé.

                                Toast.makeText(getApplicationContext(),getString(R.string.smsenvoye), Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getApplicationContext(),getString(R.string.sms_generic_failure), Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:

                            Toast.makeText(getApplicationContext(),getString(R.string.sms_error_radio_off), Toast.LENGTH_SHORT).show();

                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:

                            Toast.makeText(getApplicationContext(),getString(R.string.sms_error_pdu_null), Toast.LENGTH_SHORT).show();

                            break;
                        default:
                            Toast.makeText(getApplicationContext(),getString(R.string.sms_envoye_non), Toast.LENGTH_SHORT).show();

                            break;
                    }
                }
            }, new IntentFilter("SENT"));

            getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()){
                        case RESULT_OK:
                            Toast.makeText(getApplicationContext(),getString(R.string.sms_recu), Toast.LENGTH_SHORT).show();
                            break;
                        case RESULT_CANCELED:
                            Toast.makeText(getApplicationContext(),getString(R.string.sms_non_recu), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("DELIVER"));
            smsManager.sendTextMessage(numero, null, message, sendIntent, deliveredIntent);
            Toast.makeText(getApplicationContext(), "SMS envoyé.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS échoué, reessayez SVP", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
