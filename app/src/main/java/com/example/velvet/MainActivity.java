package com.example.velvet;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Implements a
 * @Author Victor Chuol
 *
 * **/
public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    GoogleSignInClient signInClient;
    Button nextBtn; ScrollView scroll;
    int count =0; GridLayout gridLayout;
    TextView f;//UserSingleton singleton;

    /**
     * initialize buttons and views
     * **/
    private void initUI(){
        nextBtn = findViewById(R.id.next_btn);
        scroll = findViewById(R.id.scroll_main);
        gridLayout = findViewById(R.id.grid_layout);
    }

    /**
     * Security feature removing back button
     */
    @Override
    public void onBackPressed() {
        //REMOVE BACK BUTTON FUNCTION
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item =  menu.findItem(R.id.close_icon);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String msg="";
        switch(item.getItemId()){
            case R.id.settings_icon:
                msg="Settings Opened";
                // Activity Transition support
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    //with transition effects
                    Intent tr_intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    startActivity(tr_intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    finish();
                } else {
                    // without transition effects
                    Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
                finish();
                break;
        }
        Toast.makeText(MainActivity.this,msg+ "check",Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //** inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        // set an exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**toolbar support**/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Projects");

        /** Custom init buttons function **/
        initUI();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProjectsActivity.class);
                startActivity(intent);
            }
        });

        //initialize firebase auth & user
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            //read & asign firebase info ex. image
        }

        signInClient = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        UserSingleton singleton = new UserSingleton();

        singleton.getInstance().setAuth(firebaseAuth);
        singleton.getInstance().setGoogleSignInClient(signInClient);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "New project created", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                count+=1;
                /*NEED TO STORE PROJECT NAME: MONGODB**/

                ImageButton project_btn = new ImageButton(MainActivity.this);
                project_btn.setBackgroundColor(0000);
                project_btn.setImageResource(R.drawable.ic_folder2);

                gridLayout.addView(project_btn);


            }
        });
    }
}