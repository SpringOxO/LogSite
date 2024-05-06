package com.example.logsite.adapter.noteadapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteDecoration extends RecyclerView.ItemDecoration {
    public int space;
    public int leftMargin;
    public NoteDecoration (Context context, int space){
        this.space = dpToPx(space, context);
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        //计算左边间距，这句很有用噢，360dp是item的宽度
        leftMargin = (getScreenWidth(view.getContext()) - dpToPx(360, view.getContext())) / 2;

        int position = parent.getChildAdapterPosition(view);
        if (parent.getAdapter() == null) return;
        int num = parent.getAdapter().getItemCount();
        if (position < num - 1){
            outRect.bottom = space;
        }
        outRect.left = leftMargin;
    }

    private int dpToPx(int dp, Context context){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
}
