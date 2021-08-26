package com.example.velvet.Helper;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Context;

public class UserSingleton {
    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
    private String UID; private Context context;

    public UserSingleton(){}

    public void setContext(Context context) {
        this.context = context;
    }
    public Context getContext() {
        return context;
    }

    public void setGoogleSignInClient(GoogleSignInClient c){
        signInClient = c;
    }
    public GoogleSignInClient getGoogleSignInClient(){
        return signInClient;
    }

    public void setAuth(FirebaseAuth f){
        firebaseAuth = f; UID = firebaseAuth.getUid();
    }
    public FirebaseAuth getAuth(){
        if(UID == null || firebaseAuth == null){
            //throw an exception
            throw new RuntimeException("Firebase auth not instantiated");
        }
        return firebaseAuth;
    }

    public String getUID(){return UID;}
    public static UserSingleton getInstance(){

        return loadInstance.instance;
    }

    //lazy thread safe instatiation of instance using application context
    private static class loadInstance{
        static final UserSingleton instance = new UserSingleton();
    }


}
