package com.example.logsite.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.logsite.R;
import com.example.logsite.database.Note;

public class ViewNode extends RelativeLayout {
    Context mContext;
    Note note;
    TextView tvNode;
    public ViewNode (Context context){
        this(context, null);
    }

    public ViewNode(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_node, this, true);
        tvNode=(TextView) findViewById(R.id.view_node_title);
    }

    public void setTitle(String title){
        tvNode.setText(title);
    }
}
