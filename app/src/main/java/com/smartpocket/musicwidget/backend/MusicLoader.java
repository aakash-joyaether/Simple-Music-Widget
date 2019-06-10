package com.smartpocket.musicwidget.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.smartpocket.musicwidget.model.Song;

public class MusicLoader {
	private static final String TAG = "Music Loader";
	private static MusicLoader instance;
	private final Context context;
	private Cursor cur;
	private int numberOfAudios;
	public static MusicLoader getInstance(Context context){
		if (instance == null) {
			instance = new MusicLoader(context);
			instance.prepare();
		}

		return instance;
	}

	private MusicLoader(Context context) {
		this.context = context;
	}


	private void prepare() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String sortOrder = "RANDOM()";

		Log.d(TAG, "Querying media...");


		//Some audio may be explicitly marked as not being music
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DURATION
		};

		cur = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,
				selection,
				null,
				sortOrder);

		Log.d(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));

		if (cur == null) {
			// Query failed...
			Log.e(TAG, "Failed to retrieve music: cursor is null");
			return;
		}

		if (!cur.moveToFirst()) {
			Log.e(TAG, "Failed to move cursor to first row (no music found).");
			return;
		}

		Log.d(TAG, "Done querying media. MusicLoader is ready.");
	}

	public Song getRandom() {
		String title = "";
		String artist = "";
		long duration = 0;
		long id = 0;
		if (cur != null && cur.getCount() > 0) {
			this.numberOfAudios = cur.getCount();
			Log.d(TAG, "numSongs: " + this.numberOfAudios);

			int randomNum = (int) (Math.random() * this.numberOfAudios);
			if (cur.moveToPosition(randomNum)) {
				title = cur.getString(cur.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE));
				artist = cur.getString(cur.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST));
				duration = cur.getLong(cur.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION));
				id = cur.getLong(cur.getColumnIndex(android.provider.MediaStore.Audio.Media._ID));

			}
		}
		return new Song(id, title, artist, duration);
	}

	public Song getCurrent() {
		String title  = cur.getString(cur.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE));
		String artist = cur.getString(cur.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST));
		long duration = cur.getLong(  cur.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION));
		long id       = cur.getLong(  cur.getColumnIndex(android.provider.MediaStore.Audio.Media._ID));

		return new Song(id, title, artist, duration);
	}

	public void close() {
		if (cur != null){

			cur.close();
		}

		instance = null;
	}
}