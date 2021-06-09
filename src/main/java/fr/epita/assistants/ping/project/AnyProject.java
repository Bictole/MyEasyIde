package fr.epita.assistants.ping.project;

import fr.epita.assistants.myide.domain.entity.*;
import org.eclipse.jgit.api.Git;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AnyProject implements Project {

    private final Node rootNode;
    private final Set<Aspect> aspects;
    private Git git = null;

    public AnyProject(Node root, Set<Aspect> aspects)
    {
        rootNode = root;
        this.aspects = aspects;

        for (var a : aspects)
        {
            if (a.getType() == Mandatory.Aspects.GIT)
            {
                try {
                    git = Git.open(new File(String.valueOf(root.getPath())));
                }
                catch (Exception e)
                {
                    System.out.println("Open Git Failed");
                }
            }

        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public Set<Aspect> getAspects() {
        return aspects;
    }

    @Override
    public Optional<Feature> getFeature(Feature.Type featureType) {
        List<Feature> features = this.getFeatures();
        for (Feature f : features)
        {
            if (f.type().equals(featureType))
            {
                return Optional.of(f);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<@NotNull Feature> getFeatures() {
        return Project.super.getFeatures();
    }

    public Git getgit()
    {
        return git;
    }
}
