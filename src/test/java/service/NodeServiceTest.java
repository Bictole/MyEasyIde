package service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.service.NodeManager;
import fr.epita.assistants.ping.service.ProjectManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NodeServiceTest {

    private ProjectManager projectManager;
    private Path createPath;
    private Path deletePath;
    private Path movePath;
    private Path updatePath;

    private void makeDirectory(Path path) {
        if (Files.notExists(path))
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            try {
                FileUtils.cleanDirectory(path.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void makeFile(Path path) {
        if (Files.notExists(path))
            try {
                Files.createFile(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @BeforeAll
    public void setUp() {
        projectManager = new ProjectManager();
        try {
            makeDirectory(Path.of("ProjectTests/NodeManager"));

            createPath = Path.of("ProjectTests/NodeManager/CreateProject");
            makeDirectory(createPath);

            deletePath = Path.of("ProjectTests/NodeManager/DeleteProject");
            makeDirectory(deletePath);


            movePath = Path.of("ProjectTests/NodeManager/MoveProject");
            makeDirectory(movePath);
            makeDirectory(movePath.resolve("DestinationFolder"));


            updatePath = Path.of("ProjectTests/NodeManager/UpdateProject");
            makeDirectory(updatePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method cleans up the files & directories created for test
    // comment to avoid deleting (for manual debug)
    @AfterAll
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(createPath.getParent().toFile());
    }

    @Test
    public void CreateFileTest() {
        String name = "CreateFileTest.txt";
        Project project = projectManager.load(createPath);
        projectManager.getNodeService().create(project.getRootNode(), name, Node.Types.FILE);
        assertEquals(1, project.getRootNode().getChildren().size());
        assertTrue(Files.exists(createPath.resolve(name)));
    }

    @Test
    public void CreateFolderTest() {
        String name = "CreateFolder";
        Project project = projectManager.load(createPath);
        projectManager.getNodeService().create(project.getRootNode(), name, Node.Types.FOLDER);
        assertEquals(3, project.getRootNode().getChildren().size());
        assertTrue(Files.exists(createPath.resolve(name)));
    }

    @Test
    public void GetFolderTest() {
        String name = "CreateGetFolder";
        Project project = projectManager.load(createPath);
        Node created = projectManager.getNodeService().create(project.getRootNode(), name, Node.Types.FOLDER);
        Node get = ((NodeManager) projectManager.getNodeService()).getFromSource(project.getRootNode(), createPath.resolve(name));
        assertEquals(created, get);
    }

    @Test
    public void GetNotExistingFolderTest() {
        Project project = projectManager.load(createPath);
        Node get = ((NodeManager) projectManager.getNodeService()).getFromSource(project.getRootNode(), Path.of("/Wrong/path"));
        assertNull(get);
    }

    @Test
    public void DeleteFileProject() {
        String name = "ToDeleteFile.txt";
        makeFile(deletePath.resolve(name));
        Project project = projectManager.load(deletePath);
        assertEquals(1, project.getRootNode().getChildren().stream().filter(Node::isFile).toList().size());
        projectManager.getNodeService().delete(project.getRootNode().getChildren().stream().filter(Node::isFile).toList().get(0));
        assertEquals(0, project.getRootNode().getChildren().stream().filter(Node::isFile).toList().size());
        assertTrue(Files.notExists(deletePath.resolve(name)));
    }

    @Test
    public void DeleteFolderProject() {
        String name = "ToDeleteFolder";
        makeDirectory(deletePath.resolve(name));
        Project project = projectManager.load(deletePath);

        assertEquals(1, project.getRootNode().getChildren().stream().filter(Node::isFolder).toList().size());
        projectManager.getNodeService().delete(project.getRootNode().getChildren().stream().filter(Node::isFolder).toList().get(0));
        assertEquals(0, project.getRootNode().getChildren().stream().filter(Node::isFolder).toList().size());
        assertTrue(Files.notExists(deletePath.resolve(name)));
    }

    @Test
    public void MoveFileProject() throws IOException {
        String name = "ToMoveFile.txt";
        makeFile(movePath.resolve(name));
        Project project = projectManager.load(movePath);

        assertEquals((int) Files.list(movePath).count(),project.getRootNode().getChildren().size());
        FileNode file = (FileNode)project.getRootNode().getChildren().stream().filter(node -> node.isFile() && node.getPath().toFile().getName().equals("ToMoveFile.txt")).toList().get(0);
        FolderNode destinationFolder = (FolderNode) project.getRootNode().getChildren().stream()
                .filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("DestinationFolder")).toList().get(0);
        projectManager.getNodeService().move(file, destinationFolder);
        assertEquals(Files.list(movePath).filter(path -> !path.toFile().isFile()).toList().size(),
                project.getRootNode().getChildren().stream().filter(Node::isFolder).toList().size());
        assertTrue(destinationFolder.getChildren().get(0).isFile());
    }

    @Test
    public void MoveFolderProject() {
        String name = "ToMoveFolder";
        makeDirectory(movePath.resolve(name));

        Project project = projectManager.load(movePath);
        assertEquals(2, project.getRootNode().getChildren().size());
        FolderNode folderToMove = (FolderNode) project.getRootNode().getChildren().stream().filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("ToMoveFolder")).toList().get(0);
        FolderNode destinationFolder = (FolderNode) project.getRootNode().getChildren().stream().filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("DestinationFolder")).toList().get(0);
        projectManager.getNodeService().move(folderToMove, destinationFolder);
        assertEquals(1, project.getRootNode().getChildren().stream().filter(Node::isFolder).toList().size());
        assertEquals(0, project.getRootNode().getChildren().stream().filter(Node::isFile).toList().size());
        assertEquals(1, destinationFolder.getChildren().stream().filter(Node::isFolder).toList().size());
    }

    @Test
    public void UpdateFolderProject() {
        String name = "Folder";
        makeDirectory(updatePath.resolve(name));

        Project project = projectManager.load(updatePath);
        Node folderNode = project.getRootNode().getChildren().stream().filter(node -> node.getPath().toFile().getName().equals(name)).toList().get(0);
        assertNull(projectManager.getNodeService().update(folderNode, 0, 5, "aled".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void UpdateFileProject() {
        String name = "File.txt";
        makeFile(updatePath.resolve(name));

        Project project = projectManager.load(updatePath);
        Node folderNode = project.getRootNode().getChildren().stream().filter(node -> node.getPath().toFile().getName().equals(name)).toList().get(0);
        assertNotNull(projectManager.getNodeService().update(folderNode, 4, 6, "Au secours".getBytes(StandardCharsets.UTF_8)));
    }
}
