package fr.epita.assistants.ping.aspect;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;

import java.util.ArrayList;
import java.util.List;

public class MavenAspect implements Aspect {

    private List<Feature> featureList = new ArrayList<>();

    public MavenAspect() {
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public List<Feature> getFeatureList() {
        return Aspect.super.getFeatureList();
    }
}
