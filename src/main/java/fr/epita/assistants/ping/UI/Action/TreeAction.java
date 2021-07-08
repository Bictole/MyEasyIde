package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.Panel.NewProject;
import fr.epita.assistants.ping.UI.ProjectExplorer;
import fr.epita.assistants.ping.UI.UITools;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.service.NodeManager;
import org.eclipse.jgit.diff.Edit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static fr.epita.assistants.ping.UI.UITools.getResizedIcon;
import static fr.epita.assistants.ping.UI.UITools.getSelectedNode;

public class TreeAction {


    public static class actCopy extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actCopy(MainFrame frame) {
            super(
                    "Copy",
                    getResizedIcon(frame, Icons.COPY),
                    KeyEvent.VK_N,
                    "Copy File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            Node selected = getSelectedNode(mainFrame);
            mainFrame.getProjectExplorer().setEditAction(new ProjectExplorer.EditAction(selected, ProjectExplorer.EditAction.Action.COPY));
        }
    }

    public static class actCut extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actCut(MainFrame frame) {
            super(
                    "Cut",
                    getResizedIcon(frame, Icons.CUT),
                    KeyEvent.VK_N,
                    "Cut File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            Node selected = getSelectedNode(mainFrame);
            mainFrame.getProjectExplorer().setEditAction(new ProjectExplorer.EditAction(selected, ProjectExplorer.EditAction.Action.CUT));
        }
    }

    public static void pasteNode(MainFrame mainFrame, Node source, Node destination) { //FIXME
        NodeManager nM = (NodeManager) mainFrame.getProjectService().getNodeService();
        Path destPath = destination.getPath();
        try {
            if (source.isFile()) {
                while (true) {
                    try {
                        Files.copy(source.getPath(), destPath);
                        break;
                    } catch (FileAlreadyExistsException exp) {
                        Path cmpPath = destPath.toFile().isFile() ? destPath.getParent() : destPath;
                        if (!source.getPath().getParent().equals(cmpPath) && source.getPath().toFile().getName().equals(destPath.toFile().getName())) {
                            if (JOptionPane.showConfirmDialog(mainFrame, "Do you want overwrite ?",
                                    "Already exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                Files.copy(source.getPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                return;
                            }
                        } else {
                            Path filePath = destPath.toFile().isFile() ? destPath : source.getPath();
                            String newName = filePath.toFile().getName();
                            if (destPath.toFile().isDirectory()) {
                                destPath = destPath.resolve(newName);
                                if (destPath.toFile().exists())
                                    destPath = destPath.getParent().resolve(newName + " - Copy");
                            } else
                                destPath = destPath.getParent().resolve(newName + " - Copy");
                        }
                    } catch (Exception e) {
                        UITools.errorDialog(mainFrame, "Can not paste here.", "Paste error");
                        return;
                    }
                }
            } else {
                if (source.isFolder() && source.getPath().equals(destination.getPath())) {
                    destination = ((FolderNode) destination).getParent();
                    destPath = destPath.getParent();
                }
                Path initPath = destPath;
                while (true) {
                    try {
                        Files.copy(source.getPath(), destPath);
                        break;
                    } catch (FileAlreadyExistsException exp) {
                        Path cmpPath = destPath;
                        if (!source.getPath().getParent().equals(cmpPath) && source.getPath().toFile().getName().equals(destPath.toFile().getName())) {
                            if (JOptionPane.showConfirmDialog(mainFrame, "Do you want overwrite ?",
                                    "Already exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                Files.copy(source.getPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                return;
                            }
                        } else {
                            String newName = initPath.equals(destPath) ? source.getPath().toFile().getName() : destPath.toFile().getName();
                            if (initPath.equals(destPath)){
                                destPath = destPath.resolve(newName);
                                if (destPath.toFile().exists())
                                    destPath = destPath.getParent().resolve(newName + " - Copy");
                            }
                            else
                                destPath = destPath.getParent().resolve(newName + " - Copy");
                        }

                    } catch (Exception e) {
                        UITools.errorDialog(mainFrame, "Can not paste here.", "Paste error");
                        return;
                    }
                }
            }

            Node create = nM.createNode(destination, destPath.toFile().getName(), source.getType());
            if (create.isFolder()) {
                for (var c : source.getChildren()) {
                    pasteNode(mainFrame, c, create);
                }
            }
        } catch (Exception exp) {
            UITools.errorDialog(mainFrame, exp.getMessage(), "Paste error");
        }
    }

    public static class actPaste extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actPaste(MainFrame frame) {
            super(
                    "Paste",
                    getResizedIcon(frame, Icons.PASTE),
                    KeyEvent.VK_N,
                    "Paste File or Folder",
                    null);
            this.mainFrame = frame;
        }



        @Override
        public void actionPerformed(ActionEvent e) {
            ProjectExplorer.EditAction editAction = mainFrame.getProjectExplorer().getEditAction();
            Node selected = getSelectedNode(mainFrame);
            if (editAction == null)
                return;
            if (editAction.copyNode == null)
                mainFrame.getProjectExplorer().setEditAction(null);
            else {
                mainFrame.getProjectExplorer().setEditing(true);
                if (selected.isFile()) {
                    selected = ((FileNode) selected).getParent();
                }
                NodeManager nM = (NodeManager) mainFrame.getProjectService().getNodeService();
                pasteNode(mainFrame, editAction.copyNode, selected);
                if (editAction.action.equals(ProjectExplorer.EditAction.Action.CUT))
                    nM.delete(editAction.copyNode);
                mainFrame.getProjectExplorer().setEditing(false);
            }
        }
    }

    public static class actDelete extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actDelete(MainFrame frame) {
            super(
                    "Delete",
                    null,
                    KeyEvent.VK_N,
                    "Delete File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            mainFrame.getProjectExplorer().setEditing(true);
            Node selected = getSelectedNode(mainFrame);
            NodeManager nM = (NodeManager) mainFrame.getProjectService().getNodeService();
            nM.delete(selected);
            mainFrame.getProjectExplorer().setEditing(true);
        }
    }

}
