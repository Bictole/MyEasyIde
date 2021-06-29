package fr.epita.assistants.ping.aspect;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.FeatureFactory;
import fr.epita.assistants.ping.feature.maven.Package;
import fr.epita.assistants.ping.feature.maven.*;

import java.util.ArrayList;
import java.util.List;

public class MavenAspect implements Aspect {

    private List<Feature> featureList = new ArrayList<>();

    public MavenAspect() {
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.CLEAN));
        featureList.add(new Compile());
        featureList.add(new Install());
        featureList.add(new Package());
        featureList.add(new Test());
        featureList.add(new Exec());
        featureList.add(new Tree());
    }

    @Override
    public Type getType() {
        return Mandatory.Aspects.MAVEN;
    }

    @Override
    public List<Feature> getFeatureList() {
        return featureList;
    }
}

/*
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.CLEAN));
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.COMPILE));
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.EXEC));
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.INSTALL));
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.PACKAGE));
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.TEST));
                featureList.add(FeatureFactory.getFeature(Mandatory.Features.Maven.TREE));
*/