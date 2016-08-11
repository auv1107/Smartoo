package com.sctdroid.autosigner.models;

/**
 * Created by lixindong on 1/19/16.
 */
public class Record {
    public static final String TYPE_ENTER = "type_enter";
    public static final String TYPE_EXIT = "type_exit";
    private String timestamp;
    private String behavior_type;
    private String id;

    public Record() {}
    public Record(String timestamp, String behavior_type) {
        this.timestamp = timestamp;
        this.behavior_type = behavior_type;
    }

    public String getBehavior_type() {
        return behavior_type;
    }

    public void setBehavior_type(String behavior_type) {
        this.behavior_type = behavior_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
