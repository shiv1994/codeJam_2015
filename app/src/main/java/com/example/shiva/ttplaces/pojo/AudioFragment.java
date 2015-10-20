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

	String url=null; //link to store url in database
	Bundle bundle; //bundle containing the url loaded by the tour activity
	public ImageButton btn; //Audio button to play/pause audio
	public boolean isActive; //Audio is active
	public MediaPlayer mediaPlayer; //Media player
	public  boolean initialState = true;
	public TextView txt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		//gets content of bundle
		bundle=getArguments();
		if(bundle!=null) {
			url = bundle.getString("url");
		}
		View rootView = inflater.inflate(R.layout.fragment_audio, container, false);

		//Text view stating play or pause state
		txt=(TextView)rootView.findViewById(R.id.txt);
		txt.setText("CLICK TO PLAY");
		txt.setTextColor(Color.parseColor("#000000"));

		btn = (ImageButton) rootView.findViewById(R.id.button1);// Play/Pause button
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //Stream type of audio
		btn.setOnClickListener(audioState);//sets listener for play/pause

		return rootView;
	}


	private View.OnClickListener audioState = new View.OnClickListener() {

		@Override
		//responds to click play/pause button
		public void onClick(View v) {

			if (!isActive) {
				//If audio is not active change state
				btn.setBackgroundResource(R.drawable.pausebutton);
				if (initialState)//Creates a new player for audio if audio play hasn't started
					new Player().execute(url);

				else {
					//If audio is paused get and play audio
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
				btn.setBackgroundResource(R.drawable.pausebutton); //sets pause image
			}
			else {
				txt.setText("CLICK TO PLAY");
				btn.setBackgroundResource(R.drawable.playbutton);//sets play image
				if (mediaPlayer.isPlaying())
					mediaPlayer.pause();
				isActive = false;
			}
		}
	};

	class Player extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog progress;

		@Override
		//Gets the media player content and sets it up for playing
		protected Boolean doInBackground(String... params) {
			Boolean prepared;
			try {
				mediaPlayer.setDataSource(params[0]); //Data resource of audio

				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						initialState = true;
						isActive =false;
						btn.setBackgroundResource(R.drawable.playbutton);
						mediaPlayer.stop();
						mediaPlayer.reset();
					}
				});
				mediaPlayer.prepare();
				prepared = true;

			}
			//Handles errors thrown
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
		//removes buffering item and starts media play
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (progress.isShowing()) {
				progress.cancel();
			}
			Log.d("Prepared", "//" + result);
			mediaPlayer.start();

			txt.setText("CLICK TO PAUSE"); //pauses
			btn.setBackgroundResource(R.drawable.pausebutton);

			initialState = false;
		}

		public Player() {
			progress = new ProgressDialog(getActivity());
		}

		@Override
		//Displays progress dialog
		protected void onPreExecute() {
			super.onPreExecute();
			this.progress.setMessage("Buffering...");
			this.progress.show();
		}
	}
	@Override
	//Hnadles pausing the audio
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