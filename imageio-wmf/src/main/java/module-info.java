module pro.verron.imageio.wmf {
    requires java.desktop;
    provides javax.imageio.spi.ImageReaderSpi with pro.verron.imageio.wmf.WmfImageReaderSpi;
}
