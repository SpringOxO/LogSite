package com.example.logsite.adapter.tagadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logsite.R;
import com.example.logsite.adapter.noteadapter.NoteAdapter;
import com.example.logsite.database.Note;
import com.google.android.material.chip.Chip;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter{
    List<String> tags;
    Context mContext;
    public TagAdapter(Context context, List<String> tags){
        mContext = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chip_tag, parent, false);
        TagAdapter.TagViewHolder holder = new TagAdapter.TagViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String tag = tags.get(position);
        if (holder instanceof TagAdapter.TagViewHolder){
            ((TagAdapter.TagViewHolder) holder).bind(tag);
        }
    }

    @Override
    public int getItemCount() {
        if (tags != null){
            return tags.size();
        }
        return 0;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder{
        Chip tag;
        public TagViewHolder(@NonNull View itemView){
            super(itemView);
            tag = itemView.findViewById(R.id.tag);
        }

        public void bind (String tag){
            this.tag.setText(tag);
            this.tag.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    delTagByPosition(position);
                }
            });
        }

    }

    public void delTagByPosition (int position){
        tags.remove(position);
        notifyItemRemoved(position);
    }
}
