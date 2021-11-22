package com.example.p33barboza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_USER = "KEY_USER";
    Date date;

    String filename = "usernames.txt";
    Button button, send;
    EditText et;
    TextView tv;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
        checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_first_option:
                Intent userProfileIntent = new Intent(this, SettingsActivity.class);
                startActivity(userProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUI() {
        button = findViewById(R.id.button);
        send = findViewById(R.id.send_button);
        et = findViewById(R.id.et);
        tv = findViewById(R.id.tv);
        File file = new File(getFilesDir(), filename);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File newFile = new File(getFilesDir(), "usernames.txt");
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        newFile);

                Intent intentShareFile = new Intent(Intent.ACTION_SEND)
                        .putExtra( Intent.EXTRA_STREAM, contentUri)
                        .setType( "text/*");
                Intent chooser = Intent.createChooser(intentShareFile, "Share File");

                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(chooser);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(date);
                String tmp = et.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_USER, tmp);
                editor.apply();
                try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE | MODE_APPEND)) {
                    String fileContents = "User: " + sharedPreferences.getString(KEY_USER, getResources().getString(R.string.tv_default)) + ", Date: " + strDate + "\n";
                    fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {

                }
            }
        });
    }

    private void checkUser() {
        String preferencesFile = "auth_preferences";
        //sharedPreferences = getSharedPreferences( preferencesFile ,MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString(KEY_USER, getResources().getString(R.string.tv_default));
        tv.setText(userName);
    }
}