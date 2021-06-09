package fr.epita.assistants.ping.node;

import fr.epita.assistants.myide.domain.entity.Node;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FolderNode implements Node {

    private final Path path;
    private final Type type;
    private final List<Node> children;
    private Node parent;

    public FolderNode(Path path, Node parent) {
        this.path = path;
        this.type = Types.FOLDER;
        this.children = new ArrayList<>();
        if (parent != null)
            setParent(parent);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent){
        ((FolderNode)parent).addChildren(this);
        this.parent = parent;
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

    public void addChildren(Node child)
    {
        children.add(child);
    }

    public boolean removeChild(Node child) {
        return children.remove(child);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public boolean search(String str) throws FileNotFoundException {
        for (var child : children) {
            if (child.isFolder() && ((FolderNode)child).search(str))
                return true;
            else if (child.isFile() && ((FileNode)child).search(str))
                return true;
        }
        return false;
    }
}
