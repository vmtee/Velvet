package com.example.velvet.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.velvet.R;
import com.example.velvet.Helper.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity {
    UserSingleton userSingleton; Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");

        //Assign-email
        if(userSingleton.getInstance().getAuth() != null) {
            TextView textView = findViewById(R.id.email);
            String email = userSingleton.getInstance().getAuth().getCurrentUser().getEmail();
            textView.setText(email);
        }

        //sign-out button implementation
        signOut = findViewById(R.id.sign_out_btn);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSingleton.getInstance().getGoogleSignInClient().signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            userSingleton.getInstance().getAuth().signOut();
                            Toast.makeText(SettingsActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item =  menu.findItem(R.id.settings_icon);
        item.setVisible(false);
        return true;
    }

    //@Override

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String msg="";

        switch(item.getItemId()) {
            case R.id.close_icon:
                msg = "Settings Closed";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    Intent tr_intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(tr_intent, ActivityOptions.makeSceneTransitionAnimation(SettingsActivity.this).toBundle());
                    finish();
                } else {
                    // without transition effects
                    Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }

        Toast.makeText(SettingsActivity.this,msg+ " check",Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }
    /**
     * Settings page to home page navigation
     */
    @Override
    public void onBackPressed(){
        Intent homeIntent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(homeIntent);
        super.onBackPressed();
    }
}