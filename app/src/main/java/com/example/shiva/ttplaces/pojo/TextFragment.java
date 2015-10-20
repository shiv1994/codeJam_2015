//this activity is used to load and display text contents for the interactive tour activity

package com.example.shiva.ttplaces.pojo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.shiva.ttplaces.R;


public class TextFragment extends Fragment {

	String link=null; //link to store url in database
    String text= null;
	Bundle bundle; //bundle containing the url loaded by the tour activity
    View rootView; //layout of the activity fragment
    TextView txt; //main content view
    URL url; //url for the text
    public ProgressBar progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //gets content of bundle
        bundle=getArguments();
        if(bundle!=null) {
            link = bundle.getString("url");
        }

		rootView = inflater.inflate(R.layout.fragment_text, container, false);
        progressBar =(ProgressBar)rootView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
		txt=(TextView)rootView.findViewById(R.id.Content);
        txt.setTextColor(Color.parseColor("#000000"));

        //if the link is empty, print error

        if(link == null || link.trim().equals("")) {
            text="Content Currently Unavailable";
            txt.setText(text);
            System.out.println("Url is invalid");
        }
        else { //gets the text from the link
            (new LoadText()).execute(link);
        }
		return rootView;
	}

    class LoadText extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() { //displays progress bar while content is being loaded
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(String text) { //loads up the text content in tour activity after content has been loaded
            updateUI(text);
            progressBar.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(String... args) {

            StringBuilder content = new StringBuilder(5000); //used to build the string of 5000 characters
            String error="Sorry, this text is UNAVAILABLE"; //error msg if and error occurs
            try {
                url=new URL(link); //stores the link from db in url
                // Get the response
                BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream())); //gets the text from the url

                String s=""; //empty string that will be used to get the text form the url
                while ((s = buffer.readLine()) != null) {
                    content.append(s).append("\n");  //builds the text to display in fragment
                }

                buffer.close();
            }

            //catches any erros that may occur
            catch (MalformedURLException e) {
                e.printStackTrace();
                return error; //displays the error msg
            }
            catch (IOException e) {
                e.printStackTrace();
                return error; //displays the error msg
            }

            return content.toString(); //returns the content to display in the text fragment
        }

    }

    public void updateUI(String text){ //displays the content in the text fragment
        txt.setText(text);
    }

}
