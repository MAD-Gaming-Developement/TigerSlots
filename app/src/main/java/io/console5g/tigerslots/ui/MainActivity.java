package io.console5g.tigerslots.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appsflyer.AppsFlyerLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.console5g.tigerslots.Config;
import io.console5g.tigerslots.R;

public class MainActivity extends AppCompatActivity {

    private WebView webBrowser;
    private SharedPreferences sharedPref;
    private boolean bckExit = false;

        @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            getWindow().setFlags(1024, 1024);
            sharedPref = getSharedPreferences(Config.appCode, MODE_PRIVATE);

            webBrowser = findViewById(R.id.wvMain);
            webBrowser.setWebViewClient(new WebViewClient());

            webBrowser.getSettings().setLoadsImagesAutomatically(true);
            webBrowser.getSettings().setJavaScriptEnabled(true);
            webBrowser.getSettings().setDomStorageEnabled(true);
            webBrowser.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webBrowser.getSettings().setAllowContentAccess(true);
            webBrowser.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            webBrowser.getSettings().setSupportMultipleWindows(true);

            if(Config.permitSendData){
                AppsFlyerLib.getInstance().setCollectIMEI(false);
                AppsFlyerLib.getInstance().setCollectAndroidID(false);
                AppsFlyerLib.getInstance().setAppId(Config.appsFlyerAppID);
                AppsFlyerLib.getInstance().anonymizeUser(true);

                AppsFlyerLib.getInstance().init(Config.appsFlyerAppID, null, this);
                AppsFlyerLib.getInstance().start(this);

                webBrowser.addJavascriptInterface(new JScript(this), Config.jsInterface);
                webBrowser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            }
            webBrowser.loadUrl(Config.gameURL);

    }

    @Override
    public void onBackPressed() {
        if (bckExit) {
            super.finishAffinity();
            return;
        }
        this.bckExit = true;
        Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bckExit = false;
            }
        }, 2000);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public static class JScript {

       Context context;

        public JScript(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void postMessage(String name, String data) {
            Log.d("JsInterface:", "Event name:" + name + "Data:" + data);

            if("openWindow".equals(name)){
                try{
                    JSONObject extLink = new JSONObject();
                    Intent newWindow = new Intent(Intent.ACTION_VIEW);
                    newWindow.setData(Uri.parse(extLink.getString("url")));
                    newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newWindow);
                } catch (JSONException e) {
                    Log.d("OpenWindow", "OpenWindow Error: "+e.getMessage());
                }
            }

            if (Config.permitSendData) {

                RequestQueue sendAFRequest = Config.getInstance().getRequest();

                JSONObject requestBody = new JSONObject();

                try {
                    requestBody.put("appsflyer_id", AppsFlyerLib.getInstance().getAppsFlyerUID(context));
                    requestBody.put("eventName", name);
                    requestBody.put("eventValue", data);
                    requestBody.put("authentication", Config.appsFlyerAppID);
                    requestBody.put("endpoint", context.getPackageName());
                } catch (JSONException e) {
                    Log.d("AFREQUEST", "Error Sending AF Parameters: "+e.getMessage());
                    e.printStackTrace();
                }
                String endPoint = Config.appsFlyerAPI +
                        "?appsflyer_id=" + AppsFlyerLib.getInstance().getAppsFlyerUID(context) +
                        "&eventName=" + name +
                        "&eventValue=" + data +
                        "&authentication=" + Config.appsFlyerAppID +
                        "&endpoint=" + context.getPackageName();
                Log.d("AF API endpoint", "name-==: "+name);
                Log.d("AF API endpoint", "data-==: "+data);
                Log.d("AF API endpoint", "auth-==: "+Config.appsFlyerAppID);
                Log.d("AF API endpoint", "endpoint-==: "+context.getPackageName());

                JsonObjectRequest afRequest = new JsonObjectRequest(Request.Method.GET,
                        endPoint, requestBody, response -> {
                    Log.d("AF:RESPONSE", "AppsFlyer Event Recieved: "+response.toString());
                }, error -> {
                    Log.d("AF:RESPONSE", "AppsFlyer Event Error: "+error.getMessage());
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("accept", "application/json");
                        headers.put("content-type", "application/json");

                        return headers;
                    }
                };
                sendAFRequest.add(afRequest);
                Log.d("afrequest ==== ", "afrequest====== "+afRequest);
            }
        }

    }


}