package fr.epita.assistants.ping.node;

import fr.epita.assistants.myide.domain.entity.Node;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FileNode implements Node {

    private final Path path;
    private final Type type;

    public FileNode(Path path) {
        this.path = path;
        this.type = Types.FILE;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<Node> getChildren() {
        return Collections.EMPTY_LIST;
    }

    public boolean search(String str) throws FileNotFoundException {
        Scanner scan = new Scanner(path.toFile());
        while (scan.hasNext()) {
            String line = scan.nextLine().toString();
            if (line.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
