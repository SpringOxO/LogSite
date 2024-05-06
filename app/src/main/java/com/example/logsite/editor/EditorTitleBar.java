package com.example.logsite.editor;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.logsite.R;

public class EditorTitleBar extends RelativeLayout {
    private OnTitleBarClickListener listener;
    private LinearLayout li_back, li_save;
    private ImageView iv_back, iv_save;
    public EditorTitleBar (Context context){
        super(context);
    }

    public EditorTitleBar (Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.editor_title_bar, this);

        li_back = findViewById(R.id.li_back);
        li_save = findViewById(R.id.li_save);

        li_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onBackClick();
                }
            }
        });

        li_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onSaveClick();
                }
            }
        });

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.editor_title_bar);

        iv_back = findViewById(R.id.li_back_image);
        iv_save = findViewById(R.id.li_save_image);

        iv_back.setImageDrawable(typedArray.getDrawable(R.styleable.editor_title_bar_imBack));
        iv_save.setImageDrawable(typedArray.getDrawable(R.styleable.editor_title_bar_imSave));
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        this.listener = listener;
    }

    public interface OnTitleBarClickListener {
        public void onBackClick();
        public void onSaveClick();
    }


}
