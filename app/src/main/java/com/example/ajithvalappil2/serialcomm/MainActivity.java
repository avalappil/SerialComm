package com.example.ajithvalappil2.serialcomm;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.app.AlertDialog;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.*;

import android.os.Handler;
import android.os.Message;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;


public class MainActivity extends ActionBarActivity{

    Button connectBlu;
    TextView voiceToText;
    String message = "";
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    UsbManager manager = null;
    UsbSerialDriver driver = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.containsKey("connected")){
                String msgData  = bundle.getString("connected");
                System.out.println("Complete.....>> " + msgData);
                Button connectBlu=(Button)findViewById(R.id.button);
                if (msgData!=null && msgData.equalsIgnoreCase("Connected")){
                    connectBlu.setText("Disconnect");
                }else if (msgData!=null && msgData.equalsIgnoreCase("Disconnected")){
                    connectBlu.setText("Connect");
                }
                System.out.println("Complete.....");
            }
            if (bundle.containsKey("message")){
                String msgData  = bundle.getString("message");
                System.out.println("Complete.....>> " + msgData);
                TextView voiceToText = (TextView)findViewById(R.id.textView);
                voiceToText.append("Controller:" + msgData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        connectBlu=(Button)findViewById(R.id.button);
        voiceToText=(TextView)findViewById(R.id.textView);
        // Get UsbManager from Android.
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // Find the first available driver.
        driver = UsbSerialProber.acquire(manager);

        try {
            System.out.println("Starting.....");
        }catch (Exception ee){
            ee.printStackTrace();
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        System.out.println(item.getTitle());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openBluetooth(View view) {
        System.out.println("clicked the connect button");

    }

    public void sendMessage(String message){

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            System.out.println("onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            System.out.println("onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            //System.out.println("error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            //System.out.println("onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches.size()>=0) {
                message = matches.get(0);
                voiceToText.setText("You: " + message + "\n");
                sendMessage("*" + message + "#");
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {

        }

    }

}
