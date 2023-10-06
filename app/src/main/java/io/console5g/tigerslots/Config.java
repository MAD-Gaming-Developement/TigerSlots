package io.console5g.tigerslots;


import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


public class Config extends Application {

    private static Application appCFG = new Application();

    private FirebaseRemoteConfig remoteConfig;
    private RequestQueue requestApi;

    public static String facebookAppId = "";
    public static String facebookClientToken = "";

    public static final String appCode = "5G102TS";
    public static String apiURL = "";
    public static String gameURL = "";
    public static String policyURL = "";
    public static String appsFlyerAppID = "";
    public static String appsFlyerAPI = "";
    public static String jsInterface = "";
    public static Boolean permitSendData;

    //coding proper
    @Override
    public void onCreate() {

        super.onCreate();

        appCFG = this;
        FirebaseApp.initializeApp(this);
        initRemoteConfig();
    }

    public static synchronized Config getInstance(){ return (Config) appCFG; }

    private void initRemoteConfig(){
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(2800)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
    }

    public FirebaseRemoteConfig getRemoteConfig(){ return remoteConfig; }

    //Volley Rest API
    public RequestQueue getRequest(){
        if(requestApi == null)
            requestApi = Volley.newRequestQueue(getApplicationContext());

        return requestApi;

    }
}