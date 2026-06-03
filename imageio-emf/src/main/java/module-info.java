import pro.verron.imageio.emf.EmfImageReaderSpi;

module pro.verron.imageio.emf {
    requires java.desktop;
    provides javax.imageio.spi.ImageReaderSpi with EmfImageReaderSpi;
}
