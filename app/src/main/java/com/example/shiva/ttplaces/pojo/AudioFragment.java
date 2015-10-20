//this activity is used to load and display audio contents for the interactive tour activity

package com.example.shiva.ttplaces.pojo;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.shiva.ttplaces.R;

import java.io.IOException;

public class AudioFragment extends Fragment {

	String url=null;
	Bundle bundle;
	//begin
	public ImageButton btn;
	public boolean isActive;
	public MediaPlayer mediaPlayer;
	public  boolean initialState = true;
	public TextView txt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		bundle=getArguments();
		if(bundle!=null) {
			url = bundle.getString("url");
		}
		View rootView = inflater.inflate(R.layout.fragment_audio, container, false);

		txt=(TextView)rootView.findViewById(R.id.txt);
		txt.setText("CLICK TO PLAY");
		txt.setTextColor(Color.parseColor("#000000"));

		btn = (ImageButton) rootView.findViewById(R.id.button1);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		btn.setOnClickListener(audioState);

		return rootView;
	}

	private View.OnClickListener audioState = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!isActive) {

				btn.setBackgroundResource(R.drawable.button_pause);
				if (initialState)
					new Player().execute(url);

				else {
					if (!mediaPlayer.isPlaying()){
						try {
							mediaPlayer.prepareAsync();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						mediaPlayer.start();
					}
				}
				isActive = true;
				txt.setText("CLICK TO PAUSE");
				btn.setBackgroundResource(R.drawable.pausebutton);
			}
			else {
				txt.setText("CLICK TO PLAY");
				btn.setBackgroundResource(R.drawable.playbutton);
				if (mediaPlayer.isPlaying())
					mediaPlayer.pause();
				isActive = false;
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
						initialState = true;
						isActive =false;
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
			catch (SecurityException  | IllegalStateException | IOException e) {
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

			txt.setText("CLICK TO PAUSE");
			btn.setBackgroundResource(R.drawable.pause);

			initialState = false;
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
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
			txt.setText("CLICK TO PLAY");
			btn.setBackgroundResource(R.drawable.play);
		}
	}
}