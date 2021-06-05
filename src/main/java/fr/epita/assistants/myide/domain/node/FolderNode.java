package fr.epita.assistants.myide.domain.node;

import fr.epita.assistants.myide.domain.entity.Node;

import java.io.FileNotFoundException;
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

    public boolean search(String str) throws FileNotFoundException {
        boolean ret = true;
        for (var child : children) {
            if (child.isFolder())
                ret &= ((FolderNode)child).search(str);
            else
                ret &= ((FileNode)child).search(str);
        }
        return ret;
    }
}
