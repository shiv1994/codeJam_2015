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
import com.example.shiva.ttplaces.R;
import java.io.InputStream;
import java.net.URL;

public class ImageFragment extends Fragment {

    public Bitmap bp=null;
    public ImageView iv=null;
    String url=null;
    Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        bundle=getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }

		View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        iv = (ImageView)rootView.findViewById(R.id.tourImage);

        if(url == null || url.trim().equals("")) {
            System.out.println("Url is invalid");
        }
        else if(bp == null) {

            (new LoadImage()).execute(url);
        }
        else{
            iv.setImageBitmap(bp);
        }
        return rootView;
    }

    class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(Bitmap image) {
            bp = image;
            if (bp != null) {
                iv.setImageBitmap(bp);
            }
            else {
                System.out.println("error acquiring image");
            }
        }
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
}


