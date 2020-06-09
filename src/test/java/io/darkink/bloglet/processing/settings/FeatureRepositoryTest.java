package io.darkink.bloglet.processing.settings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FeatureRepositoryTest {

    @Value("${io.darkink.bloglet.processor.watchDir}")
    private String watchPath;

    @Autowired
    private FeatureRepository featureRepository;

    @Test
    public void shouldReturnTheFeaturesAtAPath() {
        featureRepository.save(new Feature("mayhem"), Paths.get(watchPath));

        Optional<Feature> found = featureRepository.find("mayhem", Paths.get(watchPath));
        assertTrue(found.isPresent());
        assertEquals("mayhem", found.get().getName());
    }

    @Test
    public void shouldGetParentSetting() {
        featureRepository.save(new Feature("chaos"), Paths.get(watchPath));

        Optional<Feature> found = featureRepository.find("chaos", Paths.get(watchPath, "subdir"));
        assertTrue(found.isPresent());
        assertEquals("chaos", found.get().getName());
    }

    @Test
    public void shouldMergeParentSettings() {
        Feature rootAnarchy = new Feature("anarchy");
        rootAnarchy.put("setting1", "value1");
        rootAnarchy.put("setting2", "value2");
        rootAnarchy.put("setting4", "value4");
        rootAnarchy.put("setting5", "value5");

        featureRepository.save(rootAnarchy, Paths.get(watchPath));

        Feature middleAnarchy = new Feature("anarchy");
        middleAnarchy.put("setting1", "override1");
        middleAnarchy.put("setting3", "value3");
        middleAnarchy.put("setting5", "override3");

        featureRepository.save(middleAnarchy, Paths.get(watchPath, "middle"));

        Feature lowAnarchy = new Feature("anarchy");
        lowAnarchy.put("setting4", "override2");
        lowAnarchy.put("setting6", "value6");
        lowAnarchy.put("setting5", "override4");

        featureRepository.save(lowAnarchy, Paths.get(watchPath, "middle", "notlow", "low"));

        Optional<Feature> anarchy = featureRepository.find("anarchy", Paths.get(watchPath, "middle"));
        assertTrue(anarchy.isPresent());
        assertEquals("override1", anarchy.get().get("setting1"));
        assertEquals("value2", anarchy.get().get("setting2"));
        assertEquals("value4", anarchy.get().get("setting4"));
        assertEquals("override3", anarchy.get().get("setting5"));
        assertEquals("value3", anarchy.get().get("setting3"));

        anarchy = featureRepository.find("anarchy", Paths.get(watchPath, "middle", "notlow"));
        assertEquals("override1", anarchy.get().get("setting1"));
        assertEquals("value2", anarchy.get().get("setting2"));
        assertEquals("value4", anarchy.get().get("setting4"));
        assertEquals("override3", anarchy.get().get("setting5"));
        assertEquals("value3", anarchy.get().get("setting3"));

        anarchy = featureRepository.find("anarchy", Paths.get(watchPath, "middle", "notlow", "low"));
        assertEquals("override1", anarchy.get().get("setting1"));
        assertEquals("value2", anarchy.get().get("setting2"));
        assertEquals("value3", anarchy.get().get("setting3"));
        assertEquals("override2", anarchy.get().get("setting4"));
        assertEquals("override4", anarchy.get().get("setting5"));
        assertEquals("value3", anarchy.get().get("setting3"));
        assertEquals("value6", anarchy.get().get("setting6"));

    }
}
