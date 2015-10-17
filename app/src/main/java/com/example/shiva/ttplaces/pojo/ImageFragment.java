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
import com.example.shiva.ttplaces.TourActivity;
import java.io.InputStream;
import java.net.URL;

//  Static variable used to refer to getting the content from main activity
public class ImageFragment extends Fragment {

    public Bitmap bp=null;
    public ImageView iv=null;
    String url="";
    Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        bundle=getArguments();
        url=bundle.getString("url");

		View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        iv = (ImageView)rootView.findViewById(R.id.tourImage);

        if(bp == null) {
            //url = "http://www.personal.psu.edu/sdh5174/Mario_png.png";
            new LoadImage() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                protected void onPostExecute(Bitmap image) {
                    bp = image;
                    if (bp != null) {
                        iv.setImageBitmap(bp);

                    } else {

                        System.out.println("error acquiring image");
                    }
                }
            }.execute(url);
        }else{
            iv.setImageBitmap(bp);
        }

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
