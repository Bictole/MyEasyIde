package fr.epita.assistants.myide.domain;

import fr.epita.assistants.myide.domain.entity.Node;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Folder implements Node {

    private final Path path;
    private final Type type;
    private final List<Node> children;

    public Folder(Path path) {
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
