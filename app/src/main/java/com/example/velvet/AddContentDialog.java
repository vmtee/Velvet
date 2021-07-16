package com.example.velvet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;
import android.widget.ArrayAdapter;

public class AddContentDialog extends AppCompatDialogFragment {
    private ListView listView;
    private addContentDialogListener listener;
    public AddContentDialog(addContentDialogListener listener){
        super();
        this.listener = listener;
    }
    public AddContentDialog(){
        super();
    }
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_content_dialog,null);
        builder.setView(view)
                .setTitle("Select Content Type").setNegativeButton("cancel",null);


        String[] media = {"Image","Audio","Label"};

        listView = (ListView) view.findViewById(R.id.add_content_frag);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,media);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: //
                        listener.loadImage();
                        getDialog().dismiss();
                        break;
                    case 1: //
                        Toast.makeText(getContext(), "Audio Selected",Toast.LENGTH_LONG).show();
                        listener.loadAudio();
                        getDialog().dismiss();
                        break;
                    case 2: //
                        Toast.makeText(getContext(), "Label- Selected",Toast.LENGTH_LONG).show();
                        listener.createLabel();
                        getDialog().dismiss();
                        break;
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public interface addContentDialogListener{
        void loadImage();
        void loadAudio();
        void createLabel();
    }

}
