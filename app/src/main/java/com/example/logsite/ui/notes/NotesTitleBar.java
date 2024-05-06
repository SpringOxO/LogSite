package com.example.logsite.ui.notes;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.example.logsite.R;

public class NotesTitleBar extends RelativeLayout {
    OnTitleBarClickListener listener;
    SearchView sv;
    ImageView imSearchMode, imOptions;

    private static final String TAG = "NotesTitleBar";
    public NotesTitleBar (Context context){
        super(context);
    }

    public NotesTitleBar(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.notes_title_bar, this);

//        Toast.makeText(context, "ye", Toast.LENGTH_SHORT).show();
        imSearchMode = findViewById(R.id.notes_title_bar_search_mode);
        imOptions = findViewById(R.id.notes_title_bar_options);
        sv = findViewById(R.id.notes_title_bar_search_view);


        imSearchMode.setImageResource(R.drawable.logo_search);
        imOptions.setImageResource(R.drawable.logo_4_points);

        imSearchMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onSearchModeClick();
                }
            }
        });
//        Log.d(TAG, "NotesTitleBar: 1");
        imOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onOptionsClick();
                }
            }
        });

//        sv = findViewById(R.id.notes_title_bar_search_view);
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


    public void setListener (OnTitleBarClickListener listener){
        this.listener = listener;
    }

    public interface OnTitleBarClickListener {
        public void onSearchModeClick();
        public void onOptionsClick();
    }


}
