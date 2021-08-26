package com.example.velvet.Views;
import com.example.velvet.Models.MainViewModel;
import com.example.velvet.Fragments.ProjectNameDialog;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.example.velvet.Helper.Time;


import com.example.velvet.Project;
import com.example.velvet.Adapter.ProjectsAdapter;
import com.example.velvet.R;
import com.example.velvet.Helper.UserSingleton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * @Author Victor Chuol
 *
 * **/
public class  MainActivity extends AppCompatActivity implements ProjectNameDialog.projectNameDialogListener  {
    private ScrollView scroll;
    private UserSingleton singleton;
    private String TAG = "MainActivity";
    private MainViewModel mainViewModel;
    public RecyclerView recyclerView;
    private ProjectsAdapter projectsAdapter;
    private int nCol=4;

    /**
     * initialize buttons and views
     * **/
    private void initUI(){
        setContentView(R.layout.activity_main);
        scroll = findViewById(R.id.scroll_main);
        recyclerView = findViewById(R.id.grid_layout);
        recyclerView.setLayoutManager(new GridLayoutManager(this,nCol));
        toolbarInit();
        //create projects adapter
        projectsAdapter = new ProjectsAdapter(getApplication());
        recyclerView.setAdapter(projectsAdapter);
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
                    Intent tr_intent = new Intent(getApplicationContext(), SettingsActivity.class);
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
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize toolbar
     * **/
    private void toolbarInit(){
        /**toolbar support**/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Projects");
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

        /** Custom ui init function **/
        initUI();


        /**Initialize User singleton**/
        singleton = new UserSingleton();
        singleton.getInstance().setContext(MainActivity.this);
        singleton.getInstance().setAuth(FirebaseAuth.getInstance());
        singleton.getInstance().setGoogleSignInClient(GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN));

        /**Initialize MainViewModel**/
        mainViewModel = new MainViewModel(getApplication());
        mainViewModel = new ViewModelProvider(MainActivity.this).get(mainViewModel.getClass());
        mainViewModel.init(singleton,projectsAdapter,recyclerView);

        //observe changes too mainViewModel
        mainViewModel.getAllProjects().observe(MainActivity.this, new Observer<ArrayList<View>>() {
            @Override
            public void onChanged(ArrayList<View> views) {
                projectsAdapter.setProjects(mainViewModel.getAllProjects().getValue());
                recyclerView.setAdapter(projectsAdapter);
                Log.i(TAG,"MainViewModel Data changed");
            }
        });
        //onclick functionality
        projectsAdapter.createOnClick(mainViewModel);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                addProjectDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


                            /**ProjectNameDialog METHODS**/

    /**
     * Creates add project Dialog
     * **/
    public void addProjectDialog(){
        ProjectNameDialog projectDialog = new ProjectNameDialog();
        projectDialog.show(getSupportFragmentManager(),"project Dialog");
    }

    /**
     * Cancel Dialog
     * **/
    @Override
    public void cancelName() {
        Toast.makeText(MainActivity.this, "Project not created",Toast.LENGTH_LONG).show();
    }
    /**
     * -->ProjectNameDialog:
     * Apply text to Project Button
     * **/
    @Override
    public void applyTexts(String projectName) {
        //formats project object
        Project project = new Project(projectName,new Time().getCurrentDate(),new Time().getCurrentTime());

        //push project too firebase
        mainViewModel.createNewProject(project);
        projectsAdapter.setProjects(mainViewModel.getAllProjects().getValue());
        recyclerView.setAdapter(projectsAdapter);
        Log.i("ApplyTexts","currentsize = "+projectsAdapter.getItemCount());
        Toast.makeText(MainActivity.this, "New project created",Toast.LENGTH_LONG).show();        Toast.makeText(MainActivity.this, "New project created",Toast.LENGTH_LONG).show();


    }

}

