package com.example.logsite.noteselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.example.logsite.R;

public class NoteSelectorTitleBar extends RelativeLayout {
    NoteSelectorTitleBar.OnTitleBarClickListener listener;
    SearchView sv;
    ImageView imSearchMode;

    private static final String TAG = "NoteSelectorTitleBar";
    public NoteSelectorTitleBar (Context context){
        super(context);
    }

    public NoteSelectorTitleBar(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.note_selector_title_bar, this);

//        Toast.makeText(context, "ye", Toast.LENGTH_SHORT).show();
        imSearchMode = findViewById(R.id.notes_title_bar_search_mode);
        sv = findViewById(R.id.notes_title_bar_search_view);


        imSearchMode.setImageResource(R.drawable.logo_search);

        imSearchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onSearchModeClick();
                }
            }
        });
    }

    public SearchView getSearchView (){
        return sv;
    }

    public void switchSearchModeIcon(int mode){
        if (mode == 0){
            imSearchMode.setImageResource(R.drawable.logo_search);
        }
        else {
            imSearchMode.setImageResource(R.drawable.logo_tag);
        }
    }


    public void setListener (NoteSelectorTitleBar.OnTitleBarClickListener listener){
        this.listener = listener;
    }

    public interface OnTitleBarClickListener {
        public void onSearchModeClick();
    }
}
