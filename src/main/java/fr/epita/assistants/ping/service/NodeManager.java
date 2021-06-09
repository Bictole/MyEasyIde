package fr.epita.assistants.ping.service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeManager implements NodeService {

    public NodeManager() {
    }

    @Override
    public Node update(Node node, int from, int to, byte[] insertedContent) {
        return null;
    }

    public boolean deleteNode(Node node) {
        FolderNode parent;
        if (node.isFile()) {
            // deletes from parent's children list
            FileNode file = (FileNode) node;
            parent = ((FolderNode)file.getParent());
        }
        else {
            FolderNode folder = (FolderNode) node;
            if (!folder.isEmpty()) {
                return false; // folder is not empty
            }
            parent = (FolderNode)folder.getParent();
        }
        return parent.removeChild(node);
    }

    @Override
    public boolean delete(Node node) {
        if (node.isFile()) {
            if (!deleteNode(node))
                return false; // failed to remove node from parent
            // delete the actual file
            return node.getPath().toFile().delete();
        }
        else {
            FolderNode folder = (FolderNode) node;
            if (deleteNode(node)) {
                return folder.getPath().toFile().delete();
            }
            else {
                return false; // could not delete node
            }
        }
    }

    public Node createNode(Node folder, String name, Node.Type type) throws Exception {
        if (type == Node.Types.FILE)
        {
            FileNode file = new FileNode(Path.of(folder.getPath() + "/" + name), folder);
            return file;
        }
        if (type == Node.Types.FOLDER)
        {
            FolderNode new_folder = new FolderNode(Path.of(folder.getPath() + "/" + name), folder);
            return new_folder;
        }
        throw new Exception("Unknown Type");
    }

    @Override
    public Node create(Node folder, String name, Node.Type type) {
        if (!folder.isFolder())
        {
            System.err.println("Can't create a node from a file");
            return null;
        }
        try {
            Node n = createNode(folder, name, type);
            if (n.isFolder())
                Files.createDirectory(Path.of(folder.getPath() + "/" + name));
            else
                Files.createFile(Path.of(folder.getPath() + "/" + name));
            return n;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Node move(Node nodeToMove, Node destinationFolder) {
        if (nodeToMove.isFile() || ((FolderNode)nodeToMove).isEmpty()) {
            String name = nodeToMove.getPath().toFile().getName();
            try {
                Files.move(nodeToMove.getPath(), destinationFolder.getPath().resolve(name));
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace(); // file already exists must be sent to user
            } catch (Exception e) {
                e.printStackTrace(); // any other exception
            }

            //create new node for the moved file
            Node ret = null;
            try {
                ret = createNode(destinationFolder, name, nodeToMove.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            deleteNode(nodeToMove);
            return ret;
        }
        else {
            return null; // cant move folder if not empty
        }
    }
}
