package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.ping.node.FileNode;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.nio.file.Path;
import java.util.Vector;

public class NodeTreeModel implements TreeModel {

    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
    private final Node root;

    public NodeTreeModel(Node root) {
        this.root = root;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Node parentNode = (Node) parent;
        if (parentNode.isFile())
            return null;
        return parentNode.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Node parentNode = (Node) parent;
        if (parentNode.isFile())
            throw new IllegalArgumentException("File has no child");
        return parentNode.getChildren().size();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Node parentNode = (Node) parent;
        Node childNode = (Node) child;
        return parentNode.getChildren().indexOf(childNode);
    }

    @Override
    public boolean isLeaf(Object node) {
        Node parentNode = (Node) node;
        return parentNode.isFile();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : "
                + path + " --> " + newValue);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    /*
        Fire the event listened
     */

    protected void fireTreeStructureChanged(Node oldRoot) {
        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this,
                new Object[] {oldRoot});
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
    }

    protected void fireTreeNodeChanged(Node oldRoot) {
        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this,
                new Object[] {oldRoot});
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
    }
}
