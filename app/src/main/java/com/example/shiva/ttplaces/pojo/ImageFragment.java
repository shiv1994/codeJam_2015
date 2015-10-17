package com.example.shiva.ttplaces.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shiva.ttplaces.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageFragment extends Fragment {

    public Bitmap bp=null;
    public ImageView iv=null;
    String url="";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        iv = (ImageView)rootView.findViewById(R.id.tourImage);
        url="http://www.personal.psu.edu/sdh5174/Mario_png.png";

        new LoadImage(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            protected void onPostExecute(Bitmap image) {

                if(image != null){
                    iv.setImageBitmap(image);

                }else{

                    System.out.println("error acquiring image");
                }
            }
        }.execute(url);
		return rootView;
	}
}

class LoadImage extends AsyncTask<String, String, Bitmap> {

    protected Bitmap doInBackground(String... args) {
        Bitmap bp=null;
        try {
            bp = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bp;
    }
}
