package com.example.logsite.ui.views;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.logsite.R;
import com.example.logsite.database.CRUD;
import com.example.logsite.database.Note;
import com.example.logsite.databinding.FragmentViewsBinding;
import com.example.logsite.editor.EditorActivity;
import com.example.logsite.editor.LinkSpan;
import com.example.logsite.noteselector.NoteSelectorActivity;
import com.example.logsite.ui.notes.NotesTitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewsFragment extends Fragment {

    private FragmentViewsBinding binding;

    ViewsTitleBar titleBar;
    private List<Note> noteList = new ArrayList<>();
    private List<ViewNode> viewNodes = new ArrayList<>();
    private List<RelativeLayout.LayoutParams> paramsList = new ArrayList<>();
    private List<DrawLinkView> drawLinkViewList = new ArrayList<>();
    private List<Pair<Integer, Integer>> links = new ArrayList<>();
    private RelativeLayout layoutZone;
    private long curId; //保存当前根节点的id，刷新用
    private int viewMode = 0;

    ActivityResultLauncher<Intent> editorLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (viewMode == 0){
                        refreshAll();
                    }
                    else {
                        //刷新树
                        deleteAll();
                        refreshNoteList();
                        drawTree(curId);
                    }
                }
            }
    );

    //点击图标，选择根节点
    ActivityResultLauncher<Intent> selectorLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //如果正常返回，换标签并展示树
                if (result.getResultCode() == RESULT_OK){
                    deleteAll();
                    //这是要有的，把数据读进来。。。。。
                    refreshNoteList();
                    viewMode = 1 - viewMode;
                    titleBar.switchViewModeIcon(viewMode);
                    if (result.getData() != null){
                        long id = result.getData().getLongExtra("id", 0);
                        this.curId = id;
                        drawTree(id);
                    }
                }
            }
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        ViewsViewModel viewsViewModel =
//                new ViewModelProvider(this).get(ViewsViewModel.class);
//
//        binding = FragmentViewsBinding.inflate(inflater, container, false);
        View root =  inflater.inflate(R.layout.fragment_views, container,false);

        //标题栏
        titleBar = root.findViewById(R.id.views_title_bar);
        titleBar.setListener(new ViewsTitleBar.OnTitleBarClickListener() {
            @Override
            public void onViewModeClick() {
                if (viewMode == 0){
                    Intent intent = new Intent(getContext(), NoteSelectorActivity.class);
                    selectorLauncher.launch(intent);
                }
                else {
                    viewMode = 0;
                    refreshAll();
                    titleBar.switchViewModeIcon(viewMode);
                }
            }

        });

        //绘图区
        layoutZone = root.findViewById(R.id.layout_zone);
        //获取数据
        refreshNoteList();
        //画节点
        initNodes();
        //画线
        drawLines(this.noteList);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void deleteAll (){
        layoutZone.removeAllViews();
        noteList.clear();
        viewNodes.clear();
        paramsList.clear();
        links.clear();
        drawLinkViewList.clear();
    }

    public void refreshAll (){
        deleteAll();
        //获取数据
        refreshNoteList();
        //画节点
        initNodes();
        //画线
        drawLines(this.noteList);
    }

    public void addViewNode (Note note){
        if (note != null){
            ViewNode viewNode = new ViewNode(getContext());
            viewNode.note = note;
            viewNode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view instanceof ViewNode){
                        Note curNote = ((ViewNode) view).note;
                        Intent intent = new Intent(getContext(), EditorActivity.class);
                        intent.putExtra("id", curNote.getId());
                        intent.putExtra("title", curNote.getTitle());
                        intent.putExtra("content", curNote.getContent());
                        intent.putExtra("tag", curNote.getTag());
                        intent.putExtra("time", curNote.getTime());
                        intent.putExtra("mode", (int)1);
                        editorLauncher.launch(intent);
                    }
                }
            });
            viewNode.setTitle(note.getTitle());
            viewNodes.add(viewNode);
        }
    }

    //得到id对应的note对象
    public Note findNote (Long id){
        for (Note note : noteList){
            if (note.getId() == id){
                return note;
            }
        }
        return null;
    }

    //得到note的所有link号
    public List<Long> getLinks (Note note){
        List<Long> links = new ArrayList<>();
        String content = note.getContent();
        Pattern linkPattern = Pattern.compile("<link:[0-9]+>");
        Matcher linkMatcher = linkPattern.matcher(content);
        while (linkMatcher.find()){
            if (checkNoteExist(getId(linkMatcher.group()))){
                links.add(getId(linkMatcher.group()));
            }
        }
        return links;
    }

    //检查现在列表里的最后cnt个note，得到新的note加入列表，顺便加入view
    private int addToCurNotes (List<Note> curNotes, int curRowCnt){
        int rowCnt = 0;
        int curEnd = curNotes.size();
        for (int i = curNotes.size() - curRowCnt; i < curEnd; i++){
            Note note = curNotes.get(i);
            List<Long> links = getLinks(note);
            for (Long linkId : links){
                Note toNote = findNote(linkId);
                if (!curNotes.contains(toNote)){
                    rowCnt ++;
                    curNotes.add(toNote);
                    addViewNode(toNote);
                    //添加进连线里
                    this.links.add(new Pair<Integer, Integer>(i, curNotes.size() - 1));
//                    Log.d("ViewsFragment", "addToCurNotes: " + i + " " + (curNotes.size() - 1));
                }
            }
        }
        return rowCnt;
    }

    //计算line这一行（从0开始）、有rowcnt个node，他们的位置，并写进paramslist里，并且画出来
    private void drawViews (int line, int rowCnt){
        int topMargin = (1 + line) * dpToPx(90);
        for (int i = 0; i < rowCnt; i++){
            ViewNode viewNode = viewNodes.get(viewNodes.size() - rowCnt + i);
            int leftMargin = (i + 1) * (dpToPx(320) / (rowCnt + 1));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPx(100), dpToPx(40));
            layoutParams.topMargin = topMargin;
            layoutParams.leftMargin = leftMargin;
            layoutZone.addView(viewNode, layoutParams);
            paramsList.add(layoutParams);
        }
    }

    private void drawTreeLinks(){
        for (Pair<Integer, Integer> link : links){
            drawLine(link.first, link.second);
        }
    }

    public void drawTree (long id){
        List<Note> curNotes = new ArrayList<>();
        Note rootNote = findNote(id);
        //初始化，note的list里加入根节点，并且添加进views，设置rowcnt和line（太晚了神志不清了要靠写注释来保持思考了）
        if (rootNote != null){
            curNotes.add(rootNote);
            addViewNode(rootNote);
            int rowCnt = 1;
            int line = 0;
            while (rowCnt != 0){
                //先画当前的，然后得到下一行
                drawViews(line, rowCnt);
                drawTreeLinks();
                Log.d("ViewsFragment", "drawTree: line:" + line);
                rowCnt = addToCurNotes(curNotes, rowCnt);
                line ++;
            }
        }



    }

    public void initNodes (){
        for (int i = 0; i < noteList.size(); i++){
            int topMargin = (new Random().nextInt(11) + 1) * dpToPx(45);
            int leftMargin = new Random().nextInt(4) * dpToPx(100);
            ViewNode viewNode = new ViewNode(getContext());
            //把完整的note塞进去主要是为了可以点击
            viewNode.note = noteList.get(i);
            viewNode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view instanceof ViewNode){
                        Note curNote = ((ViewNode) view).note;
                        Intent intent = new Intent(getContext(), EditorActivity.class);
                        intent.putExtra("id", curNote.getId());
                        intent.putExtra("title", curNote.getTitle());
                        intent.putExtra("content", curNote.getContent());
                        intent.putExtra("tag", curNote.getTag());
                        intent.putExtra("time", curNote.getTime());
                        intent.putExtra("mode", (int)1);
                        editorLauncher.launch(intent);
                    }
                }
            });
            viewNode.setTitle(noteList.get(i).getTitle());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPx(100), dpToPx(40));
            layoutParams.topMargin = topMargin;
            layoutParams.leftMargin = leftMargin;
            layoutZone.addView(viewNode, layoutParams);
            viewNodes.add(viewNode);
            paramsList.add(layoutParams);
        }
    }

    //从i号node到j号node画线
    public void drawLine (int i, int j){
        RelativeLayout.LayoutParams defaultParams = new RelativeLayout.LayoutParams(3000, 3000);
        defaultParams.topMargin = 0;
        defaultParams.leftMargin = 0;
        DrawLinkView drawLinkView = new DrawLinkView(getContext(),
                paramsList.get(i).leftMargin + dpToPx(50),
                paramsList.get(i).topMargin + dpToPx(40),
                paramsList.get(j).leftMargin + dpToPx(50),
                paramsList.get(j).topMargin);
        drawLinkView.invalidate();
        drawLinkViewList.add(drawLinkView);
        layoutZone.addView(drawLinkViewList.get(drawLinkViewList.size() - 1), defaultParams);
    }

    //画传入的notelist里所有关系
    //要保证notelist里的note的i和paramslist里的i是对应的！！
    //这里传了一个notelist进来，是因为说不定可以选择把一棵树里的图全部展现出来，那样就先准备好一个notelist，然后准备views和params即可
    //正常用的话就直接传this.noteList
    public void drawLines (List<Note> noteList){
        RelativeLayout.LayoutParams defaultParams = new RelativeLayout.LayoutParams(3000, 3000);
        defaultParams.topMargin = 0;
        defaultParams.leftMargin = 0;

        for (int i = 0; i < noteList.size(); i++){
            Note fromNote = noteList.get(i);
            for (int j = 0; j < noteList.size(); j++){
                Note toNote = noteList.get(j);
                if (checkLink(fromNote, toNote)){
                    DrawLinkView drawLinkView = new DrawLinkView(getContext(),
                                        paramsList.get(i).leftMargin + dpToPx(50),
                                        paramsList.get(i).topMargin + dpToPx(40),
                                        paramsList.get(j).leftMargin + dpToPx(50),
                                        paramsList.get(j).topMargin);
                    drawLinkView.invalidate();
                    drawLinkViewList.add(drawLinkView);
                    layoutZone.addView(drawLinkViewList.get(drawLinkViewList.size() - 1), defaultParams);
                }
            }
        }
    }

    public boolean checkLink (Note fromNote, Note toNote){
        String content = fromNote.getContent();
        Pattern linkPattern = Pattern.compile("<link:[0-9]+>");
        Matcher linkMatcher = linkPattern.matcher(content);
        while (linkMatcher.find()){
            if (getId(linkMatcher.group()) == toNote.getId()){
                return true;
            }
        }
        return false;
    }

    public long getId (String linkText){
        if (linkText != null){
            return Long.parseLong(linkText.substring(6, linkText.length() - 1));
        }
        return 0;
    }

    public void refreshNoteList(){
        CRUD op = new CRUD(this.getContext());
        op.open();
        if (noteList.size() > 0)noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();
//        Toast.makeText(getContext(), Integer.toString(noteList.size()), Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }

    public boolean checkNoteExist (long id){
        boolean ret = false;
        CRUD op = new CRUD(getContext());
        op.open();
        ret = op.checkNoteExist(id);
        op.close();
        return ret;
    }
}