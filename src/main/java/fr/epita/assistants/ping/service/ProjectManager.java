package fr.epita.assistants.ping.service;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.project.AnyProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class ProjectManager implements ProjectService {

    private NodeService nodeService;

    public ProjectManager() {
    }

    private Node createNodes(Path root) {
        File r = root.toFile();
        if (r.isDirectory()) {
            FolderNode folderNode = new FolderNode(root);
            try (Stream<Path> paths = Files.walk(root)) {
                paths.forEach(path -> folderNode.addChildren(createNodes(path)));
                return folderNode;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return new FileNode(root);
    }

    @Override
    public Project load(Path root) {
        Node root_node = createNodes(root);
        return new AnyProject(root_node);
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        Optional<Feature> f = project.getFeature(featureType);
        if (f.isPresent())
            return f.get().execute(project, params);
        return null;
    }

    @Override
    public NodeService getNodeService() {
        return nodeService;
    }
}
