package main.ui;

import com.eclipsesource.json.JsonObject;
import javafx.scene.control.TreeItem;

/**
 * @author Party @ RuneMate.com
 */
public class TreeTaskItem extends TreeItem<String> {

    private String name;
    private String notes;
    private TreeTaskItem success;
    private TreeTaskItem failed;
    private boolean leaf = false;
    private Boolean isSuccess = null;

    public TreeTaskItem(String name, String notes, boolean leaf, boolean isSuccess) {
        setValue((isSuccess ? "Success " : "Fail ") + (leaf ? "leaf: " : "branch: ") + name);
        this.name = name;
        this.notes = notes;
        this.leaf = leaf;
        this.isSuccess = isSuccess;
        setExpanded(true);
    }

    public TreeTaskItem(String name, String notes) {
        this.name = name;
        this.notes = notes;
        setValue("Root branch: " + name);
        setExpanded(true);
    }

    public void update(String name, String notes) {
        this.name = name;
        this.notes = notes;
        setValue((isSuccess == null ? "Root " : isSuccess ? "Success " : "Fail ") + (leaf ? "leaf: " : "branch: ") + name);
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public TreeTaskItem getSuccess() {
        return success;
    }

    public TreeTaskItem getFailed() {
        return failed;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public TreeTaskItem setSuccess(TreeTaskItem success) {
        this.success = success;
        return this;
    }

    public TreeTaskItem setFailed(TreeTaskItem failed) {
        this.failed = failed;
        return this;
    }

    public JsonObject toJsonObject() {
        JsonObject res = new JsonObject();
        res.add("name", name);
        res.add("notes", notes);
        res.add("leaf", leaf);
        if(isSuccess != null) {
            res.add("isSuccess", isSuccess);
        }
        if(getSuccess() != null) {
            res.add("success", success.toJsonObject());
        }
        if(getFailed() != null) {
            res.add("failure", failed.toJsonObject());
        }
        return res;
    }

}
