package service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.service.ProjectManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeServiceTest {

    private ProjectManager projectManager;

    @BeforeEach
    public void setUp() {
        projectManager = new ProjectManager();
        try {
            FileUtils.cleanDirectory(Path.of("ProjectTests/CreateProject").toFile());
            FileUtils.cleanDirectory(Path.of("ProjectTests/MoveProject/DestinationFolder").toFile());
            if (Files.notExists(Path.of("ProjectTests/DeleteProject/ToDeleteFile.txt")))
                Files.createFile(Path.of("ProjectTests/DeleteProject/ToDeleteFile.txt"));
            if (Files.notExists(Path.of("ProjectTests/DeleteProject/ToDeleteFolder")))
                Files.createDirectory(Path.of("ProjectTests/DeleteProject/ToDeleteFolder"));
            if (Files.notExists(Path.of("ProjectTests/MoveProject/ToMoveFolder")))
                Files.createDirectory(Path.of("ProjectTests/MoveProject/ToMoveFolder"));
            if (Files.notExists(Path.of("ProjectTests/MoveProject/ToMoveFile.txt")))
                Files.createFile(Path.of("ProjectTests/MoveProject/ToMoveFile.txt"));
        } catch (FileAlreadyExistsException fe) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CreateFileTest() {
        Project project = projectManager.load(Path.of("ProjectTests/CreateProject"));
        projectManager.getNodeService().create(project.getRootNode(), "CreateFileTest.txt", Node.Types.FILE);
        assertEquals(1, project.getRootNode().getChildren().size());
    }

    @Test
    public void CreateFolderTest() {
        Project project = projectManager.load(Path.of("ProjectTests/CreateProject"));
        projectManager.getNodeService().create(project.getRootNode(), "CreateFolder", Node.Types.FOLDER);
        assertEquals(1, project.getRootNode().getChildren().size());
    }

    @Test
    public void DeleteFileProject() {
        Project project = projectManager.load(Path.of("ProjectTests/DeleteProject"));
        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().size());
        projectManager.getNodeService().delete(project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().get(0));
        assertEquals(0, project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().size());
    }

    @Test
    public void DeleteFolderProject() {
        Project project = projectManager.load(Path.of("ProjectTests/DeleteProject"));

        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
        projectManager.getNodeService().delete(project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().get(0));
        assertEquals(0, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
    }

    @Test
    public void MoveFileProject() {
        Project project = projectManager.load(Path.of("ProjectTests/MoveProject"));

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
        Project project = projectManager.load(Path.of("ProjectTests/MoveProject"));
        assertEquals(3, project.getRootNode().getChildren().size());
        FolderNode folderToMove = (FolderNode) project.getRootNode().getChildren().stream().filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("ToMoveFolder")).toList().get(0);
        FolderNode destinationFolder = (FolderNode) project.getRootNode().getChildren().stream().filter(node -> node.isFolder() && node.getPath().toFile().getName().equals("DestinationFolder")).toList().get(0);
        projectManager.getNodeService().move(folderToMove, destinationFolder);
        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFolder()).toList().size());
        assertEquals(1, project.getRootNode().getChildren().stream().filter(node -> node.isFile()).toList().size());
        assertEquals(true, destinationFolder.getChildren().get(0).isFolder());
    }
}
