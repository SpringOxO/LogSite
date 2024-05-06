package com.example.logsite.editor;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logsite.EditorManager;
import com.example.logsite.R;
import com.example.logsite.adapter.noteadapter.NoteAdapter;
import com.example.logsite.adapter.tagadapter.TagAdapter;
import com.example.logsite.database.CRUD;
import com.example.logsite.database.Note;
import com.example.logsite.noteselector.NoteSelectorActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorActivity extends AppCompatActivity{
    private EditorTitleBar editorTitleBar;
    private TagAdapter tagAdapter;
    private List<String> tags = new ArrayList<>();
    private RecyclerView rvTags;
    private EditText etTag;
    private EditText etContent;
    private EditText etTitle;
    private Intent intent;
    private int openMode;
    private Note note;

    //添加引用时，启动选择器
    ActivityResultLauncher<Intent> selectorLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //如果正常返回，在光标处插入link文本
                if (result.getResultCode() == RESULT_OK){
                    int index = etContent.getSelectionStart();
                    Editable content = etContent.getText();
                    if (result.getData() != null){
                        long id = result.getData().getLongExtra("id", 0);
                        String linkText = "<link:" + id + ">";
                        LinkSpan linkSpan = new LinkSpan(Color.TRANSPARENT, linkText, this);
                        content.insert(index, linkSpan.spannedText());
                        content.setSpan(linkSpan, index, index + linkSpan.spannedText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        content.setSpan(new ClickableLink(id, this), index, index + linkSpan.spannedText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
    );

    //点击引用时，打开对应的note（editor）
    ActivityResultLauncher<Intent> linkLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //启动editor返回，这里没必要做任何事情
//                Toast.makeText(this, "yeah!", Toast.LENGTH_SHORT).show();
            }
    );

    //用来设置可点击的span
    class ClickableLink extends ClickableSpan{
        long id;
        Context mContext;
        public ClickableLink (long id, Context context){
            this.id = id;
            mContext = context;
        }
        @Override
        public void onClick(@NonNull View view) {
            Intent intent = new Intent(mContext, EditorActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("mode", (int)2); //mode2：只知道id
            linkLauncher.launch(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editorTitleBar = findViewById(R.id.editor_title_bar);
        etTitle = findViewById(R.id.editor_title);
        etContent = findViewById(R.id.editor_content);
        etTag = findViewById(R.id.editor_tag_input);
        //隐藏原标题栏
        getSupportActionBar().hide();
        //获取打开状态
        intent = getIntent();
        openMode = intent.getIntExtra("mode", 0);
        CharSequence exportedContent = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        if (exportedContent != null && !exportedContent.equals("")){//外界的文本
            note = new Note("", exportedContent.toString(), "", "");
            showNote();
        }
        else if (openMode == 1){ //已有的note，给出全部信息（虽然后来被迫写了mode2（只知道id），看起来有点冗余，但这个也保留着吧）
            long id = intent.getLongExtra("id", 0);
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String time = intent.getStringExtra("time");
            String tag = intent.getStringExtra("tag");
            note = new Note(id, title, content, time, tag);
            getTags();
            showNote();
        }
        else if (openMode == 2){//已有的note，只给出id（这暂时只会在link的时候出现）
            long id = intent.getLongExtra("id", 0);
            getNote(id);
            getTags();
            showNote();
        }
        else {//新的空白note
            note = new Note("", "", "", "");
        }
        //初始化tags列表
        rvTags = findViewById(R.id.editor_tags);
        tagAdapter = new TagAdapter(this, tags);
//        Toast.makeText(this, Integer.toString(tagAdapter.getItemCount()), Toast.LENGTH_SHORT).show();
        rvTags.setAdapter(tagAdapter);

        //设置标题栏listener
        editorTitleBar.setOnTitleBarClickListener(new EditorTitleBar.OnTitleBarClickListener() {
            @Override
            public void onBackClick() {
                saveNote();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onSaveClick() {
//                Toast.makeText(EditorActivity.this, "doing: save.", Toast.LENGTH_SHORT).show();
//                saveNewNote();
                saveNote();
            }
        });

        //设置新增tag栏的行为：回车就添加tag
        etTag.setImeActionLabel("Add", KeyEvent.KEYCODE_ENTER);
        etTag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && !etTag.getText().toString().equals("")){
                    String newTag = etTag.getText().toString();
                    addTag(newTag);
                    etTag.setText("");
                }
                return false;
            }
        });

        //设置content的insert长按菜单事件
        etContent.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                //这里是添加自己定义的项，如果需要去除其他按钮，要先把menu给clear一下
                inflater.inflate(R.menu.editor_content_insertion_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                //点击insert_link打开selector
                if (menuItem.getItemId() == R.id.editor_content_insert_link){
                    Intent intent = new Intent(getBaseContext(), NoteSelectorActivity.class);
                    selectorLauncher.launch(intent);
                    //这里没别的要做的了，返回值会在launcher里处理。往上翻↑↑↑
                    actionMode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        //设置content的属性！这个必须要加，否则link的点击事件不会被响应
        etContent.setMovementMethod(LinkMovementMethod.getInstance());

        //
        EditorManager.getInstance().addEditor(this);

    }

    //把新增的tag加入chip
    public void addTag (String newTag){
        tags.add(newTag);
        tagAdapter.notifyItemInserted(tagAdapter.getItemCount());
    }

    //back和home都会导致保存退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
            saveNote();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //根据id去获取note数据
    public void getNote (long id){
        CRUD op = new CRUD(this);
        op.open();
        note = op.getNote(id);
        note.setId(id);
        op.close();
    }

    //从当前note中得到tag数据存进tags里
    public void getTags (){
        String[] tags = note.getTag().split("\\|");
        for (String tag : tags){
//            Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
            if (!tag.trim().equals(""))
                this.tags.add(tag.trim());
        }
    }

    long getLinkId (String linkText){
        return Long.parseLong(linkText.substring(6, linkText.length() - 1));
    }

    //展示note
    //DONE 解析link
    public void showNote (){
        etTitle.setText(note.getTitle());

        //由于要insert，只能先set进去再拿出来，不知道有没有更自然的方法
        etContent.setText(note.getContent());
        Editable content = etContent.getText();
        Pattern linkPattern = Pattern.compile("<link:[0-9]+>");
        //解析所有的linktext
        //由于寻找正则的时候是一次性传入原字符串，所以如果一边查一边替换的话，之后替换的位置就会错掉，并且很可能越界
        //所以只能先找出所有的位置，再从后往前replace
        //蛮蠢的，这波配合不好
        Matcher linkMatcher = linkPattern.matcher(content);
        List<String> linkTexts = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        while (linkMatcher.find()){
            linkTexts.add(linkMatcher.group());
            indices.add(linkMatcher.start());
        }
        for (int i = linkTexts.size() - 1; i >= 0; i--){
            long id = getLinkId(linkTexts.get(i));
            //如果不存在就不进行解码了
            if (!checkNoteExist(id))continue;
            int index = indices.get(i);
            LinkSpan linkSpan = new LinkSpan(Color.TRANSPARENT, linkTexts.get(i), this);
            content.replace(index, index + linkTexts.get(i).length(), linkSpan.spannedText());
            content.setSpan(linkSpan, index, index + linkSpan.spannedText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(new ClickableLink(id, this), index, index + linkSpan.spannedText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        etContent.setText(content);
    }

    //将当前note作为新note添加
    public void saveNewNote (){
//        Note note = new Note(etTitle.getText().toString(), etContent.getText().toString(), getCurTime(), "test");
        openMode = 1;
        updateNote();
        CRUD op = new CRUD(this);
        op.open();
        op.addNote(note);
        op.close();
    }

    //务必保证当前note有id！将其数据更新
    public void saveExistingNote (){
        updateNote();
        CRUD op = new CRUD(this);
        op.open();
        op.updateNote(note);
        op.close();
    }

    public void saveNote (){
        if (openMode == 1 || openMode == 2){ //不管其他数据有没有，只要是已存在的（我草我现在真的觉得这个mode1的设计好多余）
            saveExistingNote();
        }
        else {
            //DONE 要不要让它把标题自动搞成文档第一行？ 做吧
            if (etTitle.getText().toString().equals("")){
                String firstPiece = etContent.getText().toString().split("\n")[0];
                if (firstPiece.length() > 20){
                    firstPiece = firstPiece.substring(0, 20);
                }
                etTitle.setText(firstPiece);
            }
            saveNewNote();
        }
    }

    public boolean checkNoteExist (long id){
        boolean ret = false;
        CRUD op = new CRUD(this);
        op.open();
        ret = op.checkNoteExist(id);
        op.close();
        return ret;
    }

    //DONE 保存linktext
    //TODO link的背景是不是可以改一下（不过也无所谓，小功能
    public void updateNote (){
        note.setTitle(etTitle.getText().toString());

        Editable content = etContent.getText();
        LinkSpan[] linkSpans = content.getSpans(0, content.length(), com.example.logsite.editor.LinkSpan.class);
//        Toast.makeText(this, Integer.toString(linkSpans.length), Toast.LENGTH_SHORT).show();
        for (LinkSpan linkSpan : linkSpans){
//            Toast.makeText(this, Integer.toString(content.getSpanStart(linkSpan)), Toast.LENGTH_SHORT).show();
            content.replace(content.getSpanStart(linkSpan), content.getSpanEnd(linkSpan), linkSpan.bindingData());
        }

        note.setContent(etContent.getText().toString());
        //更新当前时间
        note.setTime(getCurTime());

        //tags转为字符串
        String newTagString = "";
        if (tags != null && tags.size() > 0){
            for (int i = 0; i < tags.size() - 1; i++){
                newTagString += tags.get(i) + " | ";
            }
            newTagString += tags.get(tags.size() - 1);
        }
        note.setTag(newTagString);
    }

    public String getCurTime (){
        Date curDate = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(curDate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNote();
        EditorManager.getInstance().removeEditor(this);
    }

}
