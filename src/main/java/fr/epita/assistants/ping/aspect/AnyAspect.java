package fr.epita.assistants.ping.aspect;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.any.Search;

import java.util.ArrayList;
import java.util.List;

public class AnyAspect implements Aspect {

    private List<Feature> featureList = new ArrayList<>();

    public AnyAspect() {
        featureList.add(new Search());
    }

    @Override
    public Type getType() {
        return Mandatory.Aspects.ANY;
    }

    @Override
    public List<Feature> getFeatureList() {
        return featureList;
    }
}
