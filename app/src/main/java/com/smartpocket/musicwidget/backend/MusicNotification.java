package com.smartpocket.musicwidget.backend;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.smartpocket.musicwidget.MusicWidget;
import com.smartpocket.musicwidget.R;
import com.smartpocket.musicwidget.service.MusicService;

public class MusicNotification {
	private final NotificationCompat.Builder builder;
	private Notification notification;
    private final NotificationCompat.Action playPauseAction;
	private final NotificationManager manager;
	private final int notificationID;
	
	public MusicNotification(Context context, int notificationID, String title, String artist){
		this.notificationID = notificationID;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		playPauseAction = new NotificationCompat.Action(R.drawable.ic_pause_white_36dp, null, MusicWidget.getPendingIntent(context, MusicWidget.ACTION_PLAY_PAUSE));

		Intent notificationIntent = new Intent(context, MusicService.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
    	builder = new NotificationCompat.Builder(context);
		// Show controls on lock screen even when user hides sensitive content.
		builder.setVisibility(Notification.VISIBILITY_PUBLIC);
		// Add media control buttons that invoke intents in your media service
		builder.addAction(playPauseAction)
				.addAction(R.drawable.ic_stop_white_36dp, null, MusicWidget.getPendingIntent(context, MusicWidget.ACTION_STOP))
				.setStyle(new NotificationCompat.MediaStyle());

        builder.setPriority(NotificationCompat.PRIORITY_MAX);

		builder.setContentIntent(pendingIntent)
    	            .setSmallIcon(R.drawable.ic_stat_name)
    	            .setTicker(title)
    	            .setWhen(System.currentTimeMillis())
    	            .setContentTitle(title)
    	            .setContentText(artist);



    	notification = builder.build();
	}
	
	
	public Notification getNotification(){
		return notification;
	}
	
	public void update(String title, String artist, boolean isPlaying){
		builder.setContentTitle(title)
			.setContentText(artist)
			.setWhen(System.currentTimeMillis());

		if (isPlaying)
            playPauseAction.icon = R.drawable.ic_pause_white_36dp;
        else
            playPauseAction.icon = R.drawable.ic_play_arrow_white_36dp;


		notification = builder.build();
		manager.notify(notificationID, notification);
	}
}
