package com.example.velvet.Views;

import com.example.velvet.Fragments.AddContentDialog;
import android.annotation.SuppressLint;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;

import com.example.velvet.R;
import com.example.velvet.Helper.UserSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.database.Cursor;
import java.io.ByteArrayOutputStream;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProjectsActivity extends AppCompatActivity implements AddContentDialog.addContentDialogListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView; private View scrollView;
    private GridLayout gridLayout; private String TAG = "ProjectsActivity";
    private FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    private DatabaseReference clusterRef = rootNode.getReference("mediaCluster");
    private DatabaseReference projectRef = rootNode.getReference("projects");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private Uri imageUri; private String projectKey;
    private String thisProjectName; private TextView titleView;
    private Dialog labelDialog;
    //private String URIname;
    final long ONE_MB = 1024*1024;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    /**
     * Convert byte date to an imageView objects
     * **/
    private ImageView convertToImageView(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        ImageView imageView = new ImageView(ProjectsActivity.this);
        imageView.setImageBitmap(bitmap);
        imageView.setBackgroundColor(0000);
        return imageView;
    }
    /**
     * Load an image from firebase storage
     * **/
    private void loadImage(String imageName){
        String UID = UserSingleton.getInstance().getAuth().getUid();
        String refString = "images/"+UID+"/"+imageName;
        StorageReference imageRef = storageRef.child(refString);

        imageRef.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ImageView imageView = convertToImageView(bytes);
                imageView.setAdjustViewBounds(true);
                /**Temporary design use**/
                gridLayout.addView(imageView);//gridLayout.addView(imageView,position);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    /**
     * Loads Media from realtime database
     * **/
    private void loadIndividualMedia(String clusterKey){
        clusterRef.child(clusterKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if(dataSnapshot.getKey().startsWith("img:")){/*/**/
                        //loadImage(dataSnapshot.child("link").getValue(String.class),
                          //      dataSnapshot.child("position").getValue(Integer.class));
                        //int position = dataSnapshot.child("position").getValue(Integer.class);
                        loadImage(dataSnapshot.child("link").getValue(String.class));

                    }
                    if(dataSnapshot.getKey().startsWith("lbl:")){/*/**/
                        TextView textView = new TextView(ProjectsActivity.this);
                        textView.setText(dataSnapshot.child("text").getValue(String.class));
                        textView.setBackgroundColor(0000);
                        textView.setHeight(30);textView.setTextSize(18);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        gridLayout.addView(textView);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /**
     * Retrieve all project media contained within a project
     * **/
    private void retrieveProjectMedia(String projectKey){
        projectRef.child(projectKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if(dataSnapshot.getKey().startsWith("MC")
                            && dataSnapshot.getValue().equals(true)){
                        loadIndividualMedia(dataSnapshot.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_projects);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        //scrollView =findViewById(R.id.scroll_view);
        gridLayout = findViewById(R.id.project_grid);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        /**READ project name**/
        Intent intent = getIntent();
        projectKey = intent.getStringExtra("projectKey");

        titleView = new TextView(ProjectsActivity.this);

        setProjectName(projectKey);
        Log.i("Project Key: ",projectKey);
        titleView.setTextSize(22);
        titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        gridLayout.addView(titleView);

        retrieveProjectMedia(projectKey);


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContentDialog();
            }
        });
    }
    /**
     * initializes text of the Title TextView as name of the current project
     * **/
    private void setProjectName(String projectKey){
        projectRef.child(projectKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Quick fix while text view alignment is broken
                        thisProjectName = "             "+snapshot.child("name").getValue(String.class);
                        titleView.setText(thisProjectName);
                        Log.i(TAG, "onDataChange: "+thisProjectName);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /**
     * Creates the add content dialog & calls required loading methods
     */
    public void addContentDialog(){
        AddContentDialog addContentDialog = new AddContentDialog(new AddContentDialog.addContentDialogListener() {
            @Override
            public void loadImage() {
                Log.i(TAG, "loadImage: selected");
                selectStoredImage();
            }

            @Override
            public void loadAudio() {
                Log.i(TAG, "loadAudio: not yet implemented");
            }

            @Override
            public void createLabel() {
                AlertDialog.Builder labelBuilder = new AlertDialog.Builder(ProjectsActivity.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View create_label_view = inflater.inflate(R.layout.fragment_create_label_dialog,null);
                final EditText editText = create_label_view.findViewById(R.id.create_label_frag);

                labelBuilder.setTitle("Add Note").setView(create_label_view).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String labelText = editText.getText().toString();
                        TextView textView = new TextView(ProjectsActivity.this);
                        textView.setText(labelText);
                        textView.setBackgroundColor(0000);
                        textView.setHeight(30);textView.setTextSize(18);
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        gridLayout.addView(textView);
                        uploadLabel(labelText);
                    }
                });

                labelBuilder.show();
            }
        });
        addContentDialog.show(getSupportFragmentManager(),"content dialog");
    }

    /**
     * dialog error handling
     * **/
    @Override
    public void loadImage(){}
    @Override
    public void loadAudio(){}
    @Override
    public void createLabel(){}

    /**
     * Upload Label text to realtime database
     */
    private void uploadLabelToDatabase(String clusterKey,String label){
        String imageKey = ("lbl:" + clusterRef.child(clusterKey).push().getKey());
        clusterRef.child(clusterKey).child(imageKey).child("text").setValue(label);
        clusterRef.child(clusterKey).child(imageKey).child("position").setValue(2);
    }
    /**
     * Access project media cluster
     */
    private void uploadLabel(String label){
        projectRef.child(projectKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.getKey().startsWith("MC:")
                            && dataSnapshot.getValue(boolean.class).equals(true)){
                        uploadLabelToDatabase(dataSnapshot.getKey(),label);
                    }else if(dataSnapshot.getKey().startsWith("MC:")
                            && dataSnapshot.getValue(boolean.class).equals(false)){
                        projectRef.child(projectKey).child(dataSnapshot.getKey()).setValue(true);
                        uploadLabelToDatabase(dataSnapshot.getKey(),label);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Extracts the title of an imageUri
     * **/
    private String getUriName(Uri imageUri){
        Cursor returnCursor = getContentResolver()
                .query(imageUri,null,null,null,null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        return returnCursor.getString(nameIndex);
    }
    /**
     * Handles image upload to database and storage
     * **/
    private void uploadImage(String clusterKey,ImageView imageView,String URIname){
        //build & enable drawing cache
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        byte[] imageData = convertBitmapToByteArray(imageView);
        /**upload to cloud storage**/
        uploadImageToStorage(imageData,URIname);

        //uploadImageToDatabase(clusterKey,URIname);
        uploadMediaToDatabase(clusterKey,URIname,"img");

    }
    /**
     * Converts imageView data to byte data
     * **/
    private byte[] convertBitmapToByteArray(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        return imageData;
    }
    /**
     * Uploads image information to Database
     * **/
    private void uploadImageToDatabase(String clusterKey, String URIname){
        String imageKey = ("img:" + clusterRef.child(clusterKey).push().getKey());
        clusterRef.child(clusterKey).child(imageKey).child("link").setValue(URIname);
        clusterRef.child(clusterKey).child(imageKey).child("position").setValue(29);
    }

    /***
     * Uploads various media types to Database
     */
    private void uploadMediaToDatabase(String clusterKey, String media, String type){
        //URiname as identifier
        if(type.equals("img")){
            String imageKey = ("img:" + clusterRef.child(clusterKey).push().getKey());
            clusterRef.child(clusterKey).child(imageKey).child("link").setValue(media);
            clusterRef.child(clusterKey).child(imageKey).child("position").setValue(29);
        }else if(type.equals("lbl")){ //label text as identifier
            String imageKey = ("lbl:" + clusterRef.child(clusterKey).push().getKey());
            clusterRef.child(clusterKey).child(imageKey).child("text").setValue(media);
            clusterRef.child(clusterKey).child(imageKey).child("position").setValue(2);
        }
    }

    /**
     * Uploads image to cloud storage
     * **/
    private void uploadImageToStorage(byte[] imageData, String URIname){
        String refString = "images/"+UserSingleton.getInstance().getAuth().getUid()+"/"+URIname;
        StorageReference imageRef = storageRef.child(refString);
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProjectsActivity.this,"Image Upload: Failure",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProjectsActivity.this,"Image Upload: Successful",Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Handles Selection of image from users local library
     * **/
    private void selectStoredImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageResultLauncher.launch(intent);
    }
    /***
     * Copy of selectStored for audio test
     */
    private void selectStoredAudio(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageResultLauncher.launch(intent);
    }

    /**
     * Retrieve image data content from intent result
     * */
    ActivityResultLauncher<Intent> imageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK && result != null &&result.getData() != null ){
                imageUri = result.getData().getData();
                ImageView imageView = new ImageView(ProjectsActivity.this);
                imageView.setImageURI(imageUri);
                imageView.setAdjustViewBounds(true);
                gridLayout.addView(imageView);

                String URIname = getUriName(imageUri);
                projectRef.child(projectKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if(dataSnapshot.getKey().startsWith("MC:")
                                    && dataSnapshot.getValue(boolean.class).equals(true)){
                                uploadImage(dataSnapshot.getKey(),imageView,URIname);//
                            }else if(dataSnapshot.getKey().startsWith("MC:")
                                    && dataSnapshot.getValue(boolean.class).equals(false)){
                                projectRef.child(projectKey).child(dataSnapshot.getKey()).setValue(true);
                                uploadImage(dataSnapshot.getKey(),imageView,URIname);//
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    });

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Projects page to home page navigation
     */
    @Override
    public void onBackPressed(){
        Intent homeIntent = new Intent(ProjectsActivity.this, MainActivity.class);
        startActivity(homeIntent);
        super.onBackPressed();
    }

}