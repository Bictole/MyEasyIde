package fr.epita.assistants.myide.domain.node;

import fr.epita.assistants.myide.domain.entity.Node;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class File implements Node {

    private final Path path;
    private final Type type;

    public File(Path path) {
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
}
