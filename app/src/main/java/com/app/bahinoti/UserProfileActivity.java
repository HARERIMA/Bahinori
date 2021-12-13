package com.app.bahinoti;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewFullname, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Home");

        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullname = findViewById(R.id.textView_show_fullName);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewDoB =findViewById(R.id.textView_show_dob);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progressBar);

        //Set OnClickListener on ImageView to open UploadProfilePicActivity
        //imageView = findViewById(R.id.imageView_profile_dp);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserProfileActivity.this, UploadProfilePicActivity.class);
//                startActivity(intent);
//            }
//        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong. User's details are not available at the moment", Toast.
                    LENGTH_LONG).show();
        }
        else{
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    //Users come to UserProfileActivity after successfully registration

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can't login without email verification next time");

        //Open Email Apps if user clicks/tabs continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//To email app in new window and not within our app
                startActivity(intent);

            }
        });
        //Create the alert dialog
        AlertDialog alertDialog = builder.create();
        //Show the alert dialog
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {

       String userId = firebaseUser.getUid();

       //Extracting user reference from database for "Registered users"
       DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readWriteUserDetails.doB;
                    gender = readWriteUserDetails.gender;
                    mobile = readWriteUserDetails.mobile;

                    textViewWelcome.setText("Welcome, " + fullName + "!");
                    textViewFullname.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //Set user picture (after user uploaded)
                   // Uri uri = firebaseUser.getPhotoUrl();

                    // Image Viewer setImageUri() should not be used with regular Uris. so we are using picasso
                    //Picasso.with(UserProfileActivity.this).load(uri).into(imageView);
                }
//                else{
//                    Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast
                        .LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //Creating actionBar Menu

    public boolean onCreateOptionsMenu(Menu menu){
//Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh){
            //Refresh activity
            startActivity(getIntent());
            finish();
           overridePendingTransition(0,0);
       }
       else if (id == R.id.menu_update_profile){
           Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        }
        /*else if (id == R.id.menu_update_email){
//            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
//            startActivity(intent);
//        }
//        else if (id == R.id.menu_settings){
//            Toast.makeText(UserProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
//        }
//        else if (id == R.id.menu_change_password){
//            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
//            startActivity(intent);
//        }
//        else if (id == R.id.menu_delete_profile){
//            Intent intent = new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
//            startActivity(intent);
        }*/
        else if (id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);

            //Clear stack to prevent user coming back to UserProfileActivity on pressing back button after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();//Close UserProfileActivity
        }
        else{
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}