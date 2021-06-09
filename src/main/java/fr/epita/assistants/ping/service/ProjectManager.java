package fr.epita.assistants.ping.service;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.aspect.AnyAspect;
import fr.epita.assistants.ping.aspect.GitAspect;
import fr.epita.assistants.ping.aspect.MavenAspect;
import fr.epita.assistants.ping.node.FileNode;
import fr.epita.assistants.ping.node.FolderNode;
import fr.epita.assistants.ping.project.AnyProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ProjectManager implements ProjectService {

    private NodeService nodeService;

    public ProjectManager() {
    }

    private Node createNodes(Path root) {
        File r = root.toFile();
        if (r.isDirectory()) {
            FolderNode folderNode = new FolderNode(root);
            try (Stream<Path> paths = Files.list(root)) {
                for (var p : paths.toList()) {
                    folderNode.addChildren(createNodes(p));
                }
                return folderNode;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return new FileNode(root);
    }

    private void findAspects(Node rootNode, Set<Aspect> aspects) {
        if (aspects.contains(MavenAspect.class) && aspects.contains(GitAspect.class))
            return;

        if (!rootNode.getChildren().isEmpty()) {
            for (var child : rootNode.getChildren()) {
                findAspects(child, aspects);
            }
        }
        System.out.println(rootNode.getPath());
        if (rootNode.isFile() && rootNode.getPath().toString().endsWith("/pom.xml")) {
            if (!aspects.contains(MavenAspect.class)) {
                aspects.add(new MavenAspect());
            }
        }
        if (rootNode.isFolder() && rootNode.getPath().toString().endsWith("/.git")) {
            if (!aspects.contains(GitAspect.class)) {
                aspects.add(new GitAspect());
            }
        }
    }

    @Override
    public Project load(Path root) {
        Node rootNode = createNodes(root);
        Set<Aspect> aspects = new HashSet<>();
        aspects.add(new AnyAspect());
        findAspects(rootNode, aspects);
        return new AnyProject(rootNode, aspects);
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        Optional<Feature> f = project.getFeature(featureType);
        if (f.isPresent()) {
            return f.get().execute(project, params);
        }
        return null;
    }

    @Override
    public NodeService getNodeService() {
        return nodeService;
    }
}
