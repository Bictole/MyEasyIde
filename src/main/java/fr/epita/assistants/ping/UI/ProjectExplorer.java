package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.ping.UI.Action.TreeAction;
import fr.epita.assistants.ping.UI.Panel.Tab;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.service.NodeManager;
import fr.epita.assistants.ping.service.ProjectManager;
import org.eclipse.sisu.launch.Main;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;
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
        jTree.setDragEnabled(true);
        jTree.setDropMode(DropMode.ON_OR_INSERT);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        jTree.setTransferHandler(new TreeTransfertHandler());

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

            mainFrame.tabManager.OpenFile(file);
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
            assert watchService != null;
            registerPaths(watchService, root);
            WatchKey key;
            try {
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (isEditing)
                            jTree.updateUI();
                        else {
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
            SwingUtilities.invokeLater(() -> {
                ProjectManager pM = (ProjectManager) mainFrame.getProjectService();
                pM.updateTree(root);
                jTree.updateUI();
            });
        }
    }

    class TreeTransfertHandler extends TransferHandler {
        DataFlavor nodesFlavor;
        DataFlavor[] flavors = new DataFlavor[1];
        Node destination;

        public TreeTransfertHandler() {
            try {
                String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                        ";class=\"" +
                        javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                        "\"";
                nodesFlavor = new DataFlavor(mimeType);
                flavors[0] = nodesFlavor;
            } catch(ClassNotFoundException e) {
                System.out.println("ClassNotFound: " + e.getMessage());
            }
        }

        @Nullable
        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            List<Node> nodeList = new ArrayList<>();
            if (paths == null) {
                return null;
            }
            for (var path : paths)
                nodeList.add((Node) path.getLastPathComponent());
            return new NodesTransferable(nodeList.toArray(new Node[0]));
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if(!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            if(!support.isDataFlavorSupported(nodesFlavor)) {
                return false;
            }
            // Do not allow a drop on the drag source selections.
            JTree.DropLocation dl =
                    (JTree.DropLocation)support.getDropLocation();
            JTree tree = (JTree)support.getComponent();
            int dropRow = tree.getRowForPath(dl.getPath());
            int[] selRows = tree.getSelectionRows();
            assert selRows != null;
            for (int selRow : selRows) {
                if (selRow == dropRow) {
                    return false;
                }
                Node treeNode =
                        (Node) tree.getPathForRow(selRow).getLastPathComponent();
                for (Node offspring : treeNode.getChildren()) {
                    if (tree.getRowForPath(new TreePath(offspring.getPath())) == dropRow) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean importData(TransferSupport support) {
            if(!canImport(support)) {
                return false;
            }
            JTree.DropLocation dl =
                    (JTree.DropLocation)support.getDropLocation();
            destination = (Node) dl.getPath().getLastPathComponent();
            return true;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            // Extract transfer data.
            Node[] nodes = null;
            try {
                nodes = (Node[])data.getTransferData(nodesFlavor);
            } catch(UnsupportedFlavorException ufe) {
                System.out.println("UnsupportedFlavor: " + ufe.getMessage());
            } catch(java.io.IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }
            NodeService nService = mainFrame.getProjectService().getNodeService();
            assert nodes != null;
            setEditing(true);
            for (Node node : nodes) {
                TreeAction.pasteNode(mainFrame, node, destination);
                if ((action & MOVE) == MOVE) {
                    NodeManager nM = (NodeManager) mainFrame.getProjectService().getNodeService();
                    nM.delete(node);
                }
            }
            setEditing(false);
        }

        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        public class NodesTransferable implements Transferable {
            Node[] nodes;

            public NodesTransferable(Node[] nodes) {
                this.nodes = nodes;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavor.equals(flavor);
            }

            @NotNull
            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if(!isDataFlavorSupported(flavor))
                    throw new UnsupportedFlavorException(flavor);
                return nodes;
            }
        }
    }
}