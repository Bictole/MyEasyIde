package fr.epita.assistants.myide.domain.node;

import fr.epita.assistants.myide.domain.entity.Node;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FolderNode implements Node {

    private final Path path;
    private final Type type;
    private final List<Node> children;

    public FolderNode(Path path) {
        this.path = path;
        this.type = Types.FOLDER;
        this.children = new ArrayList<>();
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
        return children;
    }
}
