package io.darkink.bloglet.processing.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SettingServiceTest {

    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private SettingsService settingsService;

    @Test
    public void testFeatureNames() {
        when(featureRepository.allFeatures()).thenReturn(Arrays.asList("foo", "bar", "baz"));

        List<String> featureList = settingsService.listFeatures();
        assertEquals(3, featureList.size());
    }

}
