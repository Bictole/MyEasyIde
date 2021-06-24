package fr.epita.assistants.ping.service;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
import org.apache.commons.io.FileUtils;
import org.assertj.core.internal.ByteArrays;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeManager implements NodeService {

    private Node rootNode = null;

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public NodeManager() {
    }

    @Override
    public Node update(Node node, int from, int to, byte[] insertedContent) {
        if (node.isFolder()) {
            System.out.println("Can't update a folder");
            return null;
        }
        try
        {
            String FileContent = new String(FileUtils.readFileToByteArray(node.getPath().toFile()));
            String insertContent = new String(insertedContent);
            int len = FileContent.length();
            String begin = FileContent.substring(0, from);
            String end = "";
            if (to < len)
                end = FileContent.substring(to, len);
            String new_content = begin + insertContent + end;
            FileWriter writer = new FileWriter(node.getPath().toFile());
            writer.write(new_content, 0, new_content.length());
            writer.close();
            return node;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteNode(Node node) {
        FolderNode parent;
        if (node.isFile()) {
            // deletes from parent's children list
            FileNode file = (FileNode) node;
            parent = ((FolderNode) file.getParent());
        } else {
            FolderNode folder = (FolderNode) node;
            if (!folder.isEmpty()) {
                return false; // folder is not empty
            }
            parent = (FolderNode) folder.getParent();
        }
        return parent.removeChild(node);
    }

    public boolean olddelete(Node node) {
        if (node.isFile()) {
            if (!deleteNode(node))
                return false; // failed to remove node from parent
            // delete the actual file
            return node.getPath().toFile().delete();
        } else {
            FolderNode folder = (FolderNode) node;
            if (deleteNode(node)) {
                return folder.getPath().toFile().delete();
            } else {
                return false; // could not delete node
            }
        }
    }

    @Override
    public boolean delete(Node node) {
        if (node.isFile()) {
            if (!deleteNode(node))
                return false; // failed to remove node from parent
            // delete the actual file
            return node.getPath().toFile().delete();
        } else {
            for (int i = 0; i < node.getChildren().size();)
                delete(node.getChildren().get(i));
            if (deleteNode(node)) {
                return node.getPath().toFile().delete();
            } else {
                return false; // could not delete node
            }
        }
    }

    public Node createNode(Node folder, String name, Node.Type type) throws Exception {
        if (type == Node.Types.FILE) {
            FileNode file = new FileNode(folder.getPath().resolve(name), folder);
            return file;
        }
        if (type == Node.Types.FOLDER) {
            FolderNode new_folder = new FolderNode(folder.getPath().resolve(name), folder);
            return new_folder;
        }
        throw new Exception("Unknown Type");
    }

    @Override
    public Node create(Node folder, String name, Node.Type type) {
        if (!folder.isFolder()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Node oldmove(Node nodeToMove, Node destinationFolder) {
        if (nodeToMove.isFile() || ((FolderNode) nodeToMove).isEmpty()) {
            String name = nodeToMove.getPath().toFile().getName();
            try {
                Files.move(nodeToMove.getPath(), destinationFolder.getPath().resolve(name));
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace(); // file already exists must be sent to user
            } catch (Exception e) {
                e.printStackTrace(); // any other exception
            }
            return updateNodePath(nodeToMove, destinationFolder, name);
        } else {
            return null; // cant move folder if not empty
        }
    }

    @Override
    public Node move(Node nodeToMove, Node destinationFolder) {
        if (nodeToMove.isFile() || ((FolderNode) nodeToMove).isEmpty()) { // File or Empty Folder
            String name = nodeToMove.getPath().toFile().getName();
            try {
                Files.move(nodeToMove.getPath(), destinationFolder.getPath().resolve(name));
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace(); // file already exists must be sent to user
            } catch (Exception e) {
                e.printStackTrace(); // any other exception
            }
            return updateNodePath(nodeToMove, destinationFolder, name);
        } else { //Move all the content of the folder and the folder
            String name = nodeToMove.getPath().toFile().getName();
            try {
                Node folder = create(destinationFolder, name, Node.Types.FOLDER);
                for (int i = 0; i < nodeToMove.getChildren().size();)
                {
                    move(nodeToMove.getChildren().get(i), folder);
                }
                if (delete(nodeToMove))
                    return folder;

            } catch (Exception e){
                e.printStackTrace();
            }

            return null; // cant move folder
        }
    }

    public Node getFromSource(Node source, Path path) {
        if (source.getPath() == path)
            return source;
        for (Node child : source.getChildren()) {
            if (path.equals(child.getPath()))
                return child;
            if (path.startsWith(child.getPath())) {
                return this.getFromSource(child, path);
            }
        }
        return null;
    }

    public Node getFromRoot(Path path) {
        return getFromSource(rootNode, path);
    }

    public Node updateNodePath(Node nodeToUpdate, Node destinationFolder, String name) {
        //create new node for the moved file
        Node ret = null;
        try {
            ret = createNode(destinationFolder, name, nodeToUpdate.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteNode(nodeToUpdate);
        return ret;
    }

}