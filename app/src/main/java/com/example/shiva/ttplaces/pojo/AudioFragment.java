package com.example.shiva.ttplaces.pojo;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shiva.ttplaces.R;

import java.io.IOException;

public class AudioFragment extends Fragment {

	String url=null;
	Bundle bundle;
	//begin
	private Button btn;
	private boolean playPause;
	private MediaPlayer mediaPlayer;
	private  boolean intialStage = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        bundle=getArguments();
        if(bundle!=null) {
            url = bundle.getString("url");
        }
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);

        btn = (Button) rootView.findViewById(R.id.button1);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        btn.setOnClickListener(pausePlay);

        return rootView;
    }

	private View.OnClickListener pausePlay = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!playPause) {
				btn.setBackgroundResource(R.drawable.button_pause);
				if (intialStage)
					new Player().execute(url);
				else {
					if (!mediaPlayer.isPlaying()){
						try {
							mediaPlayer.prepare();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						mediaPlayer.start();
					}
				}
				playPause = true;
			}
			else {
				btn.setBackgroundResource(R.drawable.button_play);
				if (mediaPlayer.isPlaying())
					mediaPlayer.pause();
				playPause = false;
			}
		}
	};

	class Player extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog progress;

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean prepared;
			try {

				mediaPlayer.setDataSource(params[0]);

				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						intialStage = true;
						playPause=false;
						btn.setBackgroundResource(R.drawable.button_play);
						mediaPlayer.stop();
						mediaPlayer.reset();
					}
				});
				mediaPlayer.prepare();
				prepared = true;

			}
            catch (IllegalArgumentException e) {
				Log.d("IllegalArgument", e.getMessage());
				prepared = false;
				e.printStackTrace();
			}
            catch (SecurityException e) {
				prepared = false;
				e.printStackTrace();
			}
            catch (IllegalStateException e) {
				prepared = false;
				e.printStackTrace();
			}
            catch (IOException e) {
				prepared = false;
				e.printStackTrace();
			}
			return prepared;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (progress.isShowing()) {
				progress.cancel();
			}
			Log.d("Prepared", "//" + result);
			mediaPlayer.start();

			intialStage = false;
		}

		public Player() {
			progress = new ProgressDialog(getActivity());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.progress.setMessage("Buffering...");
			this.progress.show();
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.pause();

			//mediaPlayer.reset();
			//mediaPlayer.release();
			//mediaPlayer = null;
		}
	}
}
