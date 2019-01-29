package com.bignerdranch.android.notes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.notes.database.NoteContract;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    private OnItemClickListenerInterface mListener;

    public interface OnItemClickListenerInterface{
        void onItemClick(long id);
    }

    public void setOnItemClickListener(OnItemClickListenerInterface listener){
        mListener = listener;
    }

    public NotesAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;

        public NotesViewHolder(final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        mListener.onItemClick((long)itemView.getTag());
                    }
                }
            });
        }
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.note_item, viewGroup, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder notesViewHolder, int i) {
        if(!mCursor.moveToPosition(i))
            return;

        String name = mCursor.getString(mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndex(NoteContract.NoteEntry._ID));

        notesViewHolder.nameTextView.setText(name);
        notesViewHolder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }

        mCursor = newCursor;

        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

}
