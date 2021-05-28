package com.example.velvet;

import android.content.Context;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class UserSingleton {
    //private static final UserSingleton instance = new UserSingleton();
    //private static final UserSingleton instance = null ;
    private String name;
    private String email;
    private String userName;
    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;

    UserSingleton() {
    //initialize user
        //need account
  //      name = "";
    }

    public void setEmail(String s){
        email = s ;
        //email = (String) firebaseAuth.getCurrentUser().getEmail();
    }
    public void setGoogleSignInClient(GoogleSignInClient c){
        signInClient = c;
    }
    public GoogleSignInClient getGoogleSignInClient(){
        return signInClient;
    }
    public void setAuth(FirebaseAuth f){
        firebaseAuth = f;
    }
    public FirebaseAuth getAuth(){
        return firebaseAuth;
    }

    public String getEmail(){
        return email;
    }

    public static UserSingleton getInstance(){

        return loadInstance.instance;
    }

    //lazy thread safe instatiation of instance using application context
    private static class loadInstance{
        static final UserSingleton instance = new UserSingleton();
    }


}
