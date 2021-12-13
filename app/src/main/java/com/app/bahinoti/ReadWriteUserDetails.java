package com.app.bahinoti;

public class ReadWriteUserDetails {

    public String doB, gender, mobile;

    //Constructor
    public ReadWriteUserDetails(){

    };

    public ReadWriteUserDetails(String textDob, String textGender, String textMobile){
        this.doB = textDob;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
