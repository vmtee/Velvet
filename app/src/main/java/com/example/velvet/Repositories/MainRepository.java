package com.example.velvet.Repositories;
import com.example.velvet.Adapter.ProjectsAdapter;
import com.example.velvet.Helper.*;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;


import com.example.velvet.Project;
import com.example.velvet.Views.ProjectsActivity;
import com.example.velvet.Helper.UserSingleton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

//all connections to database
public class MainRepository {
    private static Context context;
    private static UserSingleton userSingleton = new UserSingleton();
    private FirebaseDatabase rootNode ;
    private DatabaseReference projectRef ;
    private static MainRepository repoInstance;
    private ArrayList<View> list = new ArrayList<>();
    private static ProjectsAdapter projectsAdapter;
    private static RecyclerView recyclerView;

    public static MainRepository getInstance(Context c, UserSingleton singleton,
                                             ProjectsAdapter adapter, RecyclerView view){
        if (repoInstance ==null ){
            Log.i("getInstance","TRUE");
            repoInstance = new MainRepository();
            context = c;
            userSingleton = singleton;
            projectsAdapter = adapter;
            recyclerView = view;
        }else {
            Log.i("getInstance","False");
            projectsAdapter.setProjects(repoInstance.getAllProjects().getValue());
            recyclerView.setAdapter(projectsAdapter);
        }

        return repoInstance;
    }
    /**
     * Push project too Database & and update project list
     * @param button
     * @param project
     * **/
    public void addProject(Button button, Project project) {
        pushProjectToFirebase(project);
        list.add(button);
    }
    /**
     * Returns all current projects
     * **/
    public MutableLiveData<ArrayList<View>> getAllProjects(){
        rootNode = FirebaseDatabase.getInstance();
        list = loadUserProjects(userSingleton.getInstance().getAuth().getCurrentUser());
        MutableLiveData<ArrayList<View>> data = new MutableLiveData<>();
        data.setValue(list);
        return data;
    }
    /**
     * Load user projects From database
     * @param firebaseUser Firebase User instance
     * **/
    protected ArrayList<View> loadUserProjects(FirebaseUser firebaseUser){
        DatabaseReference userRef = rootNode.getReference("users");
        String userID = firebaseUser.getUid();
        if(firebaseUser != null){
            userRef.child(userID).child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Boolean projectBool = dataSnapshot.getValue(Boolean.class);
                            if(projectBool==Boolean.TRUE){
                                loadIndividualProject(dataSnapshot.getKey());
                            }
                        }
                    }else{
                        throw new RuntimeException("Projects snapshot: Does not Exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Log.i("list1 size = ",  ""+list.size()+"" +projectsAdapter.getItemCount() );
            return list;
        }else {
            throw new RuntimeException("FirebaseUser is null");
            //return null;
        }
    }

    /**
     * Read and set names of individual project
     * @param key Key of Project
     * **/
    protected void loadIndividualProject(String key){
        projectRef = rootNode.getReference("projects");
        projectRef.child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Button button = new Button(context);
                button.setText(snapshot.getValue(String.class));
                list.add(button);
                projectsAdapter.add(button);
                recyclerView.setAdapter(projectsAdapter);
                Log.i("LoadIndividualProject","RepositorySize =" +list.size());
                Log.i("LoadIndividualProject","AdapterSize =" +projectsAdapter.getItemCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw new RuntimeException("Database Error: Load");
            }
        });
    }

    /**
     * Create on Long Click Functionality
     * @param button Button to be modified
     * **/
    public void createButtonLongClick(Button button){
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(MainActivity.this, "LongClick-->ACTIVATED",Toast.LENGTH_LONG).show();
                /**Create DELETEDialog logic here**/
                Log.i("OnLongClick: ","Project Long Click");
                return false;
            }
        });
    }

    /**
     * Create Project intent
     * @param projectButton button navigation too project
     * **/
    public void createProjectPage(Button projectButton){
        ProjectsActivity prActivity = new ProjectsActivity();
        DatabaseReference projectRef = rootNode.getReference("projects");
        String intentProjectName = (String) projectButton.getText();
        String TAG = "CreateProjectPage: ";

        FirebaseUser firebaseUser = userSingleton.getInstance().getAuth().getCurrentUser();

        if(firebaseUser != null){
            projectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Log.i(TAG," snapshot exists");
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            if(dataSnapshot.child("name").getValue().equals(intentProjectName)){
                                String intentProjectKey = dataSnapshot.getKey();
                                Intent intent = new Intent(context,prActivity.getClass());
                                intent.putExtra("projectKey",intentProjectKey);
                                context.startActivity(intent);
                            }
                        }
                    }else{
                        Log.i(TAG,"Snapshot Does not Exist");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw new RuntimeException("Database Error: Load");
                }
            });
        }else throw new RuntimeException("FirebaseUser is null");

    }


                                        /**UPLOAD***/
    /**
     * Push project objects too realtime database
     * @param  project Project too be pushed
     * **/
    public void pushProjectToFirebase(Project project){

        String userID = UserSingleton.getInstance().getUID();
               // firebaseAuth.getCurrentUser().getUid();
        /**added line**/
        DatabaseReference userRef = rootNode.getReference("users");
        DatabaseReference clusterRef = rootNode.getReference("mediaCluster");
        DatabaseReference projectRef = rootNode.getReference("projects");

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


}
