package com.example.shiva.ttplaces;

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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginRegisterActivity extends AppCompatActivity {
    EditText pass,email;
    String userName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        //Parse.initialize(this, "4SkJs9vZM7ev0Hpj02ZaC4fnQ6Sy6yyQEkJdnwsK", "ZmvXXAkbYBCdrzP8iAx5nqUUvuUIPHwgOmXhIbyC");
        checkUser();
        pass = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email_address);
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

    public void addUser(View view){

        if(!email.getText().toString().equals("") && email.getText().toString().contains("@")){
            if(!pass.getText().toString().equals("")) {

                ParseUser user = new ParseUser();
                extractUserName();
                user.setUsername(userName);
                user.setPassword(pass.getText().toString());
                user.setEmail(email.getText().toString());
                //A question also has to be asked to determine whether a user is local or foreign.
                //If no, default would be Trinidad and Tobago
                user.put("country", "Trinidad and Tobago");
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            Toast toast = Toast.makeText(getApplicationContext(), "User Account Created Successfully!", Toast.LENGTH_SHORT);
                            toast.show();
                            login(userName,pass.getText().toString());
                        } else {
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

    public void login(View view){
        if(!email.getText().toString().equals("") && !pass.getText().toString().equals("")){
            extractUserName();
            ParseUser.logInInBackground(userName, pass.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT);
                        toast.show();
                        runMainActivity();
                    } else {
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
            ParseUser.logInInBackground(userName, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT);
                        toast.show();
                        runMainActivity();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
    }

    public void runMainActivity(){

        Intent i;
        SharedPreferences preferences = getSharedPreferences("userAnswers", 0);
        boolean value = preferences.contains("ansKey1");
        if(value) {
            i = new Intent(this, HomeActivity.class);
        }
        else {
            i = new Intent(this, SuggestionActivity.class);
        }
        startActivity(i);
        this.finish();

    }


}
