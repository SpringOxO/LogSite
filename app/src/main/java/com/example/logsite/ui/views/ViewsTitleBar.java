package com.example.logsite.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.logsite.R;

public class ViewsTitleBar extends RelativeLayout {
    ViewsTitleBar.OnTitleBarClickListener listener;
    ImageView imViewMode;

    private static final String TAG = "NotesTitleBar";
    public ViewsTitleBar (Context context){
        super(context);
    }

    public ViewsTitleBar(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.views_title_bar, this);

//        Toast.makeText(context, "ye", Toast.LENGTH_SHORT).show();
        imViewMode = findViewById(R.id.views_title_bar_view_mode);


        imViewMode.setImageResource(R.drawable.logo_graph);

        imViewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onViewModeClick();
                }
            }
        });

//        sv = findViewById(R.id.notes_title_bar_search_view);
    }

    public void switchViewModeIcon(int mode){
        if (mode == 0){
            imViewMode.setImageResource(R.drawable.logo_graph);
        }
        else {
            imViewMode.setImageResource(R.drawable.logo_tree);
        }
    }


    public void setListener (ViewsTitleBar.OnTitleBarClickListener listener){
        this.listener = listener;
    }

    public interface OnTitleBarClickListener {
        public void onViewModeClick();
    }
}
