package fr.epita.assistants.ping.node;

import fr.epita.assistants.myide.domain.entity.Node;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FileNode implements Node {

    private final Path path;
    private final Type type;
    private Node parent = null;

    public FileNode(Path path, Node parent) {
        this.path = path;
        this.type = Types.FILE;
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

    @Override
    public String toString() {
        return path.toFile().getName();
    }
}
