package com.example.shiva.ttplaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

//This class is run on the first execution of the application. It presents the user a login page where they input their email(while will be used as the username)
//and a desired password.
//Upon pressing the register button, the user will be logged in automatically.
//

public class LoginRegisterActivity extends AppCompatActivity {
    EditText pass,email;
    String userName;
    private SharedPreferences sharedPreferences;
    private static final String USEREMAIL="userEmail";
    ProgressDialog progressDialog;
    private static final String sharedPrefExistKey ="sharedPrefExistKey";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        //This function check to see whether a user has already logged in.
        //If so, the user will be taken to the home activity instead of the login page.
        checkUser();
        sharedPreferences = getSharedPreferences(USEREMAIL, Context.MODE_PRIVATE);
        String emailUser = sharedPreferences.getString(USEREMAIL,"");

        pass = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email_address);
        if(!emailUser.equals("")){
            email.setText(emailUser);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to add a user to the system.
    public void addUser(View view){

        if(!email.getText().toString().equals("") && email.getText().toString().contains("@")){
            if(!pass.getText().toString().equals("")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USEREMAIL, email.getText().toString());
                editor.apply();

                ParseUser user = new ParseUser();
                extractUserName();
                user.setUsername(userName);
                user.setPassword(pass.getText().toString());
                user.setEmail(email.getText().toString());
                //A question also has to be asked to determine whether a user is local or foreign.
                //If no, default would be Trinidad and Tobago
                showProgressDialog("Creating Account ...");
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            dismissProgressDialog();
                            Toast toast = Toast.makeText(getApplicationContext(), "User Account Created Successfully!", Toast.LENGTH_SHORT);
                            toast.show();
                            //Once a user account has been created successfully, they are logged automatically.
                            login(userName,pass.getText().toString());
                        } else {
                            dismissProgressDialog();
                            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                            toast.show();
                            email.setText("");
                            pass.setText("");
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                        }
                    }
                });
            }
            //If fields are not filled out, we display an error message.
            else{
                Toast toast = Toast.makeText(getApplicationContext(), "Please Ensure all fields are filled out.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Not A Valid Email Address", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void extractUserName(){
        if(!email.getText().toString().equals("")) {
            int pos = email.getText().toString().lastIndexOf("@");
            userName = email.getText().toString().substring(0, pos - 1);
        }
    }

    public void checkUser(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            runMainActivity();
        }
        else {
            return;
        }
    }

    //Method overloading in the following functions and the first login function is used via a user pressing the login button
    //after entering his/her credentials.
    //The second function is used within the registration context and will be run automatically upon pressing the register button.

    public void login(View view){
        if(!email.getText().toString().equals("") && !pass.getText().toString().equals("")){
            extractUserName();
            showProgressDialog("Signing In ...");
            ParseUser.logInInBackground(userName, pass.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        dismissProgressDialog();
                        Toast toast = Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT);
                        toast.show();
                        runMainActivity();
                    } else {
                        dismissProgressDialog();
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"Required fields are missing (Username & Password)", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void login(String userName, String password){
        showProgressDialog("Signing In ...");
            ParseUser.logInInBackground(userName, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        dismissProgressDialog();
                        Toast toast = Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT);
                        toast.show();
                        runMainActivity();
                    } else {
                        dismissProgressDialog();
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
    }

    //This function runs the main activity in addition to the preferences page once the preferenced have not been filled out.
    public void runMainActivity(){
        Intent i;
        SharedPreferences preferences = getSharedPreferences("userAnswers", 0);
        boolean value = preferences.getBoolean(sharedPrefExistKey,false);
        if(value) {
            i = new Intent(this, HomeActivity.class);
        }
        else {
            i = new Intent(this, SuggestionActivity.class);
        }
        startActivity(i);
        this.finish();

    }

    public void showProgressDialog(String message){
        progressDialog = new ProgressDialog(LoginRegisterActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }


}
