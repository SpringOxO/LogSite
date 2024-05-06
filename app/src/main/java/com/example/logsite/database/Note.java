package com.example.logsite.database;

public class Note {
    private long id;
    private String title;
    private String content;
    private String time;
    private String tag;

    /**
     * 不加id构造note，用于新建note
     * @param title
     * @param content
     * @param time
     * @param tag
     */
    public Note (String title, String content, String time, String tag){
        this.title = title;
        this.content = content;
        this.time = time;
        this.tag = tag;
    }

    /**
     * 加上用id去构造一个note。这里应该仅用于产生一个已有的note
     * @param id
     * @param title
     * @param content
     * @param time
     * @param tag
     */
    public Note (long id, String title, String content, String time, String tag){
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
