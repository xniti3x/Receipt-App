package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final Gson gson = new Gson();
    private List<Transaction> ta = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private CustomArrayAdapter adapter= null;
    private Uri imgUri =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        verifyStoragePermissions(this);

        if (Intent.ACTION_SEND.equals(action) && type != null) {//when sharing a file...
            handleSendImage(intent);
            handleListItem();//converts and shows json to ListView
            exeTransactionsRequest();//http-get (fetches opened transaction json)
        } else {
            Intent intentWebView = new Intent(this,MyWebView.class);
            startActivity(intentWebView);
        }

    }
    private void handleListItem() {
        adapter = new CustomArrayAdapter (MainActivity.this, android.R.layout.simple_list_item_1, ta);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Transaction clicked=(Transaction) adapterView.getItemAtPosition(i);
                if (imgUri!=null) {
                    File file = new File(getRealPathFromURI(imgUri));
                    uploadFile(getProperty("server_api_upload")+clicked.getTransactionId(), file);
                    //adapter.clear();
                    Toast.makeText(getBaseContext(), "Upload war Erfolgreich.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Es wurde kein Foto zur App geteilt.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
    public void uploadFile(String serverURL, File file) {
        try {

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                            "userfile", file.getName(),
                            RequestBody.create(MediaType.parse("*/*"),
                            file
                            )
                    )
                    .build();
            Request request = new Request.Builder().url(serverURL).post(requestBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    // Handle the error
                    Log.d("Client:", "Fehler beim senden der Anfrage.");
                    e.printStackTrace();
                }
                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Upload successful
                        imgUri=null;
                        Log.d("Server","Upload war Erfolgreich.");

                    }else{
                        // Handle the error
                        Log.d("Server:", "Fehler beim hochladen des Fotos");
                    }
                }
            });

        } catch (Exception ex) {
            // Handle the error
        }
    }
    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            this.imgUri =imageUri;
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void exeTransactionsRequest(){
        Request request = new Request.Builder().url(getProperty("server_api_getTransactions")).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                // Handle the error
                e.printStackTrace();
            }
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                    // Handle the error
                String res=response.body().string();
                System.out.println(res);
                    Transaction[] tr =gson.fromJson(res, Transaction[].class);
                    for (Transaction t:tr) {
                        ta.add(t);
                    }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                }

                // Upload successful

        });

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
