package main.ui;

import javafx.scene.control.TreeCell;

/**
 * Created by Elliot Kipling on 26/12/2016.
 */
public class TreeCellValueFactory extends TreeCell<String> {

    private TreeTaskItem container;

    public TreeCellValueFactory(TreeTaskItem item) {
        this.container = item;
    }



}
