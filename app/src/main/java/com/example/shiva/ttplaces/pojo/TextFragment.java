package com.example.shiva.ttplaces.pojo;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shiva.ttplaces.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class TextFragment extends Fragment {

	String url=null,text;
	Bundle bundle;
    View rootView;
    TextView txt;
    ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        bundle=getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }

		rootView = inflater.inflate(R.layout.fragment_text, container, false);
		txt=(TextView)rootView.findViewById(R.id.Content);
        txt.setTextColor(Color.parseColor("#000000"));

        if(url == null || url.trim().equals("")) {
            text="Content Currently Unavailable";
            txt.setText(text);
            System.out.println("Url is invalid");
        }
        else {//Extract text information from url to text file and store in string text   <----------------------------
            (new LoadText()).execute(url);
        }
		return rootView;
	}

    class LoadText extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Recreating text fragments from history");
        }

        protected void onPostExecute(String text) {
            updateUI(text);
        }

        protected String doInBackground(String... args) {
            StringBuilder content = new StringBuilder(5000);

            URL url = null;
            try {
                url = new URL("http://mas-health.com/ttplaces/zoology.txt");

                // Get the response
                BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()));

                String s = "";
                while ((s = buffer.readLine()) != null) {
                    content.append(s).append("\n");
                }

                buffer.close();
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            return content.toString();
        }
    }

    public void updateUI(String text){
        txt.setText(text);
        dismissProgressDialog();
    }
    public void showProgressDialog(String message){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }

}
