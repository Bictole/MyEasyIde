package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
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
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectExplorer {

    private JTree jTree;
    private TreeModel treeModel;

    private MainFrame mainFrame;

    private boolean isEditing = false;

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public JTree getjTree() {
        return jTree;
    }

    public EditAction getEditAction() {
        return editAction;
    }

    public void setEditAction(EditAction editAction) {
        this.editAction = editAction;
    }

    public static class EditAction {
        public enum Action {
            COPY,
            CUT
        }
        public Node copyNode;
        public Action action;

        public EditAction(Node copyNode, Action action) {
            this.copyNode= copyNode;
            this.action = action;
        }
    }

    private EditAction editAction;



    public ProjectExplorer(MainFrame mainFrame, Node root) {
        this.mainFrame = mainFrame;
        treeModel = new NodeTreeModel(root);
        jTree = new JTree(treeModel);
        createUpdater(root);

        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

    private void createUpdater(Node root) {
        Thread updater = new Thread(this.new FilesWatcher(root));
        updater.start();
    }

    class FilesWatcher implements Runnable {
        private final Node root;

        public FilesWatcher(Node root) {
            this.root = root;
        }

        private void registerPaths(WatchService watchService, Node node) {
            try {
                node.getPath().register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
                for (var child : node.getChildren()) {
                    if (child.isFolder())
                        registerPaths(watchService, child);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            WatchService watchService
                    = null;
            try {
                watchService = FileSystems.getDefault().newWatchService();
            } catch (IOException e) {
                e.printStackTrace();
            }
            registerPaths(watchService, root);
            WatchKey key;
            try {
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (isEditing) {
                            System.out.println("Editing...");
                            jTree.updateUI();
                        }
                        else {
                            System.out.println(
                                    "Event kind:" + event.kind()
                                            + ". File affected: " + event.context() + ".");
                            updateTree();
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public void updateTree() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ProjectManager pM = (ProjectManager) mainFrame.getProjectService();
                    pM.updateTree(root);
                    jTree.updateUI();
                }
            });
        }
    }


}