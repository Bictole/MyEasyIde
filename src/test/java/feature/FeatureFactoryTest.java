package feature;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.feature.FeatureFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FeatureFactoryTest {

    @Test
    void getFeatureTestType() {
        Feature cleanup = FeatureFactory.getFeature(Mandatory.Features.Any.CLEANUP);
        Assertions.assertEquals(cleanup.type(), Mandatory.Features.Any.CLEANUP);
    }

    @Test
    void getFeatureTestSingleton() {
        Feature cleanup = FeatureFactory.getFeature(Mandatory.Features.Any.CLEANUP);
        Feature cleanup2 = FeatureFactory.getFeature(Mandatory.Features.Any.CLEANUP);
        Assertions.assertEquals(cleanup, cleanup2);
    }
}