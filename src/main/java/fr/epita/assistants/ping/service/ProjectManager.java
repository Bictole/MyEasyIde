package fr.epita.assistants.ping.service;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.Tools;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ProjectManager implements ProjectService {

    private NodeService nodeService;

    public ProjectManager() {
        nodeService = new NodeManager();
    }



    public void initNodes(Node root) {
        if (root.isFolder()) {
            try (Stream<Path> paths = Files.list(root.getPath())) {
                for (var p : paths.toList()) {
                    Node node = null;
                    if (p.toFile().isDirectory()) {
                        node = new FolderNode(p, root);
                        initNodes(node);
                    } else
                        node = new FileNode(p, root);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTree(Node root) {
        if (root.isFolder()) {
            try {
                Stream<Path> paths = Files.list(root.getPath());
                var pathList = paths.toList();
                for (var p : pathList) {
                    if (root.getChildren().stream().noneMatch(node -> node.getPath().equals(p))) {
                        Node node = null;
                        if (p.toFile().isDirectory()) {
                            node = new FolderNode(p, root);
                            updateTree(node);
                        } else
                            node = new FileNode(p, root);
                    } else {
                        if (p.toFile().isDirectory()) {
                            Node node = ((NodeManager) nodeService).getFromSource(root, p);
                            updateTree(node);
                        }
                    }
                }
                if (pathList.size() < root.getChildren().size())
                    for (int i = 0; i < root.getChildren().size(); i++) {
                        var child = root.getChildren().get(i);
                        if (pathList.stream().noneMatch(path -> path.equals(child.getPath()))) {
                            NodeManager nM = (NodeManager) nodeService;
                            nM.deleteNode(child);
                            i--;
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void findAspects(Node rootNode, Set<Aspect> aspects) {
        if (!rootNode.getChildren().isEmpty()) {
            for (var child : rootNode.getChildren()) {
                if (aspects.contains(MavenAspect.class) && aspects.contains(GitAspect.class))
                    return;

                //Pour les Path Linux : / et les Path Windows : \
                if (child.isFile() && child.getPath().toString().endsWith(File.separator + "pom.xml"))
                {
                    if (!aspects.contains(MavenAspect.class)) {
                        aspects.add(new MavenAspect());
                    }
                }

                if (child.isFolder() && child.getPath().toString().endsWith(File.separator +".git")) {
                    if (!aspects.contains(GitAspect.class)) {
                        aspects.add(new GitAspect());
                    }
                }
            }
        }
    }

    @Override
    public Project load(Path root) {
        if (!root.toFile().isDirectory()) {
            System.err.println("Root is not a folder");
            return null;
        }
        Node rootNode = new FolderNode(root, null);
        initNodes(rootNode);
        ((NodeManager) nodeService).setRootNode(rootNode);
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
