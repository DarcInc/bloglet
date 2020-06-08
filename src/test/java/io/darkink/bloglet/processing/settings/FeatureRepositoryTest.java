package io.darkink.bloglet.processing.settings;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FeatureRepositoryTest {

    @Value("${io.darkink.bloglet.processor.watchDir}")
    private String watchPath;

    @Test
    public void shouldReturnTheFeaturesAtAPath() {
        FeatureRepository featureRepository = new FeatureRepository();

        featureRepository.save(new Feature("mayhem"), Paths.get(watchPath));

        Feature found = featureRepository.find("mayhem", Paths.get(watchPath));
        assertEquals("mayhem", found.getName());
    }

    @Test
    public void shouldGetParentSetting() {
        FeatureRepository featureRepository = new FeatureRepository();

        featureRepository.save(new Feature("mayhem"), Paths.get(watchPath));

        Feature found = featureRepository.find("mayhem", Paths.get(watchPath, "subdir"));
        assertEquals("mayhem", found.getName());
    }
}
