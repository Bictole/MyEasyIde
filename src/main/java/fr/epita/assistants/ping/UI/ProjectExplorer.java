package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.NodeManager;
import fr.epita.assistants.ping.service.ProjectManager;
import org.eclipse.sisu.launch.Main;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectExplorer {

    private JTree jTree;
    private TreeModel treeModel;

    private MainFrame mainFrame;

    public JTree getjTree() {
        return jTree;
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)
                    (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we have already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */
            try {
                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode)
                        (node.getChildAt(index));
            } catch (NullPointerException exc) {
            }

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }

        public void treeNodesInserted(TreeModelEvent e) {
        }

        public void treeNodesRemoved(TreeModelEvent e) {
        }

        public void treeStructureChanged(TreeModelEvent e) {
        }
    }

    public ProjectExplorer(MainFrame mainFrame, Node root) {
        this.mainFrame = mainFrame;
        treeModel = new NodeTreeModel(root);
        jTree = new JTree(treeModel);

        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
                    if (tp == null)
                        return;
                }
                if (e.getClickCount() == 2) {
                    mouseOpenFile(e);
                }

            }
        });
    }

    private void mouseOpenFile(MouseEvent e) {
        TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
        if (tp == null)
            return;
        Node node = (Node) tp.getLastPathComponent();

        File file = node.getPath().toFile();
        if (file.isDirectory())
            return;
        try {
            String text = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            // Set the text
            mainFrame.getrSyntaxTextArea().setText(text);
            mainFrame.getrSyntaxTextArea().setEditable(true);
            mainFrame.setOpenedFile(file);
        } catch (Exception evt) {
            JOptionPane.showMessageDialog(mainFrame, evt.getMessage());
        }
    }
}