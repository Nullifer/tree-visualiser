package main.writer;

import main.ui.TreeTaskItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Party
 */
public class TemplateWriter {

    private TreeTaskItem treeTaskItem;

    public TemplateWriter(TreeTaskItem treeTaskItem) {
        this.treeTaskItem = treeTaskItem;
    }

    public void write(File directory) {
        String name = treeTaskItem.getName();
        String notes = treeTaskItem.getNotes();
        String template = read();

        notes = "* " + notes;
        notes = notes.replace(System.lineSeparator(), System.lineSeparator() + " * ");
        template = template.replace("{$NOTES}", notes);
        template = template.replace("{$NAME}", name);

        TreeTaskItem success;
        TreeTaskItem failure;
        String successTask = null;
        String failedTask = null;

        if(!treeTaskItem.isLeaf()) {
            String importString = "TreeTask;" + System.lineSeparator();
            String branchString = "BranchTask {" + System.lineSeparator();
            if ((success = treeTaskItem.getSuccess()) != null) {
                importString = importString + System.lineSeparator() + "import path.to.your." + (successTask = success.getName());
                branchString = branchString + System.lineSeparator()
                        + "    private " + successTask + " " + successTask.toLowerCase() + " = new " + successTask + "();";
            }
            if ((failure = treeTaskItem.getFailed()) != null) {
                importString = importString + System.lineSeparator()  + "import path.to.your." + (failedTask = failure.getName());
                branchString = branchString + System.lineSeparator()
                        + "    private " + failedTask + " " + failedTask.toLowerCase() + " = new " + failedTask + "();";
            }

            template = template.replace("TreeTask;", importString);
            template = template.replace("BranchTask {", branchString);
        }
        template = template.replace("{$FAIL}", failedTask == null ? "null" : failedTask.toLowerCase());
        template = template.replace("{$SUCCESS}", successTask == null ? "null" : successTask.toLowerCase());

        File file = new File(directory + File.separator + name + ".java");

        if(!file.exists()) {
            try {
                file.createNewFile();
                file.setWritable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(template);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(getClass().getResourceAsStream((treeTaskItem.isLeaf() ? "leaf" : "branch") + "_template.txt"))) {
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

}
