import pro.verron.imageio.svg.SvgImageReaderSpi;

module pro.verron.imageio.svg {
    requires java.desktop;
    provides javax.imageio.spi.ImageReaderSpi with SvgImageReaderSpi;
}
