package fr.epita.assistants.ping.aspect;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.git.*;

import java.util.ArrayList;
import java.util.List;

public class GitAspect implements Aspect {

    private List<Feature> featureList = new ArrayList<>();

    public GitAspect() {
        featureList.add(new Add());
        featureList.add(new Commit());
        featureList.add(new Pull());
        featureList.add(new Push());
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
