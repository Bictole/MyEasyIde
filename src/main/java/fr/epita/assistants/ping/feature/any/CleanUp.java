package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.NodeManager;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CleanUp implements Feature{
    public class ExecutionReportCleanUp implements Feature.ExecutionReport {
        public final boolean success;
        public String output;

        public ExecutionReportCleanUp(Boolean success, String output) {
            this.success = success;
            this.output = output;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }
    }

    @Override
    public ExecutionReport execute(Project project, Object... param) {

        NodeManager nodeManager = new NodeManager();

        return cleanUp(project.getRootNode(), nodeManager);
    }

    private boolean cleanUpRec(Node current, NodeManager nodeManager, Path ignoreFile, Path actualPath) {

        for (int i = 0; i < current.getChildren().size(); i++) {
            boolean res = false;
            if (!actualPath.equals(Path.of("")) || !current.getChildren().get(i).getPath().toFile().getName().equals(".myideignore"))
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
            System.out.println(e.getMessage());
        }

        return delete;
    }

    private ExecutionReportCleanUp cleanUp(Node root, NodeManager nodeManager) {
        try {
            for (var c : root.getChildren())
            {
                if (c.isFile() && c.getPath().getFileName().compareTo(Path.of(".myideignore")) == 0)
                {
                    cleanUpRec(root, nodeManager, root.getPath().resolve(".myideignore"), Path.of(""));
                    return new ExecutionReportCleanUp(true, "");
                }

            }
        }
        catch (Exception e)
        {
            return new ExecutionReportCleanUp(false, "Deletion failed");
        }

        return new ExecutionReportCleanUp(false, "No .myideignore file found");

    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.CLEANUP;
    }
}
