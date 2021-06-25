package fr.epita.assistants.ping.feature;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.any.CleanUp;
import fr.epita.assistants.ping.feature.any.Dist;
import fr.epita.assistants.ping.feature.any.Search;
import fr.epita.assistants.ping.feature.git.Add;
import fr.epita.assistants.ping.feature.git.Commit;
import fr.epita.assistants.ping.feature.git.Pull;
import fr.epita.assistants.ping.feature.git.Push;
import fr.epita.assistants.ping.feature.maven.*;
import fr.epita.assistants.ping.feature.maven.Package;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class FeatureFactory {

    private enum Types {

        // ANY FEATURES
        DIST(Mandatory.Features.Any.DIST, new Dist()),
        CLEANUP(Mandatory.Features.Any.CLEANUP, new CleanUp()),
        SEARCH(Mandatory.Features.Any.SEARCH, new Search()),

        // GIT FEATURES
        ADD(Mandatory.Features.Git.ADD, new Add()),
        COMMIT(Mandatory.Features.Git.COMMIT, new Commit()),
        PULL(Mandatory.Features.Git.PULL, new Pull()),
        PUSH(Mandatory.Features.Git.PUSH, new Push()),

        // MAVEN FEATURES
        CLEAN(Mandatory.Features.Maven.CLEAN, new Clean()),
        COMPILE(Mandatory.Features.Maven.COMPILE, new Compile()),
        EXEC(Mandatory.Features.Maven.EXEC, new Exec()),
        INSTALL(Mandatory.Features.Maven.INSTALL, new Install()),
        PACKAGE(Mandatory.Features.Maven.PACKAGE, new Package()),
        TEST(Mandatory.Features.Maven.TEST, new Test()),
        TREE(Mandatory.Features.Maven.TREE, new Tree());

        public final Feature.Type type;
        public final Feature feature;

        Types(Feature.Type type, Feature feature) {
            this.type = type;
            this.feature = feature;
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
        Types enumType = Types.typeOfType(type);
        Feature feature = enumType.feature;
        if (feature == null)
            throw new IllegalArgumentException("Given type is not valid");
        featureTable.put(type, feature);
        return feature;
    }
}
