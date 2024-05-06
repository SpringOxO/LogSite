package com.example.logsite.ui.notes;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logsite.R;
import com.example.logsite.adapter.noteadapter.NoteDecoration;
import com.example.logsite.database.CRUD;
import com.example.logsite.database.Note;
import com.example.logsite.adapter.noteadapter.NoteAdapter;
import com.example.logsite.adapter.noteadapter.NoteTouchListener;
import com.example.logsite.database.NotesDataBase;
import com.example.logsite.databinding.FragmentNotesBinding;
import com.example.logsite.editor.EditorActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment implements NoteTouchListener.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

    private FragmentNotesBinding binding;
    private FloatingActionButton btnAddNote;
    private NotesDataBase notesDbHelper;
    private NoteAdapter noteAdapter;
    private List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesTitleBar titleBar;
    private SearchView searchView;
    private int searchMode = 0;
    //下面两个是文档计数
    private TextView tvNotesCnt;
    private int notesCnt;
    //这个curNote是为了长按菜单而用来暂时存放目标笔记的
    private Note curNote;
    //这个position也是为了长按菜单，指示这个东西在adapter里的位置，有点冗余。。但就这样吧。
    private int curPosition;
    final Handler updateNotesCntHandler = new Handler();
    private Runnable updateNotesCntRunnable = new Runnable() {
        @Override
        public void run() {
            updateNotesCnt();
            updateNotesCntHandler.postDelayed(this, 100);
        }
    };


    private static final String TAG = "NotesFragment";

    ActivityResultLauncher<Intent> editorLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
//                    Toast.makeText(getContext(), "!!!", Toast.LENGTH_SHORT).show();
                    refreshNoteList();
                    noteAdapter.notifyDataSetChanged();
                }
            }
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //我之前在这里调了很久的报错，注释掉之后，又调调调，后来修了个别的地方好了，但，这里就注释掉吧。反正用不上。
//        NotesViewModel notesViewModel =
//                new ViewModelProvider(this).get(NotesViewModel.class);
//
//        binding = FragmentNotesBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
        View root =  inflater.inflate(R.layout.fragment_notes, container,false);

//        很痛苦的一件事情：使用google的底部导航栏模板，就不能设置noactionbar，只能将它hide，然后我在设置自己的toolbar的时候，就会告诉我已经有一个actionbar了。总之，我不许使用toolbar:(
//        toolbar = root.findViewById(R.id.view_toolbar);
//
//                setHasOptionsMenu(true);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        //标题栏的俩按钮和一个sv
        titleBar = root.findViewById(R.id.notes_title_bar);
        titleBar.setListener(new NotesTitleBar.OnTitleBarClickListener() {
            @Override
            public void onSearchModeClick() {
//                Toast.makeText(getContext(), "hh", Toast.LENGTH_SHORT).show();
                searchMode = 1 - searchMode;
                noteAdapter.setFilterMode(searchMode);
                titleBar.switchSearchModeIcon(searchMode);
            }

            @Override
            public void onOptionsClick() {
                Toast.makeText(getContext(), "hhh", Toast.LENGTH_SHORT).show();
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


        //新增按钮
        btnAddNote = root.findViewById(R.id.fab);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditorActivity.class);
                intent.putExtra("mode", (int)0);
                editorLauncher.launch(intent);
            }
        });

        //笔记列表
        recyclerView = root.findViewById(R.id.note_list);
        refreshNoteList();
        noteAdapter = new NoteAdapter(this.getContext(), noteList);
        recyclerView.setAdapter(noteAdapter);
//        Toast.makeText(getContext(), Integer.toString(noteAdapter.getItemCount()), Toast.LENGTH_SHORT).show();

        recyclerView.addOnItemTouchListener(new NoteTouchListener(recyclerView, this));
        NoteDecoration noteDecoration = new NoteDecoration(getContext(), 16);
        recyclerView.addItemDecoration(noteDecoration);

        //文档计数
        tvNotesCnt = root.findViewById(R.id.notes_cnt);

        updateNotesCntHandler.post(updateNotesCntRunnable);

        return root;
    }

    @SuppressLint("SetTextI18n")
    public void updateNotesCnt (){
        notesCnt = noteAdapter.getItemCount();
        tvNotesCnt.setText(notesCnt + " docs.");
    }

    //下面两个click函数都是recyclerview的，可以去看notetouchlistener里面定义的接口
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position, long id) {
//                Toast.makeText(getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
        NoteAdapter.NoteViewHolder holder = (NoteAdapter.NoteViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        assert holder != null;
        Note curNote = holder.getNote();
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtra("id", curNote.getId());
        intent.putExtra("title", curNote.getTitle());
        intent.putExtra("content", curNote.getContent());
        intent.putExtra("tag", curNote.getTag());
        intent.putExtra("time", curNote.getTime());
        intent.putExtra("mode", (int)1);
        editorLauncher.launch(intent);

    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, View itemView, int position, long id) {
        PopupMenu popup = new PopupMenu(getContext(), itemView);
        NoteAdapter.NoteViewHolder holder = (NoteAdapter.NoteViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        assert holder != null;
        curNote = holder.getNote();
//        Toast.makeText(getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
        curPosition = position;

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.note_long_press_menu);
        popup.show();
    }

    //这个click是弹出菜单的
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.note_delete){
            deleteNote(curNote);
            noteList.remove(curNote);
            noteAdapter.removeFilteredData(curNote);
            noteAdapter.notifyItemRemoved(curPosition);
            updateNotesCnt();
//            noteAdapter.notifyDataSetChanged();
//            noteAdapter.notifyItemRangeChanged(curPosition, noteAdapter.getItemCount() - curPosition);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void deleteNote(Note note){
        CRUD op = new CRUD(this.getContext());
        op.open();
        op.removeNote(note);
        op.close();
    }

    public void refreshNoteList(){
        CRUD op = new CRUD(this.getContext());
        op.open();
        if (noteList.size() > 0)noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();
//        Toast.makeText(getContext(), Integer.toString(noteList.size()), Toast.LENGTH_SHORT).show();
    }
}