package fr.epita.assistants.ping.aspect;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.FeatureFactory;
import fr.epita.assistants.ping.feature.git.*;

import java.util.ArrayList;
import java.util.List;

public class GitAspect implements Aspect {

    private List<Feature> featureList = new ArrayList<>();

    public GitAspect() {
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Git.ADD));
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Git.COMMIT));
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Git.PULL));
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Git.PUSH));
    }

    @Override
    public Type getType() {
        return Mandatory.Aspects.GIT;
    }

    @Override
    public List<Feature> getFeatureList() {
        return featureList;
    }
}
