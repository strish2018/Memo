package com.bignerdranch.android.notes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bignerdranch.android.notes.database.NoteContract;
import com.bignerdranch.android.notes.database.NoteDBHelper;

public class MainActivity extends AppCompatActivity implements MemoDialog.MemoDialogListener{

    private SQLiteDatabase mDatabase;
    private NotesAdapter mAdapter;
    private FloatingActionButton mFabAdd;

    private NotificationHelper mNotificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFabAdd = findViewById(R.id.fab_add);

        NoteDBHelper dbHelper = new NoteDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        mNotificationHelper = new NotificationHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotesAdapter(this, getAllItems());
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListenerInterface() {
            @Override
            public void onItemClick(long id) {
                itemClick(id);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);
        showNotifications();

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_note:
                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void addNote(){

        //MemoDialog dialog = new MemoDialog();
        MemoDialog dialog = MemoDialog.newInstance("", 0, false);
        dialog.show(getSupportFragmentManager(), "dialog");
//        ContentValues cv = new ContentValues();
//        cv.put(NoteContract.NoteEntry.COLUMN_NAME, "Test adsfdg sd dsg sdg sdgdsgsdgs gdsg dhdfahdfh hdfhdfbdfh dafh dfhadjhdfaharhfadh dafhvdhsdgth");
//
//        mDatabase.insert(NoteContract.NoteEntry.TABLE_NAME, null, cv);
//        mAdapter.swapCursor(getAllItems());
    }

    @Override
    public void applyText(String s) {
        createNote(s);
    }

    @Override
    public void updateText(String s, long id) {
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntry.COLUMN_NAME, s);

        mDatabase.update(NoteContract.NoteEntry.TABLE_NAME, cv, NoteContract.NoteEntry._ID + " = ?", new String[]{String.valueOf(id)});
        mAdapter.swapCursor(getAllItems());
        showNotifications();
    }

    void itemClick(long id) {
        String text = "";
        NoteDBHelper dbHelper = new NoteDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //specify the columns to be fetched
        String[] columns = {NoteContract.NoteEntry.COLUMN_NAME};
        //Select condition
        String selection = NoteContract.NoteEntry._ID + " = ?";
        //Arguments for selection
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(NoteContract.NoteEntry.TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            text = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME));
        }

        dbHelper.close();
        MemoDialog dialog = MemoDialog.newInstance(text, id, true);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void createNote(String s){
        ContentValues cv = new ContentValues();
        cv.put(NoteContract.NoteEntry.COLUMN_NAME, s);

        mDatabase.insert(NoteContract.NoteEntry.TABLE_NAME, null, cv);
        mAdapter.swapCursor(getAllItems());
        showNotifications();
    }

    private void removeItem(long id){
        mDatabase.delete(NoteContract.NoteEntry.TABLE_NAME,
                NoteContract.NoteEntry._ID + "=" + id, null);
        mAdapter.swapCursor(getAllItems());
        //showNotifications();
        removeNotification((int)id);
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

    private void removeNotification(int id){
        mNotificationHelper.getManager().cancel(id);
    }
}
