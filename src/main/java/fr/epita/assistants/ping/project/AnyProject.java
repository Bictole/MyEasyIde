package fr.epita.assistants.ping.project;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.ping.aspect.AnyAspect;

import javax.validation.constraints.NotNull;
import java.util.*;

public class AnyProject implements Project {

    private final Node rootNode;
    private final Set<Aspect> aspects = new HashSet<>();

    public AnyProject(Node root)
    {
        rootNode = root;
        aspects.add(new AnyAspect());
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
            if (f.type() == featureType)
                return Optional.of(f);
        }
        return Optional.empty();
    }

    @Override
    public List<@NotNull Feature> getFeatures() {
        return Project.super.getFeatures();
    }
}
