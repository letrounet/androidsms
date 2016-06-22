import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Button voitureArrive, voitureAccepte;
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
