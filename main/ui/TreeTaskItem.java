package main.ui;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 * @author Party @ RuneMate.com
 */
public class TreeTaskItem extends TreeItem<String> {

    private String name;
    private String notes;
    private boolean leaf;

    public TreeTaskItem(String name, String notes, boolean leaf) {
        setValue((leaf ? "L: " : "B: " ) + name);
        this.name = name;
        this.notes = notes;
        this.leaf = leaf;
        setExpanded(true);
    }

    public void update(String name, String notes) {
        this.name = name;
        this.notes = notes;
        setValue((leaf ? "L: " : "B: " ) + name);
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
}
