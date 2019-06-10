package com.smartpocket.musicwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.smartpocket.musicwidget.service.MusicService;

public class MusicWidget extends AppWidgetProvider {

	private static final String TAG = "Music Widget";
	public static final String ACTION_PLAY_PAUSE = "com.smartpocket.musicwidget.play_pause";
	public static final String ACTION_STOP = "com.smartpocket.musicwidget.stop";

	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{	
		associateIntents(context);
		Log.d(TAG, "Widget's onUpdate()");
	}
	
	public static RemoteViews getRemoteViews(Context context){
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

		// For Play/Pause button
		PendingIntent pendingIntentStart = getPendingIntent(context, MusicWidget.ACTION_PLAY_PAUSE);
		remoteViews.setOnClickPendingIntent(R.id.button_play_pause, pendingIntentStart);

		// For Stop button
		PendingIntent pendingIntentStop = getPendingIntent(context, MusicWidget.ACTION_STOP);
		remoteViews.setOnClickPendingIntent(R.id.button_stop,pendingIntentStop);



		return remoteViews;
	}

	public static PendingIntent getPendingIntent(Context context, String action) {
		Intent intent = new Intent(context, MusicWidget.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	private void associateIntents(Context context) {

		try {
			RemoteViews remoteViews = getRemoteViews(context);
			
			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(context, MusicWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, remoteViews);
		} 
		catch (Exception e) 
		{}
	}
	
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) 
	{
		super.onDeleted(context, appWidgetIds);
		Intent oService = new Intent(context, MusicService.class);
		context.stopService(oService);
		Log.d(TAG, "Deleting widget");
	}


	@Override
	public void onReceive(Context context, Intent intent) 
	{
		final String action = intent.getAction();
		Log.d(TAG, "Widget received action: " + action);
		
		if ((action.equals(ACTION_PLAY_PAUSE)
				|| action.equals(ACTION_STOP)
		        ))
		{
			Intent serviceIntent = new Intent(context, MusicService.class);
			serviceIntent.setAction(action);
			context.startService(serviceIntent);
		} 
		else
		{
			super.onReceive(context, intent);
		}
	}
}
