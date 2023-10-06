package io.console5g.tigerslots.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;

import org.json.JSONException;
import org.json.JSONObject;

import io.console5g.tigerslots.Config;
import io.console5g.tigerslots.R;

public class SplashActivity extends AppCompatActivity {

    private FirebaseRemoteConfig remoteConfig;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_splash);

        sharedPref = getSharedPreferences(Config.appCode, MODE_PRIVATE);

        fbRemoteConfig();


    }

    public void fbRemoteConfig(){
        remoteConfig = Config.getInstance().getRemoteConfig();
        remoteConfig.setDefaultsAsync(R.xml.default_config);
        remoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d("Firebase Remote Config: ", "Remote Config Successful.");
                Toast.makeText(this, "Remote Configuration Successfully Loaded.", Toast.LENGTH_LONG).show();
            }else{
                Log.d("Firebase Remote Config: ", "Remote Config Unsuccessful.");
            }

            setInitialConfig();
        });

        remoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
                Log.d("Firebase Remote Config: ", "Remote Config Update: "+configUpdate.getUpdatedKeys());
                Toast.makeText(SplashActivity.this, "Remote Configuration Successfully Updated.", Toast.LENGTH_LONG).show();

                setInitialConfig();
            }

            @Override
            public void onError(FirebaseRemoteConfigException error) {
                Log.d("Firebase Remote Config: ", "Remote Config Error: "+error.getCode(),error);
                setInitialConfig();
            }
        });
    }

    private void setInitialConfig(){
        Config.apiURL = remoteConfig.getString("apiURL");
        Config.appsFlyerAPI = remoteConfig.getString("appsFlyerAPI");
        Config.appsFlyerAppID = remoteConfig.getString("appsFlyerAppID");
        Config.jsInterface = remoteConfig.getString("jsInterface");

        Log.d("appsflyerappid====","appsflyerappid==== "+Config.appsFlyerAppID);
        Log.d("appsflyerapi====","appsflyerapi==== "+Config.appsFlyerAPI);
        Log.d("apiurl====","apiurl=== "+Config.apiURL);
        Log.d("jsInterface====","jsInterface=== "+Config.jsInterface);
        RequestQueue connectAPI = Config.getInstance().getRequest();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("appid", Config.appCode);
        }catch (JSONException e){
            e.printStackTrace();
        }

        String endPoint = Config.apiURL + "?appid=" + Config.appCode;

        JsonObjectRequest jsonObjectRequest =new  JsonObjectRequest(Request.Method.GET,
                endPoint, requestBody,
                response ->{
                    try{
                        Config.gameURL = response.getString("gameURL");
                        Config.policyURL = response.getString("policyURL");
                        Config.permitSendData = sharedPref.getBoolean("permitSendData", false);

                        if(!sharedPref.getBoolean("doneUserConsent",false))
                        {
                            Intent userConsent = new Intent(this, PolicyActivity.class);
                            userConsent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(userConsent);
                            finish();
                        }else{
                            Intent webApp = new Intent(this, MainActivity.class);
                            webApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(webApp);
                            finish();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error ->{ Log.d("API Response: ", "Error in response from API: "+error.getMessage());

        });
        connectAPI.add(jsonObjectRequest);
    }
}