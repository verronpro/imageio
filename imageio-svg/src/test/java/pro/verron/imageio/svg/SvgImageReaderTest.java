package pro.verron.imageio.svg;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/// Unit tests for verifying the functionality of the `SvgImageReader` implementation.
/// These tests focus on reading SVG format metadata using `ImageIO` and ensuring that
/// the reader correctly identifies the format and extracts the width and height attributes
/// from the SVG file.
@DisplayName("SVG ImageIO reader metadata tests")
public class SvgImageReaderTest {

    @BeforeAll
    static void beforeAll() {
        var registry = IIORegistry.getDefaultInstance();
        var imageReaderSpi = new SvgImageReaderSpi();
        registry.registerServiceProvider(imageReaderSpi);
    }

    @Test
    @DisplayName("sample.svg: reader detects format and extracts width/height attributes")
    void sampleSvg_dimensionsFromAttributes() throws Exception {
        var path = Path.of("..", "test", "sample.svg");
        var file = path.toFile();
        assertTrue(file.exists(), "Test SVG file not found: " + file.getAbsolutePath());

        try (var iis = ImageIO.createImageInputStream(file)) {
            assertNotNull(iis, "ImageInputStream is null");
            var imageReaders = ImageIO.getImageReaders(iis);
            var firstReader = imageReaders.next();
            firstReader.setInput(iis, false, true);
            assertEquals("svg", firstReader.getFormatName());
            var w = firstReader.getWidth(0);
            var h = firstReader.getHeight(0);
            firstReader.dispose();

            assertEquals(100, w);
            assertEquals(100, h);

            firstReader.dispose();
        }
    }
}
