package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class Search implements Feature {

    private class ExecutionReportSearch implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";
        public Path fileFound = null;

        public ExecutionReportSearch(Optional<Path> path) {
            success = path.isPresent();
            if (success)
                fileFound = path.get();
            if (!success)
                errorMessage = "No such expression found";
        }

        public ExecutionReportSearch(String errorMessage) {
            this.success = false;
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public Path getFileFound() {
            return fileFound;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    @Override
    public ExecutionReport execute(Project project, Object... param) {
        if (param.length > 1)
            return new ExecutionReportSearch("Too much string provided");
        try {
            String research = String.valueOf(param[0]);
            Optional<Path> found = search(project.getRootNode(), research);
            return new ExecutionReportSearch(found);
        } catch (FileNotFoundException f) {
            return new ExecutionReportSearch("File not found");
        } catch (IllegalArgumentException e) {
            return new ExecutionReportSearch("Illegal argument provided");
        }
    }

    private Optional<Path> search(Node root, String research) throws FileNotFoundException {
        for (var child : root.getChildren()) {
            if (child.isFolder())
            {
                var s = this.search(child, research);
                if (s.isPresent())
                    return s;
            }
            else if (child.isFile() && this.scan_file(child, research).isPresent())
                return Optional.of(child.getPath());
        }
        return Optional.empty();
    }

    private Optional<Path> scan_file(Node file, String research) throws FileNotFoundException {
        Scanner scan = new Scanner(file.getPath().toFile());
        while (scan.hasNext()) {
            String line = scan.nextLine().toString();
            if (line.contains(research)) {
                return Optional.of(file.getPath());
            }
        }
        return Optional.empty();
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.SEARCH;
    }
}
