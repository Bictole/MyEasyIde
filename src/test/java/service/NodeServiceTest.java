package service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.service.ProjectManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NodeServiceTest {

    private ProjectManager projectManager;
    private Path createPath;
    private Path deletePath;
    private Path movePath;
    private Path updatePath;

    private void makeDirectory(Path path) throws IOException {
        if (Files.notExists(path))
            Files.createDirectory(path);
        else
            FileUtils.cleanDirectory(path.toFile());
    }

    private void makeFile(Path path) throws IOException {
        if (Files.notExists(path))
            Files.createFile(path);
    }

    @BeforeEach
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
            makeDirectory(movePath.resolve("ToMoveFolder"));
            makeFile(movePath.resolve("ToMoveFile.txt"));


            updatePath = Path.of("ProjectTests/NodeManager/UpdateProject");
            makeDirectory(updatePath);
            makeDirectory(updatePath.resolve("Folder"));
            makeFile(updatePath.resolve("File.txt"));

        } catch (FileAlreadyExistsException fe) {

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        assertEquals(1, project.getRootNode().getChildren().size());
        assertTrue(Files.exists(createPath.resolve(name)));
    }

    @Test
    public void DeleteFileProject() throws IOException {
        String name = "ToDeleteFile.txt";
        makeFile(deletePath.resolve(name));
        Project project = projectManager.load(deletePath);
        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().size());
        projectManager.getNodeService().delete(project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().get(0));
        assertEquals(0, project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().size());
        assertTrue(Files.notExists(deletePath.resolve(name)));
    }

    @Test
    public void DeleteFolderProject() throws IOException {
        String name = "ToDeleteFolder";
        makeDirectory(deletePath.resolve(name));
        Project project = projectManager.load(deletePath);

        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
        projectManager.getNodeService().delete(project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().get(0));
        assertEquals(0, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
        assertTrue(Files.notExists(deletePath.resolve(name)));
    }

    @Test
    public void MoveFileProject() {
        Project project = projectManager.load(movePath);

        assertEquals(3, project.getRootNode().getChildren().size());
        FileNode file = (FileNode)project.getRootNode().getChildren().stream().filter(node -> node.isFile() && node.getPath().toFile().getName().equals("ToMoveFile.txt")).toList().get(0);
        FolderNode destinationFolder = (FolderNode) project.getRootNode().getChildren().stream()
                .filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("DestinationFolder")).toList().get(0);
        projectManager.getNodeService().move(file, destinationFolder);
        assertEquals(2, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
        assertEquals(true, destinationFolder.getChildren().get(0).isFile());
    }

    @Test
    public void MoveFolderProject() {
        Project project = projectManager.load(movePath);
        assertEquals(3, project.getRootNode().getChildren().size());
        FolderNode folderToMove = (FolderNode) project.getRootNode().getChildren().stream().filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("ToMoveFolder")).toList().get(0);
        FolderNode destinationFolder = (FolderNode) project.getRootNode().getChildren().stream().filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("DestinationFolder")).toList().get(0);
        projectManager.getNodeService().move(folderToMove, destinationFolder);
        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().size());
        assertEquals(true, destinationFolder.getChildren().get(0).isFolder());
    }

    @Test
    public void UpdateFolderProject(){
        Project project = projectManager.load(updatePath);
        Node foldernode = project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().get(0);
        assertEquals(null, projectManager.getNodeService().update(foldernode, 0, 5, "aled".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void UpdateFileProject(){
        Project project = projectManager.load(updatePath);
        Node foldernode = project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().get(0);
        assertNotEquals(null, projectManager.getNodeService().update(foldernode, 4, 6, "Au secours".getBytes(StandardCharsets.UTF_8)));
    }
}
