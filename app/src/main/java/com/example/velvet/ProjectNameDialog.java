package com.example.velvet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


public class ProjectNameDialog extends AppCompatDialogFragment {
    private EditText projectEditText;
    private projectNameDialogListener listener;
    @androidx.annotation.NonNull
    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_project_name_dialog,null);
        builder.setView(view).setTitle("Name of project").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.cancelName();
            }
        }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** send to database and set name of project **/
                String projectName = projectEditText.getText().toString();
                listener.applyTexts(projectName);
            }
        });

        projectEditText = view.findViewById(R.id.add_project_frag);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (projectNameDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "Project Name dialog implementation error");
        }
    }

    public interface projectNameDialogListener{
        void applyTexts(String projectName);
        void cancelName();
    }
}
