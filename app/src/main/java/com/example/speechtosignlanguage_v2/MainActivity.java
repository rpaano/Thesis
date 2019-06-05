package com.example.speechtosignlanguage_v2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.app.Dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;
    Button clk;
    VideoView videov;
    VideoView videoplay;
    TextInputEditText textinputtext;
    TextView textView;
    VideoView view;


    //storage
    public String path = Environment.getExternalStorageDirectory().getPath() + "/SpeechToSignLanguage/";
    //private List<String>  input_sepa = new ArrayList<String>();
    private int STORAGE_PERMISSION_CODE = 1;
    private int ctr = 0;
    private String str;
    private String[] linking = {"is", "are", "am", "being", "been", "become", "be", "was", "were"};
    public String[] input_sepa;
    private String mp4 = ".mp4";
    private int len = 0;
    public String[] theses;

    @SuppressLint("CutPasteId")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        myDialog = new Dialog(this);

        final int these = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (these == PackageManager.PERMISSION_GRANTED) {
            //Continue
        } else {
            requestStoragePermission();
        }

        clk = (Button) findViewById(R.id.button);
        videov = (VideoView) findViewById(R.id.videoView);
        textinputtext = (TextInputEditText) findViewById(R.id.parse);
        //textinputtext.setText(path+"april"+mp4);
        textView = (TextView) findViewById(R.id.textview);
        //textView.setText(path);
        view = (VideoView)findViewById(R.id.videoView);
        String paths = "android.resource://" + getPackageName() + "/" + R.raw.april;
        videov.setVideoURI(Uri.parse(paths));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File files = new File(path);

        if (!files.exists()){
            exitapp();
        }else {

            //videov.start();

        }

        // loop video
        view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @SuppressLint("SetTextI18n")
            public void onCompletion(MediaPlayer mp) {
                view.start();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }



    public void defaults(){
        //view = (VideoView)findViewById(R.id.videoView);

        view.setVideoPath(path+"april"+mp4);
        view.start();
    }

    public void onButtonClick(View v){
        if (v.getId() ==R.id.imageButton){
            promptSpeechInput();
        }
    }

    public void promptSpeechInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE ,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something");

        try{
            startActivityForResult(i,100);
        }catch(ActivityNotFoundException a){
            Toast.makeText(MainActivity.this,"Sorry! Your device does not support speech language!", Toast.LENGTH_LONG).show();
        }
    }

    public void  onActivityResult(int request_code, int result_code, Intent i){
        super.onActivityResult(request_code,result_code,i);

        switch (request_code){
            case 100: if (result_code == RESULT_OK && i != null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                textinputtext.setText(result.get(0));
            }
            break;
        }
    }

    public void exitapp(){
        Toast.makeText(MainActivity.this,"Please include additional files", Toast.LENGTH_LONG).show();
        //System.exit(0);
    }

    public void special_char(){
        Toast.makeText(MainActivity.this,"Special Character Ignored", Toast.LENGTH_LONG).show();
        //System.exit(0);
    }

    public void donot(){
        Toast.makeText(MainActivity.this,"", Toast.LENGTH_LONG).show();
        //System.exit(0);
    }

    //video
    @SuppressLint("SetTextI18n")
    public void videoplay(View v) {

        //exitapp();
        //get string
        String input = textinputtext.getText().toString().trim();

        if (!validatetext()){
            return;
        }

        input_sepa = input.split(" ");
        ctr = 0;
        len = 0;
        int in = 0;

        File file = new File(path+input_sepa[ctr]+mp4);

        if(file.exists()){
            in = 1;
            //textView.setText(path+input_sepa[ctr]+mp4 );
            videov.setVideoPath(path+input_sepa[ctr]+mp4);
            videov.start();

            ctr++;

            if (ctr>=input_sepa.length){
                ctr = 0;
                in = 3;

            }

        }else{
            in= 2;
            theses = input_sepa[ctr].split("");

            if (theses[len].equals("")){
                len++;
                in=4;
            }

            if (!theses[len].matches("[a-zA-Z.? ]*")){
                special_char();

            }else{



                repeat();

            }

            File filez = new  File(path+theses[len]+mp4);
            if (!filez.exists()){
                donot();
                return;
            }


            len++;

            if (len >= theses.length){
                len = 0;
                ctr ++;
                if (ctr>=input_sepa.length){
                    ctr = 0;


                }
            }else{
                len--;
            }

        }

        //textView.setText(len+" "+ctr+" "+in+" "+ theses[len]);

        videov.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @SuppressLint("SetTextI18n")
            public void onCompletion(MediaPlayer mp) {

                //videov.start();
                if (ctr != 0||len!=0){

                    if (len != 0){

                        if (theses[len].equals("")){
                            len++;
                        }
                        //textView.setText(path+theses[len]+mp4+" "+len);
                        videov.setVideoPath(path+theses[len]+mp4);
                        videov.start();

                        len++;

                        if (len >= theses.length) {
                            len = 0;
                            ctr++;
                            if (ctr >= input_sepa.length) {
                                ctr = 0;

                            }
                        }

                    }else{
                        File file = new File(path+input_sepa[ctr]+mp4);

                        if(file.exists()){

                            //textView.setText(path+input_sepa[ctr]+mp4 );
                            videov.setVideoPath(path+input_sepa[ctr]+mp4);
                            videov.start();

                            ctr++;

                            if (ctr>=input_sepa.length){
                                ctr = 0;


                            }

                        }else{

                            theses = input_sepa[ctr].split("");

                            if (theses[len].equals("")){
                                len++;
                            }

                            if (!theses[len].matches("[a-zA-Z.? ]*")){
                                special_char();

                            }else{

                                videov.setVideoPath(path+theses[len]+mp4);

                                videov.start();
                            }
                            len++;

                            if (len >= theses.length){
                                len = 0;
                                ctr ++;
                                if (ctr>=input_sepa.length){
                                    ctr = 0;
                                }
                            }
                        }
                    }
                }else{
                    repeat();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void repeat(){
        //exitapp();
        //get string
        String input = textinputtext.getText().toString().trim();

        if (!validatetext()){
            return;
        }

        input_sepa = input.split(" ");
        ctr = 0;
        len = 0;
        int in = 0;

        File file = new File(path+input_sepa[ctr]+mp4);

        if(file.exists()){
            in = 1;
            //textView.setText(path+input_sepa[ctr]+mp4 );
            videov.setVideoPath(path+input_sepa[ctr]+mp4);
            videov.start();

            ctr++;

            if (ctr>=input_sepa.length){
                ctr = 0;
                in = 3;

            }

        }else{
            in= 2;
            theses = input_sepa[ctr].split("");

            if (theses[len].equals("")){
                len++;
            }

            if (!theses[len].matches("[a-zA-Z.? ]*")){
                special_char();

            }else{

                File filez = new  File(path+theses[len]+mp4);
                if (!filez.exists()){
                    donot();
                    return;
                }
                videov.setVideoPath(path+theses[len]+mp4);

                videov.start();

            }


            len++;

            if (len >= theses.length){
                len = 0;
                ctr ++;
                if (ctr>=input_sepa.length){
                    ctr = 0;


                }
            }

        }


        //textView.setText(len+" "+ctr+" "+in+" "+ theses[len]);

        videov.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @SuppressLint("SetTextI18n")
            public void onCompletion(MediaPlayer mp) {

                //videov.start();
                if (ctr != 0||len!=0){

                    if (len != 0){

                        if (theses[len].equals("")){
                            len++;
                        }

                        File filez = new  File(path+theses[len]+mp4);
                        if (!filez.exists()){
                            donot();
                            return;
                        }

                        //textView.setText(path+theses[len]+mp4);
                        videov.setVideoPath(path+theses[len]+mp4);
                        videov.start();

                        len++;

                        if (len >= theses.length) {
                            len = 0;
                            ctr++;
                            if (ctr >= input_sepa.length) {
                                ctr = 0;

                            }
                        }

                    }else{
                        File file = new File(path+input_sepa[ctr]+mp4);

                        if(file.exists()){

                            //textView.setText(path+input_sepa[ctr]+mp4 );
                            videov.setVideoPath(path+input_sepa[ctr]+mp4);
                            videov.start();

                            ctr++;

                            if (ctr>=input_sepa.length){
                                ctr = 0;


                            }

                        }else{

                            theses = input_sepa[ctr].split("");

                            if (theses[len].equals("")){
                                len++;
                            }

                            if (!theses[len].matches("[a-zA-Z.? ]*")){
                                special_char();

                            }else{

                                videov.setVideoPath(path+theses[len]+mp4);

                                videov.start();

                            }

                            len++;

                            if (len >= theses.length){
                                len = 0;
                                ctr ++;
                                if (ctr>=input_sepa.length){
                                    ctr = 0;


                                }
                            }
                        }

                    }
                }else{
                    repeat();
                }
            }
        });
    }

    //check if null
    private boolean validatetext(){
        String check = textinputtext.getText().toString().trim();

        if (check.isEmpty()) {
            textinputtext.setError("Field can't be empty!!");
            return false;
        }else{
            textinputtext.setError(null);
            return true;
        }
    }

    //request storage
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    public void opencamera(View v){
        Intent startNewActivity = new Intent(this,camera.class);
        startActivity(startNewActivity);
    }

    public void ShowPopup(MenuItem item) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.custompopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}

