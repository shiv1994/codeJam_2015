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
            String info=null;



            return info;
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
