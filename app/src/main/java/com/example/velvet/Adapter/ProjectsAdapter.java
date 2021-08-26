package com.example.velvet.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.util.Log;
import com.example.velvet.Models.MainViewModel;
import com.example.velvet.Project;
import com.example.velvet.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Project> arr;
    private final Context context;
    private ArrayList<View> projects;
    private LayoutInflater inflater;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemClickListener onClickListener;
    private MainViewModel mainViewModel;
    public ProjectsAdapter (Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        projects = new ArrayList<View>();
    }
    /**
     * initialize mainviewmodel used for onclick
     * **/
    public void createOnClick(MainViewModel model){
        this.mainViewModel = model;
    }

    /**
     * Add View too projects
     * @param project project too be added
     * **/
    public void add(View project){
        projects.add(project);
    }

    /**
     * Replace contents of projects
     * @views new state of project arraylist
     * */
    public void setProjects(ArrayList<View> views) {
        projects.clear();
        projects.addAll(views);
    }

    /**
     * Layout manager invokes and creates new views
     * @param parent
     * @param viewType
     * **/
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_projects,parent,false);
        return new ViewHolder(view);
    }

    /**
     * layout manager invokes & replaces contents of views
     * @param holder
     * @param position
     * **/
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       ViewHolder mviewHolder =(ViewHolder) holder;
       mviewHolder.button.setText(( (Button) projects.get(position)).getText());
       mviewHolder.button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.i("Adapter","Onclick selected");
               mainViewModel.createProjectPage(mviewHolder.button);
           }
       });
       mviewHolder.button.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               mainViewModel.createLongClick(mviewHolder.button);
               Log.i("Adapter","LongClick selected");
               return false;
           }
       });
    }

    /**
     * Reference to type of views that use ViewHolder
     * */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.grid_button);
        }
        public Button getButtonView(){
            return button;
        }
    }

    /**
     * Return size of data set
     * */
    @Override
    public int getItemCount() {
        return projects.size();
    }
}
