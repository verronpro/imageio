package pro.verron.imageio.emf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static pro.verron.imageio.emf.utils.TestUtils.error;
import static pro.verron.imageio.emf.utils.TestUtils.findChild;

/// Test class for validating the functionality of the EMF ImageIO reader.
/// This class includes tests to ensure that the reader correctly identifies EMF
/// image files, reads the format metadata, and verifies image dimensions against
/// expected values. It also ensures that the ImageInputStream is properly initialized and
/// integrates with the ImageIO framework as intended.
/// The tests use the \`EmfImageReaderSpi\` service provider to register the EMF reader
/// dynamically and validate its integration with the ImageIO API.
/// Key aspects tested include:
/// - Format detection of EMF images.
/// - Validation of non-null and valid dimensions.
/// - Comparison of actual dimensions with expected values.
@DisplayName("EMF ImageIO reader metadata tests")
public class EmfImageReaderTest {

    @BeforeAll
    static void beforeAll() {
        var registry = IIORegistry.getDefaultInstance();
        var imageReaderSpi = new EmfImageReaderSpi();
        registry.registerServiceProvider(imageReaderSpi);
    }

    @Test
    @DisplayName("sample.emf: reader detects format and matches FreeHEP dimensions")
    void sampleCatEmf_metadataMatchesFreeHEP() throws Exception {
        var emfPath = Path.of("..", "test", "sample.emf");
        var emfFile = emfPath.toFile();
        assertTrue(emfFile.exists(), "Test EMF file not found: " + emfFile.getAbsolutePath());

        try (var iis = ImageIO.createImageInputStream(emfFile)) {
            assertNotNull(iis, "ImageInputStream is null");
            var imageReaders = ImageIO.getImageReaders(iis);
            var firstReader = imageReaders.next();
            firstReader.setInput(iis, false, true);
            assertEquals("emf", firstReader.getFormatName());
            var w = firstReader.getWidth(0);
            var h = firstReader.getHeight(0);
            firstReader.dispose();

            assertTrue(w > 0 && h > 0, "Non-positive dimensions returned");
            assertEquals(5716, w, "Width should match expectations");
            assertEquals(1511, h, "Height should match expectations");

            firstReader.dispose();
        }
    }

    @Test
    @DisplayName("sample.emf: standard metadata contains Dimension with HorizontalPixelSize/VerticalPixelSize")
    void sampleCatEmf_standardMetadata_hasDimensionNode() throws Exception {
        var emfPath = Path.of("..", "test", "sample.emf");
        var emfFile = emfPath.toFile();
        assertTrue(emfFile.exists(), "Test EMF file not found: " + emfFile.getAbsolutePath());

        try (var iis = ImageIO.createImageInputStream(emfFile)) {
            assertNotNull(iis, "ImageInputStream is null");

            var imageReaders = ImageIO.getImageReaders(iis);
            var firstReader = imageReaders.next();
            firstReader.setInput(iis, false, true);

            // Sanity check: format and dimensions
            assertEquals("emf", firstReader.getFormatName());
            var expectedW = firstReader.getWidth(0);
            var expectedH = firstReader.getHeight(0);

            var metadata = firstReader.getImageMetadata(0);
            assertNotNull(metadata, "Image metadata must not be null");
            assertTrue(metadata.isStandardMetadataFormatSupported(), "Standard metadata format should be supported");

            var root = metadata.getAsTree("javax_imageio_1.0");
            assertNotNull(root, "Standard metadata tree should not be null");

            var dimension = findChild(root, "Dimension") //
                    .orElseThrow(error("Metadata must contain a Dimension node"));

            // The standard metadata exposes screen size in pixels.
            // Verify HorizontalScreenSize/VerticalScreenSize are present and match the reader-reported pixel dimensions.
            var hss = findChild(dimension, "HorizontalScreenSize")//
                    .orElseThrow(error("Dimension must contain HorizontalScreenSize when pixel dimensions are known"));
            var vss = findChild(dimension, "VerticalScreenSize") //
                    .orElseThrow(error("Dimension must contain VerticalScreenSize when pixel dimensions are known"));

            var hssAttr = hss.getAttributes().getNamedItem("value").getNodeValue();
            var vssAttr = vss.getAttributes().getNamedItem("value").getNodeValue();
            assertDoesNotThrow(() -> Integer.parseInt(hssAttr), "HorizontalScreenSize 'value' must be an integer");
            assertDoesNotThrow(() -> Integer.parseInt(vssAttr), "VerticalScreenSize 'value' must be an integer");
            assertEquals(expectedW, Integer.parseInt(hssAttr), "HorizontalScreenSize should match image width");
            assertEquals(expectedH, Integer.parseInt(vssAttr), "VerticalScreenSize should match image height");

            firstReader.dispose();
        }
    }
}
