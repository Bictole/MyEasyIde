package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.service.NodeManager;
import fr.epita.assistants.ping.service.ProjectManager;
import org.eclipse.sisu.launch.Main;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
    private DefaultTreeModel treeModel;

    private MainFrame mainFrame;

    public JTree getjTree() {
        return jTree;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public ProjectExplorer(MainFrame mainFrame, Node root) {
        this.mainFrame = mainFrame;
        DefaultMutableTreeNode top = createTree(root);
        treeModel = new DefaultTreeModel(top);
        jTree = new JTree(treeModel);

        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
                    if (tp == null)
                        return;
                    mainFrame.setSelectedFile(new File(createFilePath(tp)));
                    checkUpdateTree(root);
                }
                if (e.getClickCount() == 2) {
                    mouseOpenFile(e);
                }

            }
        });
    }

    private static String createFilePath(TreePath treePath) {
        StringBuilder sb = new StringBuilder();
        Object[] nodes = treePath.getPath();
        if (nodes.length == 0)
            return sb.toString();
        sb.append(nodes[0].toString());
        for (int i = 1; i < nodes.length; i++) {
            sb.append(File.separatorChar).append(nodes[i].toString());
        }
        return sb.toString();
    }

    private void mouseOpenFile(MouseEvent e) {
        TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
        if (tp == null)
            return;
        File file = new File(createFilePath(tp));
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

    public void checkUpdateTree(Node root) {
        File fdir = root.getPath().toFile();
        if (!fdir.exists())
            UITools.errorDialog(mainFrame, "ROOT FOLDER HAS BEEN DELETED");
        else if (!checkTree(root)) {
            updateTree(root);

            //DefaultMutableTreeNode top = createTree(root);
            //jTree.setModel(new DefaultTreeModel(top));
        }
    }

    public void updateTree(Node root) {
        try {
            File fdir = root.getPath().toFile();
            if (!fdir.exists()) {
                System.out.println(fdir + "does not exist");
                return;
            }
            File[] tmp = fdir.listFiles();
            List<File> ol = new ArrayList<File>(Arrays.asList(tmp));
            List<Node> nodes = root.getChildren();
            List<Path> paths = new ArrayList<>();
            for (int i = 0; i < ol.size(); i++) {
                File file = ol.get(i);
                Optional<Node> next = nodes.stream()
                        .filter(node -> ((node.isFile() && file.isFile()) || (node.isFolder() && file.isDirectory()))
                                && node.getPath().equals(file.toPath())).findAny();
                if (!next.isPresent()) {
                    Node newNode = ((NodeManager) mainFrame.getProjectService().getNodeService()).createNode(root, file.getName(), file.isFile() ? Node.Types.FILE : Node.Types.FOLDER);
                    ((ProjectManager) mainFrame.getProjectService()).initNodes(newNode);
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(newNode.getPath());
                    fillTree(treeNode, newNode);
                    root.getTreeNode().add(treeNode);
                    treeModel.nodeChanged(root.getTreeNode());
                }
                else {
                    if (next.get().getTreeNode() == null) {
                        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(next.get().getPath());
                        fillTree(treeNode, next.get());
                        root.getTreeNode().add(treeNode);
                        treeModel.nodeChanged(root.getTreeNode());
                    }
                    else if (file.isDirectory())
                        updateTree(next.get());
                    paths.add(next.get().getPath());
                }
            }
            nodes = root.getChildren();
            if (nodes.size() != paths.size()) {
                nodes.stream().filter(node -> !paths.contains(node.getPath())).
                        forEach(node -> ((NodeManager) mainFrame.getProjectService().getNodeService()).deleteNode(node));
                treeModel.nodeChanged(root.getTreeNode());
            }
        } catch (Exception e) {
            UITools.errorDialog(mainFrame, e.getMessage());
        }

    }

    DefaultMutableTreeNode createTree(Node root) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(root.getPath());
        root.setTreeNode(top);
        if (root.isFile() || root.getChildren().isEmpty())
            return top;

        fillTree(top, root);

        return top;
    }

    void fillTree(DefaultMutableTreeNode root, Node dir) {

        List<Node> list = dir.getChildren();

        Collections.sort(list, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.isFolder() && o2.isFolder()) {
                    return o1.getPath().compareTo(o2.getPath());
                } else if (o1.isFolder()) {
                    return -1;
                } else if (o2.isFolder()) {
                    return 1;
                }
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i).getPath().toFile();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
            list.get(i).setTreeNode(node);
            if (list.get(i).isFolder()) {
                fillTree(node, list.get(i));
            }
            root.add(node);
        }
    }

    boolean checkTree(Node dir) {
        File fdir = dir.getPath().toFile();
        if (!fdir.exists())
            return false;
        File[] tmp = fdir.listFiles();
        List<File> ol = new ArrayList<File>(Arrays.asList(tmp));
        List<Node> nodes = dir.getChildren();
        List<Path> paths = new ArrayList<Path>();
        for (int i = 0; i < ol.size(); i++) {
            File file = ol.get(i);
            Optional<Node> next = nodes.stream()
                    .filter(node -> ((node.isFile() && file.isFile()) || (node.isFolder() && file.isDirectory()))
                            && node.getPath().equals(file.toPath())).findAny();
            if (!next.isPresent())
                return false;
            if (file.isDirectory() && !checkTree(next.get()))
                return false;
            paths.add(next.get().getPath());
        }
        return paths.size() == nodes.size();
    }

}
