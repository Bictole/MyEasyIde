package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.node.FolderNode;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
        if (param.length > 0)
            return new CleanUp.ExecutionReportCleanUp("Too much argument provided");
        return cleanUp(project.getRootNode());
    }

    private void cleanUpRec(Node current, Node parent, Path ignoreFile) {
        for (var child : current.getChildren()) {
            cleanUpRec(child, current, ignoreFile);
        }

        try {
            Scanner scan = new Scanner(ignoreFile);
            while (scan.hasNext()) {
                String line = scan.nextLine().toString();
                if (String.valueOf(current.getPath()).endsWith(line)) {
                    // delete(Path.of(line));
                    Files.deleteIfExists(current.getPath());

                    if (parent != null) {
                        ((FolderNode) parent).deleteChildren(current);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private ExecutionReportCleanUp cleanUp(Node root) {
        try {
            cleanUpRec(root, null, Path.of(root.getPath() + "/" + ".myideignore"));
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
