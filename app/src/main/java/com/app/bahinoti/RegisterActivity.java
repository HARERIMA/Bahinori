package com.app.bahinoti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

  private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDob, editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;
  private ProgressBar progressBar;
  private RadioGroup radioGroupRegisterGender;
  private RadioButton radioButtonRegisterGenderSelected;
  private DatePickerDialog picker;
  private static  final String TAG ="RegisterActivity";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    getSupportActionBar().setTitle(" User registration page");

    Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

    progressBar = findViewById(R.id.progressBar);
    editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
    editTextRegisterEmail = findViewById(R.id.editText_register_email);
    editTextRegisterDob = findViewById(R.id.editText_register_dob);
    editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
    editTextRegisterPwd = findViewById(R.id.editText_register_password);
    editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

    //Radio button for gender
    radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
    radioGroupRegisterGender.clearCheck();

    //Setting up DatePicker on EditText
    editTextRegisterDob.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        //DatePicker dialog
        picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            editTextRegisterDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

          }
        },year, month, day);
        picker.show();
      }
    });
    Button buttonRegister = findViewById(R.id.button_register);
    buttonRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
        radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

        //Obtain the entered data

        String textFullName = editTextRegisterFullName.getText().toString();
        String textEmail = editTextRegisterEmail.getText().toString();
        String textDob = editTextRegisterDob.getText().toString();
        String textMobile = editTextRegisterMobile.getText().toString();
        String textPwd = editTextRegisterPwd.getText().toString();
        String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
        String textGender;// can't obtain the value before verify if any button was selected or not

        //Validate mobile number using Matcher and Pattern (Regular expression)
        String mobileRegex = "[3-6-7][0-9]{7}";//first number can be {6,8,9} and rest 9 nos. can be any no
        Matcher mobileMatcher;
        Pattern mobilePattern= Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);
        if (TextUtils.isEmpty(textFullName)){
          Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
          editTextRegisterFullName.setError("Full name is required");
          editTextRegisterFullName.requestFocus();
        }
        else if (TextUtils.isEmpty(textEmail)){
          Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
          editTextRegisterEmail.setError("Email is required");
          editTextRegisterEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
          Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
          editTextRegisterEmail.setError("Valid email is required");
          editTextRegisterEmail.requestFocus();
        }
        else if (TextUtils.isEmpty(textDob)){
          Toast.makeText(RegisterActivity.this, "Please enter date of birth", Toast.LENGTH_LONG).show();
          editTextRegisterDob.setError("DOB is required");
          editTextRegisterDob.requestFocus();
        }
        else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1){
          Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
          radioButtonRegisterGenderSelected.setError("Gender is required");
          radioButtonRegisterGenderSelected.requestFocus();
        }
        else if (TextUtils.isEmpty(textMobile)){
          Toast.makeText(RegisterActivity.this, "Please enter mobile number", Toast.LENGTH_LONG).show();
          editTextRegisterMobile.setError("Mobile no is required");
          editTextRegisterMobile.requestFocus();
        }
        else if (textMobile.length()<8 || textMobile.length()>10){
          Toast.makeText(RegisterActivity.this, "Please re-enter mobile number", Toast.LENGTH_LONG).show();
          editTextRegisterMobile.setError("Mobile no should be >8 and <10 digits");
          editTextRegisterMobile.requestFocus();
        }
        else if (!mobileMatcher.find()) {
          Toast.makeText(RegisterActivity.this, "Please re-enter mobile number", Toast.LENGTH_LONG).show();
          editTextRegisterMobile.setError("Mobile no is not valid");
          editTextRegisterMobile.requestFocus();
        }
        else if (TextUtils.isEmpty(textPwd)){
          Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
          editTextRegisterPwd.setError("password is required");
          editTextRegisterPwd.requestFocus();
        }
        else if (textPwd.length()<6){
          Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
          editTextRegisterPwd.setError("Password too weak");
          editTextRegisterPwd.requestFocus();
        }
        else if (TextUtils.isEmpty(textConfirmPwd)){
          Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
          editTextRegisterConfirmPwd.setError("Password confirmation is required");
          editTextRegisterConfirmPwd.requestFocus();
        }
        else if (!textPwd.equals(textConfirmPwd)) {
          Toast.makeText(RegisterActivity.this, "Please same password", Toast.LENGTH_LONG).show();
          editTextRegisterConfirmPwd.setError("Confirmation password is required");
          editTextRegisterConfirmPwd.requestFocus();
          //Clear the entered password
          editTextRegisterPwd.clearComposingText();
          editTextRegisterConfirmPwd.clearComposingText();
        }
        else{
          textGender = radioButtonRegisterGenderSelected.getText().toString();
          progressBar.setVisibility(View.VISIBLE);
          registerUser(textFullName, textEmail, textDob, textMobile, textGender, textPwd);
        }
      }
    });

  }

  //Register user using the credentials given

  private  void  registerUser(String textFullName, String textEmail, String textDob, String textMobile, String textGender, String textPwd){
    FirebaseAuth auth =FirebaseAuth.getInstance();

    //Create user profile
    auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
          Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_LONG).show();
          FirebaseUser firebaseUser = auth.getCurrentUser();

          //Update display name of user

          UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
          firebaseUser.updateProfile(profileChangeRequest);

          //Enter user data into the firebase Realtime database.
          ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDob,textGender,textMobile );

          //Extracting user reference from database for "Registered users"
          DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Registered Users");
          referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails)
                  .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                      if (task.isSuccessful()){

                        //Send verify email
                        firebaseUser.sendEmailVerification();

                        Toast.makeText(RegisterActivity.this, "User registered successfully.please verify your email",
                                Toast.LENGTH_LONG).show();

                        //Open user profile after successfully registration
                        Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                        //To prevent user from returning back to register activity on pressing back button after registration
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();//To close register
                      }
                      else{

                        Toast.makeText(RegisterActivity.this, "User registered failed.please try again",
                                Toast.LENGTH_LONG).show();
                      }

                      //Hide progressBar whether use creation is successfully or failed
                      progressBar.setVisibility(View.GONE);
                    }
                  });
        }
        else{
          try {
            throw task.getException();
          }catch (FirebaseAuthWeakPasswordException e){
            editTextRegisterPwd.setError("Your password is too weak. kindly use a mixte of alphabet, numer and special characters");
            editTextRegisterPwd.requestFocus();
          }catch (FirebaseAuthInvalidCredentialsException e){
            editTextRegisterPwd.setError("Your email is invalid or already in use. kindly re-enter");
            editTextRegisterPwd.requestFocus();
          }catch (FirebaseAuthUserCollisionException e){
            editTextRegisterPwd.setError("User is already registered with this email.Use an other email");
            editTextRegisterPwd.requestFocus();
          }catch (Exception e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(RegisterActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
          }

          //Hide progressBar whether use creation is successfully or failed
          progressBar.setVisibility(View.GONE);
        }

      }
    });

  }
}