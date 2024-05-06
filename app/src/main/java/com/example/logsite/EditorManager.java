package com.example.logsite;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logsite.editor.EditorActivity;

import java.util.LinkedList;
import java.util.List;

public class EditorManager {
    private EditorManager(){

    }

    private static EditorManager instance = new EditorManager();
    private static List<EditorActivity> activityStack = new LinkedList<>();
    public static EditorManager getInstance(){
        return instance;
    }


    public void addEditor(EditorActivity aty) {
        activityStack.add(aty);
    }

    public void removeEditor(EditorActivity aty) {
        activityStack.remove(aty);
    }

    public void finishAllEditor(){
        Log.d("TAGEDITORMANAGER", "finishAllEditor: " + activityStack.size());
        for (int i = 0 , size = activityStack.size(); i < size;i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).saveNote();
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
}
