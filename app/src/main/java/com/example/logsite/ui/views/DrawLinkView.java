package com.example.logsite.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class DrawLinkView extends View {

    int beginX;
    int beginY;
    int endX;
    int endY;
    public DrawLinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DrawLinkView (Context context, int beginX, int beginY, int endX, int endY){
        super(context);
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Toast.makeText(getContext(), "" + beginX + " " + beginY, Toast.LENGTH_SHORT).show();
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 抗锯齿效果,显得绘图平滑
        paint.setColor(Color.BLACK); // 设置画笔颜色
        paint.setStrokeWidth(4.0f);// 设置笔触宽度
        paint.setStyle(Paint.Style.STROKE);// 设置画笔的填充类型(完全填充)
        paint.setTextSize(30);// 字体
        Path mPath = new Path();
        mPath.reset();
        mPath.moveTo(beginX, beginY);
        mPath.cubicTo(beginX + 80, beginY + 80, endX -80, endY - 80, endX, endY);
        mPath.moveTo(endX, endY);
        mPath.lineTo(endX + 15, endY - 15);
        mPath.moveTo(endX, endY);
        mPath.lineTo(endX - 15, endY - 15);
        canvas.drawPath(mPath, paint);
    }
}
