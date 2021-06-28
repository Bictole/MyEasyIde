package fr.epita.assistants.ping.aspect;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.FeatureFactory;
import fr.epita.assistants.ping.feature.any.CleanUp;
import fr.epita.assistants.ping.feature.any.Dist;
import fr.epita.assistants.ping.feature.any.Search;

import java.util.ArrayList;
import java.util.List;

public class AnyAspect implements Aspect {

    private List<Feature> featureList = new ArrayList<>();

    public AnyAspect() {
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Any.SEARCH));
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Any.DIST));
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Any.CLEANUP));
        featureList.add(FeatureFactory.getFeature(Mandatory.Features.Any.RUN));
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
