package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class EpubFile {
    private final ZipFile zipFile;
    protected final static String CONTAINER_PATH = "META-INF/container.xml";

    // private PackageDocument packageDocument;

    // public PackageDocument getPackageDocument() throws IOException, ParserConfigurationException, SAXException {
    //     if (packageDocument ==null) {
    //         init();
    //     }
    //     return (PackageDocument) packageDocument.clone();
    // }

    public EpubFile(File file) throws IOException {
        zipFile = new ZipFile(file);
        // init();
    }

    // public EpubFile(String path) {

    //     try {
    //         zipFile = new ZipFile(new File(path));
    //         // init();
    //     } catch (IOException | ParserConfigurationException | SAXException e) {
    //         throw new RuntimeException("Epub文件异常", e);
    //     }
    // }

    // private void init() throws IOException, ParserConfigurationException, SAXException {
    //     InputStream opf = getInputStream(
    //             ContainerUtils.getRootFile(getInputStream(container)));
    //     packageDocument = PackageDocumentUtils
    //             .getPackageDocument(PackageDocumentUtils.getPackageElement(opf));
    // }

    protected InputStream getInputStream(String entry) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry(entry));
    }

}
