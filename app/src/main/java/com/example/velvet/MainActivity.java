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

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.transition.Explode;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.velvet.ProjectNameDialog.projectNameDialogListener;
import org.w3c.dom.Text;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author Victor Chuol
 *
 * **/
public class MainActivity extends AppCompatActivity implements ProjectNameDialog.projectNameDialogListener {
    FirebaseAuth firebaseAuth;
    GoogleSignInClient signInClient;
    Button nextBtn; ScrollView scroll;
    GridLayout gridLayout; UserSingleton singleton;
    static ArrayList<View> viewArrayList;
    private String TAG = "MainActivity";

    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    DatabaseReference projectRef ; DatabaseReference projectRef1;
    @Override
    protected void onStart(){
        super.onStart();
   }

   /**
    * Load Projects of user using firebase userID
    * **/
   protected void loadExistingProjects(FirebaseUser firebaseUser){
        if(firebaseUser != null) {
            projectRef = rootNode.getReference("projects2"); /**Testing Database**/

            String userID = firebaseUser.getUid();
            projectRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            //projectRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if( snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Button button = new Button(MainActivity.this);
                            Project project = new Project();
                            project.setName(dataSnapshot.child("name").getValue(String.class));
                            project.setDayCreated(dataSnapshot.child("dateCreated").getValue(String.class));
                            button.setText(project.getName());
                            gridLayout.addView(button);
       /**TESTING**/        Log.i(TAG, "onDataChange: snapshot procedure PASSED");
                        }
                    }else {
       /**TESTING**/        Log.i(TAG, "onDataChange: snapshot does not exist " );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
    /**
     * Push project objects too realtime database
     * **/
    public void pushProjectToFirebase(Project project){
        String userID = firebaseAuth.getCurrentUser().getUid();
        String key = projectRef.child(userID).push().getKey();//retrieve unique key

        projectRef.child(userID).child(key).child("name").setValue(project.getName());
        projectRef.child(userID).child(key).child("dayCreated").setValue(project.getDayCreated());
        projectRef.child(userID).child(key).child("timeStamp").setValue(project.getTimeStamp());

    }

    private void createFullscreenFragment(Button button){
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });
    }

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

    /** Create Action Bar**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item =  menu.findItem(R.id.close_icon);
        item.setVisible(false);
        return true;
    }

    /**
     * Toolbar Support:
     * --> Settings page transition
     * **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String msg="";
        switch(item.getItemId()){
            case R.id.settings_icon:
                msg="Settings Opened";
                // Transition support
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
        //** inside your activity (if transitions enabled theme)
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        // set an exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Custom init buttons function **/
        initUI();

        /**toolbar support**/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Projects");

        /**  TESTING BUTTON **/
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
        signInClient = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        /**Initialize User singleton**/
        //UserSingleton singleton = new UserSingleton();
        singleton = new UserSingleton();
        singleton.getInstance().setAuth(firebaseAuth);
        singleton.getInstance().setGoogleSignInClient(signInClient);

        //viewArrayList = new ArrayList<View>();
        viewArrayList = new ArrayList<View>();

        Log.i(TAG,"OnCreate: Load-Existing-Projects" );
        loadExistingProjects(firebaseUser);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                /**Create dialog**/
                addProjectDialog();

                /** TO-DO:
                 *      ---> IMPLEMENT STORAGE OF PROJECT NAME
                 *      IN FIREBASE WITHIN DIALOG FRAGMENT
                 * **/

/** UI-FOLDER-FEATURE Removed for testing purposes
                ImageButton project_btn = new ImageButton(MainActivity.this);
                project_btn.setBackgroundColor(0000);
                project_btn.setImageResource(R.drawable.ic_folder2);
**/
                Button projectButton = new Button(MainActivity.this);
                projectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //implements INDIVIDUAL project page intent
                        /******ERROR POINT*******//***
                        projectFullscreenFragment frag = new projectFullscreenFragment();
                        Intent intent = new Intent(MainActivity.this,frag.getClass());
                        startActivity(intent);***/
                    }
                });
                viewArrayList.add(projectButton);
                gridLayout.addView(projectButton);

                Snackbar.make(view, "New project created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
                            /**DIALOG METHODS**/
    /**
     * -->ProjectNameDialog:
     * removes button when cancel selected
     * **/
    @Override
    public void cancelName() {
        int s = viewArrayList.size();
        viewArrayList.remove(s-1);
        gridLayout.removeViewAt(s-1);
    }
    /**
     * -->ProjectNameDialog:
     * Apply text to Project Button
     * **/
    @Override
    public void applyTexts(String projectName) {
        int s = viewArrayList.size();
        Button b =(Button) viewArrayList.get(s-1);
        b.setText(projectName);
        viewArrayList.add(s-1,b);
        /***CREATE PROJECT IN FIREBASE**********/
        Project project = new Project(projectName,getCurrentDate(),getCurrentTime());
        pushProjectToFirebase(project);
        /**TESTING OF TIME**/
        Log.i(TAG,"Dialog-applyTexts: Day-->" + project.getDayCreated());
        Log.i(TAG,"Dialog-applyTexts: Time-->" + project.getTimeStamp());
    }

    /**
     * -->ProjectNameDialog:
     * Creates project add Dialog
     * **/
    public void addProjectDialog(){
        ProjectNameDialog projectDialog = new ProjectNameDialog();
        projectDialog.show(getSupportFragmentManager(),"project Dialog");
    }
                                /**TIME METHODS**/
    /**
     * returns current Time of device
     * **/
    public String getCurrentTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentTime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss a");
        String time = dateFormat.format(currentTime);
        return time;
    }
    /**
     * returns current Date of device
     * **/
    public String getCurrentDate(){
        Date time = Calendar.getInstance().getTime();
        String formatTime = DateFormat.getDateInstance().format(time);
        return formatTime;
    }
}

