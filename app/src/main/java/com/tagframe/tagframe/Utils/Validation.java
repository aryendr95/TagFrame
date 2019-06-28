package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.tagframe.tagframe.Models.FrameList_Model;

import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * Created by Reetu Wadhawan on 8/10/2015.
 */
public class Validation {

    // Error Messages
    public static final String REQUIRED_MSG = "required";
    public static final String PASSWORD_MISMATCH = "password mismatch";

    public static final String EMAIL_MSG = "invalid email";
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+"
            + "(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg,
                                  boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(Html.fromHtml("<font color='white'>" + errMsg + "</font>"));
            return false;
        }
        ;

        return true;
    }


    public static boolean hasText(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(String str) {
        if (str.length() < 6) {
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(EditText editText) {

        String str = editText.getText().toString();

        if (str.length() < 6) {
            editText.setError(Html.fromHtml("<font color='white'>" + "Too short" + "</font>"));
            return false;
        }
        return true;
    }


    // return true if the input field is valid, based on the parameter passed
    public static boolean isValidEmail(String str) {


        // pattern doesn't match so returning false
        if (Pattern.matches(EMAIL_REGEX, str)) {

            return false;
        }
        ;

        return true;
    }

    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(Html.fromHtml("<font color='white'>" + REQUIRED_MSG + "</font>"));
            return false;
        }

        return true;
    }

    public static boolean doPasswordMatch(EditText password, EditText confirmPassword) {

        String text1 = password.getText().toString().trim();
        String text2 = confirmPassword.getText().toString().trim();

        // length 0 means there is no text
        if (TextUtils.equals(text1, text2)) {
            return true;
        }
        confirmPassword.setError(Html.fromHtml("<font color='white'>" + "PasswordMismatch" + "</font>"));
        return false;
    }

    public static boolean isAgreementChecked(int status, Context context) {

        if (status == 0) {

           /* CommonUi.showAlertDialog(context,context.getString(R.string.title),
                    context.getString(R.string.message),true,null);*/

            return false;
        }

        return true;
    }

    public static boolean hasFrameInformation(ArrayList<FrameList_Model> framedata_map) {
        for (int i = 0; i < framedata_map.size(); i++) {
            if (framedata_map.get(i).getEndtime() == 0 || framedata_map.get(i).getName().isEmpty())
                return false;
        }
        return true;
    }
}
