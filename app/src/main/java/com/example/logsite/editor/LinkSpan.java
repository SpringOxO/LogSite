package com.example.logsite.editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.example.logsite.database.CRUD;
import com.example.logsite.database.Note;

public class LinkSpan extends BackgroundColorSpan implements DataBindingSpan<String>{
    //这里使用全局变量完全是为了节省内存（我反正觉得一直new就很吓人），缺点是不支持动态变化（点击引用，更改标题之后，这里也是不会变的）
    //想让它变也可以，也不难，就是有点麻烦，时间又紧，暂时不想写，留个TODO在这里吧
    //TODO 更改link过去的笔记，这里的内容现在不会变，下次加载才会变。可以在editor返回时更新一下。
    String linkText;
    Context mContext;
    SpannableString spanned;
    Note note;
    private static final String TAG = "LinkSpan";
    public LinkSpan (int bgColor, String linkText, Context context){
        //继承了设定背景颜色的span，emmm，反正保证它真的是个span嘛，对吧。bgcolor传transparent就好
        super(bgColor);
        this.linkText = linkText;
        this.mContext = context;
    }
    @Override
    public CharSequence spannedText() {
        if (spanned == null){
            //内容为文档标题，设为斜体
            spanned = new SpannableString(getNote(getId()).getTitle());
            spanned.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanned.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanned;
    }

    @Override
    public String bindingData() {
        return linkText;
    }

    public long getId (){
        if (linkText != null){
            return Long.parseLong(linkText.substring(6, linkText.length() - 1));
        }
        return 0;
    }

    public Note getNote (long id){
        if (note == null){
            CRUD op = new CRUD(mContext);
            op.open();
            note =  op.getNote(id);
            note.setId(id);
            op.close();
        }
        return note;
    }
}
