package io.console5g.tigerslots.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import io.console5g.tigerslots.Config;
import io.console5g.tigerslots.R;

public class PolicyActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 201232;
    private WebView policyWV;
    private Button AcceptBtn, DeclineBtn;


    private SharedPreferences sharedPref;

    private AlertDialog.Builder alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        sharedPref = getSharedPreferences(Config.appCode, MODE_PRIVATE);

        policyWV = findViewById(R.id.webViewPolicy);
        AcceptBtn = findViewById(R.id.AcceptBtn);
        DeclineBtn = findViewById(R.id.DeclineBtn);

        policyWV.setWebViewClient(new WebViewClient());
        policyWV.loadUrl(Config.policyURL);

        showConsent();
    }

    public void showConsent(){

        DeclineBtn.setOnClickListener(decline -> finishAffinity());

        AcceptBtn.setOnClickListener(accept -> {
            alertDialog = new AlertDialog.Builder(PolicyActivity.this);
            alertDialog.setTitle("User Data Consent");
            alertDialog.setMessage("We may collect your information based on your activities during the usage of the app, to provide better user experience.");
            alertDialog.setPositiveButton("Agree", (dialogInterface, i) -> {
                Config.permitSendData = true;
                sharedPref.edit().putBoolean("permitSendData", true).apply();
                sharedPref.edit().putBoolean("doneUserConsent",true).apply();
                dialogInterface.dismiss();
            });
            alertDialog.setNegativeButton("Disagree", (dialogInterface, i) -> {
                Config.permitSendData = false;
                sharedPref.edit().putBoolean("permitSendData", false).apply();
                sharedPref.edit().putBoolean("doneUserConsent",false).apply();
                dialogInterface.dismiss();
            });

            alertDialog.setOnDismissListener(dialogInterface -> {
                if(Config.permitSendData){
                    if (!checkPermissions())
                        requestPermission();
                    else
                        openGame();
                }else
                    openGame();
            });
            alertDialog.show();
        });
    }

    private boolean checkPermissions(){
        //loc perm
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        int mediaPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mediaPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            mediaPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return locationPermission == PackageManager.PERMISSION_GRANTED
                && cameraPermission == PackageManager.PERMISSION_GRANTED
                && mediaPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES }, PERMISSION_REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                sharedPref.edit().putBoolean("grantLocation", true);
                sharedPref.edit().putBoolean("grantCamera", true);
                sharedPref.edit().putBoolean("grantMedia", true);
            } else {
                sharedPref.edit().putBoolean("grantLocation", false);
                sharedPref.edit().putBoolean("grantCamera", false);
                sharedPref.edit().putBoolean("grantMedia", false);
            }
            openGame();
        }

    }
    private void openGame(){
        Intent gameIntent = new Intent(this, SplashActivity.class);
        gameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gameIntent);
        finish();
    }
}