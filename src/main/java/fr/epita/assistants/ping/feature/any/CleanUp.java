package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.service.NodeManager;
import fr.epita.java.util.Regexes;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CleanUp implements Feature{
    private class ExecutionReportCleanUp implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";

        public ExecutionReportCleanUp() {
            this.success = true;
        }
        public ExecutionReportCleanUp(String errorMessage) {
            this.success = false;
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    @Override
    public ExecutionReport execute(Project project, Object... param) {
        /*if (param.length > 0)
            return new CleanUp.ExecutionReportCleanUp("Too much argument provided");*/

        NodeManager nodeManager = new NodeManager();

        return cleanUp(project.getRootNode(), nodeManager);
    }

    private boolean cleanUpRec(Node current, NodeManager nodeManager, Path ignoreFile, Path actualPath) {
        for (int i = 0; i < current.getChildren().size(); i++) {
            boolean res = false;
            if (!current.getChildren().get(i).getPath().toFile().getName().equals(".myideignore"))
                res = cleanUpRec(current.getChildren().get(i), nodeManager, ignoreFile, actualPath.resolve(current.getChildren().get(i).getPath().toFile().getName()));

            if (res)
                i -= 1;
        }
        if (actualPath.equals(Path.of("")))
            return true;

        boolean delete = false;

        try {
            Scanner scan = new Scanner(ignoreFile);
            while (scan.hasNext() && !delete) {
                String line = scan.nextLine();
                Pattern pattern = Pattern.compile(Pattern.quote(line));
                if (pattern.matcher(actualPath.toString()).find())
                {
                    nodeManager.delete(current);
                    // delete(Path.of(line));
                    //Files.deleteIfExists(current.getPath());
                    //nodeManager.deleteNode(current);
                    delete = true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return delete;
    }

    private ExecutionReportCleanUp cleanUp(Node root, NodeManager nodeManager) {
        try {
            cleanUpRec(root, nodeManager, root.getPath().resolve(".myideignore"), Path.of(""));
        }
        catch (Exception e)
        {
            return new ExecutionReportCleanUp("Deletion failed");
        }

        return new ExecutionReportCleanUp();
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.CLEANUP;
    }
}
