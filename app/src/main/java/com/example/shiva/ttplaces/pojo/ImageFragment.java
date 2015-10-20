//this activity is used to load and display image contents for the interactive tour activity

package com.example.shiva.ttplaces.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.shiva.ttplaces.R;
import java.io.InputStream;
import java.net.URL;

public class ImageFragment extends Fragment {

    public Bitmap bp=null;
    public ImageView iv=null;
    public String url=null; ////link to store url in database
    public Bundle bundle; //bundle containing the url loaded by the tour activity
    public ProgressBar progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        //gets contents of bundle
        bundle=getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }

		View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        progressBar =(ProgressBar)rootView.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        iv = (ImageView)rootView.findViewById(R.id.tourImage);

        //if the link is empty, print error

        if(url == null || url.trim().equals("")) {
            System.out.println("Url is invalid");
        }
        else if(bp == null) {

            (new LoadImage()).execute(url); //loads the image in url
        }
        else{
            iv.setImageBitmap(bp); //sets the image to load on fragment

        }

        return rootView;
    }

    class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() { //displays progress bar while content is being loaded
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Bitmap image) { //displays the image content in tour activity after content has been loaded
            bp = image;
            if (bp != null) {
                iv.setImageBitmap(bp);
            }
            else {
                System.out.println("error acquiring image");
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
        protected Bitmap doInBackground(String... args) {//loads up the image content in tour activity after content has been loaded
            Bitmap bp=null;
            try {
                bp = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent()); //gets the image from the url

            }

            //catches error
            catch (Exception e) {
                e.printStackTrace();
            }
            return bp; //returns the content to display in the image fragment
        }
    }

}


