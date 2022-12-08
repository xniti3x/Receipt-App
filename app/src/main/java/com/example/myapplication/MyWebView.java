package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);
        WebView myWebView = findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(getProperty("iframe_api"));
        myWebView.getSettings().setJavaScriptEnabled(true);

    }

    public String getProperty(String key) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = this.getAssets();

            InputStream inputStream = assetManager.open("app_variable.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        }catch (IOException e){
            e.fillInStackTrace();
        }
        return null;
    }
}