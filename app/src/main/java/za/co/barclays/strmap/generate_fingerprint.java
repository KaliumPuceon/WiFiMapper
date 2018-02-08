package za.co.barclays.strmap;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class generate_fingerprint extends AppCompatActivity {

    boolean isRecorded;
    String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Mostly autogen boilerplate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_fingerprint);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isRecorded = false;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scan Cleared", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                    //Reset recording status on pressing the mail button
                    //Resets ProgressBar, contents of textdisplay, etc.
                    isRecorded = false;
                    line = "";
                    ProgressBar prog = findViewById(R.id.scanCount);
                    prog.setProgress(0);
                    Button save = findViewById(R.id.SaveButton);
                    save.setEnabled(false);
                    EditText display = findViewById(R.id.textDisplay);
                    display.setText("");
                    Button scan = findViewById(R.id.button);
                    scan.setEnabled(true);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Boilerplate
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generate_fingerprint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //More reset code, boilerplate
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        isRecorded = false;
        line = "";
        ProgressBar prog = findViewById(R.id.scanCount);
        prog.setProgress(0);
        Button save = findViewById(R.id.SaveButton);
        save.setEnabled(false);
        EditText display = findViewById(R.id.textDisplay);
        display.setText("");

        return super.onOptionsItemSelected(item);
    }



    public void incrementProgressBar(View v) { //OK technically this does a lot of things that aren't "Increment the bar"

        Button save = findViewById(R.id.SaveButton);
        ProgressBar prog = findViewById(R.id.scanCount);
        Button scan = findViewById(R.id.button);
        int progress = prog.getProgress();
        prog.setProgress(progress+10);
        if (prog.getProgress()==100) { //Check if bar full and allow saving if true

            isRecorded = true;
            save.setEnabled(true);
            scan.setEnabled(false);

        }

        //IMPORTANT: START OF WIFI HANDLING MAGIC CODE
        //Also you'll need to set the wifi permissions

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); //Create a wifi manager
        List<ScanResult> results; //For storing results
        wifi.startScan(); //Run a scan

        results = wifi.getScanResults(); //Store results

        EditText display = findViewById(R.id.textDisplay);

        EditText pickRoomName = findViewById(R.id.pickRoom);
        EditText pickRoomID = findViewById(R.id.pickRoomID);
        EditText pickAreaID = findViewById(R.id.pickAreaID);


        for (int k = 0; k < results.size(); k++) //Iterate over results and print out
        {
            line = line +pickRoomID.getText() +"," +pickRoomName.getText()+","+pickAreaID.getText()+","+(results.get(k).SSID + "," + results.get(k).BSSID + "," + results.get(k).level)+"\n";
        }

        // To summarise, if each entry in the list is gonna be stored in a variable, let's call it value
        //
        // Then to get the MAC, get value.BSSID and for the Strength, get value.level

    }

    public void oneScan(View v) { //Take a diagnostic scan and save it to the download file

        line = "";

        EditText display = findViewById(R.id.textDisplay);

        EditText pickRoomName = findViewById(R.id.pickRoom);
        EditText pickRoomID = findViewById(R.id.pickRoomID);
        EditText pickAreaID = findViewById(R.id.pickAreaID);

        //MORE WIFI CODE, IDENTICAL BECAUSE DRY

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> results;
        wifi.startScan();
        results = wifi.getScanResults();
        for (int k = 0; k < results.size(); k++) {
            line = line+pickRoomID.getText() +"|" +pickRoomName.getText()+"|"+pickAreaID.getText()+(results.get(k).SSID +"|" +results.get(k).BSSID+"|"+results.get(k).level)+"\n";
        }


        String localops = line;
        display.setText(localops);

        //Setting up file writing to the Downloads folder

        String filename = String.valueOf(pickRoomName.getText())+"reading"+String.valueOf(pickRoomID.getText())+String.valueOf(pickAreaID.getText())+".txt";
        filename = filename;
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename); //setup filestream to downloads folder

            outputStream = new FileOutputStream(file);
            outputStream.write(localops.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incrementAreaID(View v){ //Never Used
        EditText pickAreaID = findViewById(R.id.pickAreaID);
        int number = Integer.parseInt(String.valueOf(pickAreaID.getText()));
        pickAreaID.setText(number+1);
    }

    public void saveCurrentPrint(View v) throws IOException { //Uses the same filestream code to save a fingerprint file to the Downloads folder

        EditText display = findViewById(R.id.textDisplay);
        String localops = line;

        EditText pickRoomName = findViewById(R.id.pickRoom);
        EditText pickRoomID = findViewById(R.id.pickRoomID);
        EditText pickAreaID = findViewById(R.id.pickAreaID);

        display.setText(localops);

        String filename = String.valueOf(pickRoomName.getText())+"_"+String.valueOf(pickRoomID.getText())+"_"+String.valueOf(pickAreaID.getText())+".txt";
        filename = filename;
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            outputStream = new FileOutputStream(file);
            outputStream.write(localops.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
