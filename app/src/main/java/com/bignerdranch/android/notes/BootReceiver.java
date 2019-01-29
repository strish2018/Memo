package com.bignerdranch.android.notes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import com.bignerdranch.android.notes.database.NoteContract;
import com.bignerdranch.android.notes.database.NoteDBHelper;

public class BootReceiver extends BroadcastReceiver {

    private SQLiteDatabase mDatabase;
    private NotificationHelper mNotificationHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            NoteDBHelper dbHelper = new NoteDBHelper(context);
            mDatabase = dbHelper.getWritableDatabase();
            mNotificationHelper = new NotificationHelper(context);
            showNotifications();
        }
    }

    private Cursor getAllItems(){
        return mDatabase.query(NoteContract.NoteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
//                NoteContract.NoteEntry.COLUMN_TIMESTAMP + " DESC"
                null
        );
    }

    public void showNotifications(){
        Cursor cr = getAllItems();
        for (int i = 0; i < cr.getCount(); i++){
            if(cr.moveToPosition(i)){
                String message = cr.getString(cr.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME));
                int id = (int)cr.getLong(cr.getColumnIndex(NoteContract.NoteEntry._ID));
                sendNotification("Memo", message, id);
            }
        }
    }

    private void sendNotification(String title, String message, int id){

        NotificationCompat.Builder nb = mNotificationHelper.getChannelNotification(title, message);
        nb.setOngoing(true);
        nb.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mNotificationHelper.getManager().notify(id, nb.build());
    }
}
