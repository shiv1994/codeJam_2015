package com.example.shiva.ttplaces;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {
    EditText userName,pass,email;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Parse.initialize(this, "4SkJs9vZM7ev0Hpj02ZaC4fnQ6Sy6yyQEkJdnwsK", "ZmvXXAkbYBCdrzP8iAx5nqUUvuUIPHwgOmXhIbyC");
        checkUser();
        userName = (EditText)findViewById(R.id.username);
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
        ParseUser user = new ParseUser();
        user.setUsername(userName.getText().toString());
        user.setPassword(pass.getText().toString());
        user.setEmail(email.getText().toString());

        // other fields can be set just like with ParseObject
        //user.put("phone", "650-253-0000");
        if(!userName.getText().toString().equals("") || !email.getText().toString().equals("") || !pass.getText().toString().equals(""))
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Toast toast = Toast.makeText(getApplicationContext(),"User Account Created Successfully!", Toast.LENGTH_SHORT);
                    toast.show();
                    login(userName.getText().toString(),pass.getText().toString());

                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                    userName.setText("");
                    email.setText("");
                    pass.setText("");
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    public void checkUser(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            runMainActivity();
        }
        else {
            //return;
        }
    }

    public void login(String name, String password){
        ParseUser.logInInBackground(name, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT);
                    runMainActivity();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void runMainActivity(){
        Intent i = new Intent(this , HomeActivity.class);
        startActivity(i);
        this.finish();
    }


}
