package com.example.logsite.adapter.noteadapter;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class NoteTouchListener implements RecyclerView.OnItemTouchListener{

    private final OnItemClickListener onItemClickListener;
    private GestureDetectorCompat gestureDetector;


    public NoteTouchListener(final RecyclerView recyclerView, NoteTouchListener.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                onItemClickListener.onItemLongClick(recyclerView, child,
                        recyclerView.getChildAdapterPosition(child), recyclerView.getChildLayoutPosition(child));
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && onItemClickListener != null && gestureDetector.onTouchEvent(e)){
            int position = rv.getChildAdapterPosition(child);
            int id = rv.getChildLayoutPosition(child);
            onItemClickListener.onItemClick(rv, child, position, id);
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, View itemView, int position, long id);
        void onItemLongClick(RecyclerView recyclerView, View itemView, int position, long id);
    }


}
