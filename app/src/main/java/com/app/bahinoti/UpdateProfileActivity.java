package com.app.bahinoti;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDob, editTextUpdateMobile;
    private String textFullName, textEmail, textDoB, textGender, textMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update profile details page");

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDob = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show profile data
        showProfile(firebaseUser);
    }

    //Fetch data from database and display
    private void showProfile(FirebaseUser firebaseUser){

        String userIdOfRegistered = firebaseUser.getUid();

        //Extracting user reference from database
        DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Registered users");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIdOfRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null){

                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readWriteUserDetails.doB;
                    textGender = readWriteUserDetails.gender;
                    textMobile = readWriteUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDob.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    //Show gender through radio button
                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    }
                    else{
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }
                else{
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        });

    }
}