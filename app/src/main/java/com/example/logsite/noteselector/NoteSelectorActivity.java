package com.example.logsite.noteselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logsite.R;
import com.example.logsite.adapter.noteadapter.NoteAdapter;
import com.example.logsite.adapter.noteadapter.NoteTouchListener;
import com.example.logsite.adapter.selectnoteadapter.SelectNoteAdapter;
import com.example.logsite.adapter.selectnoteadapter.SelectNoteTouchListener;
import com.example.logsite.database.CRUD;
import com.example.logsite.database.Note;
import com.example.logsite.editor.EditorActivity;

import java.util.ArrayList;
import java.util.List;

public class NoteSelectorActivity extends Activity implements SelectNoteTouchListener.OnItemClickListener {
    List<Note> noteList = new ArrayList<>();
    RecyclerView recyclerView;
    SelectNoteAdapter noteAdapter;
//    TextView hint; //for debug
    private NoteSelectorTitleBar titleBar;
    private int searchMode = 0;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_selector_floating);
//        getSupportActionBar().hide();

//        hint = findViewById(R.id.hint);

        //标题栏
        titleBar = findViewById(R.id.note_selector_title_bar);
        titleBar.setListener(new NoteSelectorTitleBar.OnTitleBarClickListener() {
            @Override
            public void onSearchModeClick() {
                searchMode = 1 - searchMode;
                noteAdapter.setFilterMode(searchMode);
                titleBar.switchSearchModeIcon(searchMode);
            }
        });
        searchView = titleBar.getSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });
//        titleBar.bringToFront();
        //note列表
        refreshNoteList();
        recyclerView = findViewById(R.id.note_list);
        noteAdapter = new SelectNoteAdapter(this, noteList);
        recyclerView.setAdapter(noteAdapter);
        recyclerView.addOnItemTouchListener(new SelectNoteTouchListener(recyclerView, this));
//        recyclerView.bringToFront();

//        hint.setText(Integer.toString(noteAdapter.getItemCount()));
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position, long id) {
        SelectNoteAdapter.NoteViewHolder holder = (SelectNoteAdapter.NoteViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        assert holder != null;
        Note curNote = holder.getNote();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", curNote.getId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void refreshNoteList(){
        CRUD op = new CRUD(this);
        op.open();
        if (noteList.size() > 0)noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();;
    }
}
