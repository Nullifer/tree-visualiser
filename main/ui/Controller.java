package main.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Party @ RuneMate.com
 */
public class Controller implements Initializable {

    @FXML
    private TreeView<String> treeView;
    @FXML
    private TextField treeItemName;
    @FXML
    private RadioButton toggleBranch;
    @FXML
    private RadioButton toggleLeaf;
    @FXML
    private TextArea treeItemNotes;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonRemove;
    @FXML
    private Label errorText;
    @FXML
    private TextField infoName;
    @FXML
    private Button infoUpdate;
    @FXML
    private TextArea infoNotes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        errorText.setText("");

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(toggleBranch, toggleLeaf);
        group.selectToggle(toggleBranch);

        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                TreeTaskItem item = (TreeTaskItem) newValue;
                infoName.setText(item.getName());
                infoNotes.setText(item.getNotes());
            }
        }));

        infoUpdate.setOnAction(event -> {
            if(!infoName.getText().isEmpty()) {
                TreeTaskItem item = (TreeTaskItem) treeView.getSelectionModel().getSelectedItem();
                if(item != null) {
                    item.update(infoName.getText(), infoNotes.getText());
                }
            }
        });

        buttonAdd.setOnAction(addTask());
        buttonRemove.setOnAction(removeSelected());
    }


    private EventHandler<ActionEvent> addTask() {
        return event -> {
            String name = treeItemName.getText();
            String desc = treeItemNotes.getText();
            if (!name.isEmpty()) {
                TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
                errorText.setText("");

                if(selected != null) {
                    if(!selected.isLeaf()) {
                        if (selected.getChildren().size() == 2) {
                            errorText.setText("Max children is 2");
                        } else {
                            selected.getChildren().add(new TreeTaskItem(name, desc, toggleLeaf.isSelected()));
                        }
                    } else {
                        errorText.setText("Cannot add children to leaf node");
                    }
                } else if (treeView.getRoot() != null){
                    errorText.setText("Please select an item to add childen to");
                } else {
                    treeView.setRoot(new TreeTaskItem(name, desc, toggleLeaf.isSelected()));
                    treeView.setShowRoot(true);
                }
            }
        };
    }

    private EventHandler<ActionEvent> removeSelected() {
        return event -> {
            TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
            if(selected != null) {
                if(treeView.getRoot().equals(selected)) {
                    treeView.setRoot(null);
                } else {
                    TreeItem<String> parent = selected.getParent();
                    if(parent != null) {
                        parent.getChildren().remove(selected);
                    }
                }
            } else {
                errorText.setText("Please select an item to remove");
            }
        };
    }
}
