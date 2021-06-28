package fr.epita.assistants.ping.feature.any;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Search implements Feature {

    public class ExecutionReportSearch implements Feature.ExecutionReport {
        public final boolean success;
        public String errorMessage = "";
        public List<Path> filesMatch = new ArrayList<>();

        public ExecutionReportSearch(List<Path> filesMatch) {
            this.filesMatch = filesMatch;
            this.success = true;
        }

        public ExecutionReportSearch(String errorMessage) {
            this.success = false;
            this.errorMessage = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public List<Path> getFilesMatch() {
            return filesMatch;
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
            List<Path> filesMatch = new ArrayList<>();
            search(project.getRootNode(), research, filesMatch);
            return new ExecutionReportSearch(filesMatch);
        } catch (FileNotFoundException f) {
            return new ExecutionReportSearch("File not found exception");
        } catch (IllegalArgumentException e) {
            return new ExecutionReportSearch("Illegal argument provided");
        }
    }

    private void search(Node root, String research, List<Path> filesMatch) throws FileNotFoundException {
        for (var child : root.getChildren()) {
            if (child.isFolder()) {
                search(child, research, filesMatch);
            }
            else if (child.isFile() && this.scan_file(child, research).isPresent())
                filesMatch.add(child.getPath());
        }
    }

    private Optional<Path> scan_file(Node file, String research) throws FileNotFoundException {
        Scanner scan = new Scanner(file.getPath().toFile());
        while (scan.hasNext()) {
            String line = scan.nextLine();
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
