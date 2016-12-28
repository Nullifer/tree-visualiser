package main.ui;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.Main;
import main.writer.TemplateWriter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

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
    @FXML
    private MenuItem fileImport;
    @FXML
    private MenuItem saveFile;
    @FXML
    private MenuItem saveTemplate;
    @FXML
    private MenuItem exit;
    @FXML
    private MenuItem help;
    @FXML
    private RadioButton toggleSuccess;
    @FXML
    private RadioButton toggleFailed;
    private FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TreeViewer (*.tsv)", "*.tsv");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        errorText.setText("");

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(toggleBranch, toggleLeaf);
        group.selectToggle(toggleBranch);

        ToggleGroup group2 = new ToggleGroup();
        group2.getToggles().addAll(toggleSuccess, toggleFailed);
        group2.selectToggle(toggleSuccess);

        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                buttonAdd.setDisable(false);
                toggleFailed.setDisable(false);
                toggleSuccess.setDisable(false);
                toggleLeaf.setDisable(false);
                TreeTaskItem item = (TreeTaskItem) newValue;
                if(item.isLeaf() || item.getChildren().size() == 2) {
                    buttonAdd.setDisable(true);
                }
                if(item.getSuccess() != null) {
                    toggleSuccess.setDisable(true);
                }
                if(item.getFailed() != null) {
                    toggleFailed.setDisable(true);
                }
            }
        }));

        treeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TreeTaskItem item = (TreeTaskItem) newValue;
                infoName.setText(item.getName());
                infoNotes.setText(item.getNotes());
            }
        }));

        infoUpdate.setOnAction(event -> {
            if (!infoName.getText().isEmpty()) {
                TreeTaskItem item = (TreeTaskItem) treeView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    item.update(infoName.getText(), infoNotes.getText());
                }
            }
        });

        buttonAdd.setOnAction(addTask());
        buttonRemove.setOnAction(removeSelected());

        exit.setOnAction(event -> Main.getStage().close());
        help.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.runemate.com/community/threads/basic-tree-visualiser.9875/"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        saveTemplate.setOnAction(event -> {
            File directory = new DirectoryChooser().showDialog(Main.getStage());
            if(directory != null) {
                TemplateWriter writer = new TemplateWriter((TreeTaskItem) treeView.getRoot());
                writer.write(directory);
                exportTree(treeView.getRoot(), directory);
            }
        });

        saveFile.setOnAction(event -> {
            try {
                saveToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fileImport.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(filter);
            chooser.setSelectedExtensionFilter(filter);
            File file = chooser.showOpenDialog(Main.getStage());
            if(file != null && file.exists()) {
                String json = "";
                try(Scanner scanner = new Scanner(file)) {
                    while(scanner.hasNext()) {
                        json = json + scanner.nextLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if(!json.isEmpty()) {
                    JsonObject res = Json.parse(json).asObject();
                    if(res != null) {
                        treeView.setRoot(treeItemFromJson(res));
                    }
                }
            }
        });

        toggleFailed.setDisable(true);
        toggleSuccess.setDisable(true);
        toggleLeaf.setDisable(true);

    }


    private EventHandler<ActionEvent> addTask() {
        return event -> {
            String name = treeItemName.getText();
            String desc = treeItemNotes.getText();
            if (!name.isEmpty()) {
                TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
                errorText.setText("");

                if (selected != null) {
                    if (!selected.isLeaf()) {
                        if (selected.getChildren().size() == 2) {
                            errorText.setText("Max children is 2");
                        } else {
                            TreeTaskItem item;
                            TreeTaskItem parent = (TreeTaskItem) selected;

                            if(toggleSuccess.isSelected()) {
                                if(parent.getSuccess() != null) {
                                    errorText.setText(parent.getName() + " already has a success task");
                                } else {
                                    parent.setSuccess(item = new TreeTaskItem(name, desc, toggleLeaf.isSelected(), true));
                                    parent.getChildren().add(item);
                                }
                            } else {
                                if(parent.getFailed() != null) {
                                    errorText.setText(parent.getName() + " already has a failed task");
                                } else {
                                    parent.setFailed(item = new TreeTaskItem(name, desc, toggleLeaf.isSelected(), false));
                                    parent.getChildren().add(item);
                                }
                            }
                        }
                    } else {
                        errorText.setText("Cannot add children to leaf node");
                    }
                } else if (treeView.getRoot() != null) {
                    errorText.setText("Please select an item to add childen to");
                } else {
                    treeView.setRoot(new TreeTaskItem(name, desc));
                    treeView.setShowRoot(true);
                }
            }
        };
    }

    private EventHandler<ActionEvent> removeSelected() {
        return event -> {
            TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (treeView.getRoot().equals(selected)) {
                    treeView.setRoot(null);
                } else {
                    TreeItem<String> parent = selected.getParent();
                    if (parent != null) {
                        parent.getChildren().remove(selected);
                    }
                }
            } else {
                errorText.setText("Please select an item to remove");
            }
        };
    }

    private void exportTree(TreeItem<String> root, File directory) {
        TemplateWriter writer = new TemplateWriter((TreeTaskItem) root);
        writer.write(directory);
        for (TreeItem<String> item : root.getChildren()) {
            if(!item.getChildren().isEmpty()) {
                exportTree(item, directory);
            } else {
                TemplateWriter writer2 = new TemplateWriter((TreeTaskItem) item);
                writer2.write(directory);
            }
        }
    }

    private void saveToFile() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(filter);
        chooser.setSelectedExtensionFilter(filter);
        File file = chooser.showSaveDialog(Main.getStage());
        if (file != null) {
            if(!file.exists()) {
                file.createNewFile();
                file.setWritable(true);
            }

            if(file.exists()) {
                try(FileWriter writer = new FileWriter(file)) {
                    ((TreeTaskItem) treeView.getRoot()).toJsonObject().writeTo(writer, WriterConfig.PRETTY_PRINT);
                }
            }
        }
    }

    private TreeTaskItem treeItemFromJson(JsonObject res) {
        TreeTaskItem item = null;
        if(res != null) {
            String name = res.getString("name", "Root");
            String notes = res.getString("notes", "N/A");
            boolean leaf = res.getBoolean("leaf", false);
            if(res.get("isSuccess") != null) {
                item = new TreeTaskItem(name, notes, leaf, res.getBoolean("isSuccess", false));
            } else {
                item = new TreeTaskItem(name, notes);
            }
            if(res.get("success") != null) {
                TreeTaskItem success = treeItemFromJson(res.get("success").asObject());
                item.setSuccess(success);
                item.getChildren().add(success);
            }
            if(res.get("failure") != null) {
                TreeTaskItem failed = treeItemFromJson(res.get("failure").asObject());
                item.setFailed(failed);
                item.getChildren().add(failed);
            }
        }
        return item;
    }
}
