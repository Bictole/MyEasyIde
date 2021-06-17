package fr.epita.assistants.ping.feature;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.any.CleanUp;
import fr.epita.assistants.ping.feature.any.Dist;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class FeatureFactory {

    private enum Types {

        // ANY FEATURES
        DIST(Mandatory.Features.Any.DIST),
        CLEANUP(Mandatory.Features.Any.CLEANUP),
        SEARCH(Mandatory.Features.Any.SEARCH),

        // GIT FEATURES
        ADD(Mandatory.Features.Git.ADD),
        COMMIT(Mandatory.Features.Git.COMMIT),
        PULL(Mandatory.Features.Git.PULL),
        PUSH(Mandatory.Features.Git.PUSH),

        // MAVEN FEATURES
        CLEAN(Mandatory.Features.Maven.CLEAN);

        public final Feature.Type type;

        Types(Feature.Type type) {
            this.type = type;
        }
        private static final Map<Feature.Type, Types> BY_TYPE = new HashMap<>();

        static {
            for (Types types: values()) {
                BY_TYPE.put(types.type, types);
            }
        }

        public static Types typeOfType(Feature.Type type) {
            return BY_TYPE.get(type);
        }
    }

    private static final Hashtable<Feature.Type, Feature> featureTable = new Hashtable<>();

    private FeatureFactory() {}

    public static Feature getFeature(Feature.Type type) {
        var feature = featureTable.get(type);
        return feature == null ? addFeature(type) : feature;
    }

    private static Feature addFeature(Feature.Type type) {
        Feature feature;
        Types enumType = Types.typeOfType(type);
        switch (enumType) {
            case DIST:
                feature = new Dist();
                break;
            case CLEANUP:
                feature = new CleanUp();
                break;
            default:
                feature = null;
        }
        if (feature == null)
            throw new IllegalArgumentException("Given type is not valid");
        featureTable.put(type, feature);
        return feature;
    }
}
