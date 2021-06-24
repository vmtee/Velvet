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
    FirebaseAuth
            firebaseAuth;
    GoogleSignInClient signInClient;
    Button nextBtn; ScrollView scroll;
    GridLayout gridLayout; UserSingleton singleton;
    static ArrayList<View> viewArrayList;
    private String TAG = "MainActivity";
    String intentProjectName;
    private FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    private DatabaseReference projectRef ;

    @Override
    protected void onStart(){
        super.onStart();
   }
   protected void loadIndividualProject(String key){
        //DatabaseReference prRef = rootNode.getReference("projects");
        projectRef = rootNode.getReference("projects");
        projectRef.child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Project project = new Project(snapshot.getValue(String.class),getCurrentDate(),getCurrentTime());
                Button button = new Button(MainActivity.this);
                button.setText(snapshot.getValue(String.class));
                gridLayout.addView(button); viewArrayList.add(button);
                createButtonLongClick(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createProjectPage(button);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


   }
   protected void loadUserProjects(FirebaseUser firebaseUser){
        DatabaseReference userRef = rootNode.getReference("users");
        String userID = firebaseUser.getUid();
        if(firebaseUser != null){
            userRef.child(userID).child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String TAG = "onDataChange";
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Boolean projectBool = dataSnapshot.getValue(Boolean.class);
                            if(projectBool==Boolean.TRUE){
                                loadIndividualProject(dataSnapshot.getKey());
                                Log.i(TAG,dataSnapshot.getKey());
                            }
            /**TESTING**/   Log.i(TAG,"Snapshot Exists "+projectBool+" ");
                        }
                    }else{
            /**TESTING**/   Log.i(TAG,"Snapshot Does not Exist");
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
        /**added line**/

        DatabaseReference userRef = rootNode.getReference("users");
        DatabaseReference clusterRef = rootNode.getReference("mediaCluster");
        String projectKey = ("PR:" + projectRef.push().getKey());//retrieve unique projectKey
        String mediaClusterKey = ("MC:" + clusterRef.push().getKey());

        Boolean exists = Boolean.TRUE;Boolean DNexist = Boolean.FALSE;
        userRef.child(userID).child("projects").child(projectKey).setValue(exists);

        projectRef.child(projectKey).child("name").setValue(project.getName());
        projectRef.child(projectKey).child("dayCreated").setValue(project.getDayCreated());
        projectRef.child(projectKey).child("timeStamp").setValue(project.getTimeStamp());
        //default media cluster state
        projectRef.child(projectKey).child(mediaClusterKey).setValue(DNexist);

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
        singleton = new UserSingleton();
        singleton.getInstance().setAuth(firebaseAuth);
        singleton.getInstance().setGoogleSignInClient(signInClient);

        viewArrayList = new ArrayList<View>();

        Log.i(TAG,"OnCreate: Load-Existing-Projects" );
        loadUserProjects(firebaseUser);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                /**Create dialog**/
                addProjectDialog();


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
                        /******NEW INTENT POINT*******/
                        createProjectPage(projectButton);
                    }
                });

                createButtonLongClick(projectButton);
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

    public void createButtonLongClick(Button button){
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(MainActivity.this, "LongClick-->ACTIVATED",Toast.LENGTH_LONG).show();
                /**Create DELETEDialog logic here**/
                return false;
            }
        });
    }
    public void createProjectPage(Button projectButton){
        ProjectsActivity prActivity = new ProjectsActivity();

        intentProjectName = (String) projectButton.getText();

        String UID = firebaseAuth.getUid();
        FirebaseUser firebaseUser = UserSingleton.getInstance().getAuth().getCurrentUser();

        if(firebaseUser != null){
            projectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String TAG = "CreateProjectPage: ";
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            if(dataSnapshot.child("name").getValue().equals(intentProjectName)){
                                String intentProjectKey = dataSnapshot.getKey();
                                Intent intent = new Intent(MainActivity.this,prActivity.getClass());
                                intent.putExtra("projectKey",intentProjectKey);
                                startActivity(intent);
                   /**remove else line after testing**/
                            }else{
                                Log.i(TAG,"DataSnapshotValue: FALSE "+ dataSnapshot.child("name").getValue());
                            }

                        }
                    }else{
                        /**TESTING**/   Log.i(TAG,"Snapshot Does not Exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
                            /**DIALOG METHODS**/
    /**
     * -->ProjectNameDialog:
     * removes button when cancel selected
     * **/
    @Override
    public void cancelName() {
        int s = viewArrayList.size();
        int ls = gridLayout.getChildCount();
        viewArrayList.remove(s-1);
        gridLayout.removeViewAt(ls-1);
        String TAG = "cancelName: ";
        Log.i(TAG, "size of array-list = " + s);
        Log.i(TAG, "size of grid-layout = " + ls);
    }
    /**
     * -->ProjectNameDialog:
     * Apply text to Project Button
     * **/
    @Override
    public void applyTexts(String projectName) {
        int s = viewArrayList.size();
        Button b =(Button) viewArrayList.get(s-1);
        //Button bb = gridLayout.getChildAt()
        b.setText(projectName);
        viewArrayList.add(s-1,b);
        //gridLayout.addView();

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

