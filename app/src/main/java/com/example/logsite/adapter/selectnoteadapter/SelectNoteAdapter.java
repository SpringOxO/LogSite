package com.example.logsite.adapter.selectnoteadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logsite.R;
import com.example.logsite.adapter.noteadapter.NoteAdapter;
import com.example.logsite.database.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectNoteAdapter extends RecyclerView.Adapter implements Filterable {
    private Context mContext;
    private List<Note> fltList;
    private List<Note> srcList;
    private SelectNoteAdapter.NoteFilter filter;
    private int filterMode = 0;
    private static final String TAG = "SelectNoteAdapter";
    public SelectNoteAdapter(Context context, List<Note> notes){
        mContext = context;
        fltList = notes;
        srcList = fltList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_listed_in_selector, parent, false);
        SelectNoteAdapter.NoteViewHolder holder = new SelectNoteAdapter.NoteViewHolder(view);
        Log.d(TAG, "onCreateViewHolder: aa?");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (position == getItemCount() - 1){
//            holder.itemView.setVisibility(View.INVISIBLE);
//        }
//        else {
        Note note = fltList.get(position);
        if (holder instanceof SelectNoteAdapter.NoteViewHolder){
//            Toast.makeText(mContext, Integer.toString(position), Toast.LENGTH_SHORT).show();
            ((SelectNoteAdapter.NoteViewHolder) holder).bind(note);
        }
//        }

    }

    @Override
    public int getItemCount() {
        if(fltList != null){
            return fltList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new SelectNoteAdapter.NoteFilter();
        }
        return filter;
    }

    /**
     * 设定筛选模式，0为按title筛选，1为按tag筛选
     * @param mode
     */
    public void setFilterMode (int mode){
        filterMode = mode;
    }

    //这个函数用于删除筛选之后的list里的数据，这样才能使得在应用filter的情况下，notifyitemremoved正常运作
    public void removeFilteredData (Note note){
        if (fltList != srcList){
            fltList.remove(note);
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView titleView;
        TextView tagView;
        TextView timeView;
        Note note;

        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            titleView = itemView.findViewById(R.id.note_listed_title);
            tagView = itemView.findViewById(R.id.note_listed_tags);
            timeView = itemView.findViewById(R.id.note_listed_time);
        }

        @SuppressLint("SetTextI18n")
        public void bind (Note note){
            this.note = note;
            titleView.setText(note.getTitle());
            tagView.setText(note.getTag());
            timeView.setText(note.getTime());
            Log.d(TAG, "bind: " + note.getTitle());
        }

        public Note getNote (){
            return note;
        }
    }

    class NoteFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String str = charSequence.toString();
            if (str.isEmpty()){
                fltList = srcList;
            }
            else {
                List<Note> resultList = new ArrayList<>();
                if (filterMode == 0){
                    //filter by title
                    for (Note note : srcList){
                        if (note.getTitle().contains(str)){
                            resultList.add(note);
                        }
                    }
                }
                else {
                    // filter by tags
                    String[] tags = str.split("\\|");
                    for (int i = 0; i < tags.length; i++){
                        tags[i] = tags[i].trim();
                    }
                    for (Note note : srcList){
                        boolean isSelected = true;
                        List<String> curTags = Arrays.asList(note.getTag().split("\\|"));
                        curTags.replaceAll(String::trim);
                        for (String tag : tags){
                            if (!curTags.contains(tag)){
                                isSelected = false;
                                break;
                            }
                        }
                        if (isSelected) {
                            resultList.add(note);
                        }
                    }

                }
                fltList = resultList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = fltList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fltList = (ArrayList<Note>)filterResults.values;
            notifyDataSetChanged();
        }
    }
}
