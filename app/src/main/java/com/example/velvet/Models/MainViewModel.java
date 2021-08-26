package com.example.velvet.Models;

import com.example.velvet.Helper.*;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import android.widget.Button;

import android.app.Application;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import com.example.velvet.Project;
import com.example.velvet.Adapter.ProjectsAdapter;
import com.example.velvet.Repositories.MainRepository;
import com.example.velvet.Helper.UserSingleton;

// prepares all data for Activities(UI)
public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<View>> allProjects;
    private MainRepository repository;
    private Context context ;
    private final String TAG = "MAinViewModel";

    /**
     * @param application Used for retrieving context
     * **/
    public MainViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    /**
     * Class initialization
     * @param userSingleton Current user singleton
     * @param adapter Current MainActivity adapter
     * @param recyclerView Current MainActivity recyclerView
     * **/
    public void init(UserSingleton userSingleton,ProjectsAdapter adapter, RecyclerView recyclerView){
        if(allProjects != null){
            Log.i(TAG,"projects initialized");
            return;
        }
        //if null init repo
        repository = MainRepository.getInstance(context,
                userSingleton,adapter,recyclerView);
        allProjects = repository.getAllProjects();
    }

    /***
     * Returns current version of allProjects
     * */
    public MutableLiveData<ArrayList<View>> getAllProjects(){
        return allProjects;
    }

    /***
     * Uploads a New user project
     * @param project Project object too be uploaded
     * */
    public void createNewProject(Project project){
        Button button = new Button(context);
        button.setText(project.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.createProjectPage(button);
            }
        });
        repository.createButtonLongClick(button);
        repository.addProject(button,project);
    }
    /**
     * Creates onClick functionality for a button
     * @param b Button who's onclick is modified
     * **/
    public void createProjectPage(Button b){
        repository.createProjectPage(b);
    }

    /**
     * Creates onLongClick functionality for a button
     * @param b Button who's onclick is modified
     * **/
    public void createLongClick(Button b){repository.createButtonLongClick(b);};
}
