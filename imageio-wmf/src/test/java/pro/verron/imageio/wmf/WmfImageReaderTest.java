package pro.verron.imageio.wmf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/// Test class for validating the functionality of the WMF ImageIO reader implementation.
/// The tests in this class ensure that the WMF ImageIO reader correctly identifies
/// WMF files and extracts metadata, including image dimensions, from the Placeable WMF header.
/// Various assertions verify the accuracy of the extracted metadata and the robustness of
/// the reader against common errors.
/// Key functionality tested:
/// - Proper detection of WMF format from the input data.
/// - Accurate extraction of width and height values from the Placeable WMF header.
/// - Validation of positive, expected dimensions based on a known test WMF file.
/// This test uses JUnit 5 for structured test case organization and descriptive assertions.
/// Tests depend on the existence of required test resources in the expected file paths.
@DisplayName("WMF ImageIO reader metadata tests")
public class WmfImageReaderTest {

    @BeforeAll
    static void beforeAll() {
        var registry = IIORegistry.getDefaultInstance();
        var imageReaderSpi = new WmfImageReaderSpi();
        registry.registerServiceProvider(imageReaderSpi);
    }

    @Test
    @DisplayName("sample.wmf: reader detects format and extracts dimensions from Placeable header")
    void sampleWmf_metadataFromPlaceableHeader() throws Exception {
        var path = Path.of("..", "test", "sample.wmf");
        var file = path.toFile();
        assertTrue(file.exists(), "Test WMF file not found: " + file.getAbsolutePath());

        try (var iis = ImageIO.createImageInputStream(file)) {
            assertNotNull(iis, "ImageInputStream is null");
            var imageReaders = ImageIO.getImageReaders(iis);
            var firstReader = imageReaders.next();
            firstReader.setInput(iis, false, true);
            assertEquals("wmf", firstReader.getFormatName());
            var w = firstReader.getWidth(0);
            var h = firstReader.getHeight(0);
            firstReader.dispose();

            assertTrue(w > 0 && h > 0, "Non-positive dimensions returned");
            // Computed from bounding box (Inch=1000) @96DPI: 8016u x 6756u -> 770x649 px
            assertEquals(770, w);
            assertEquals(649, h);

            firstReader.dispose();
        }
    }
}
