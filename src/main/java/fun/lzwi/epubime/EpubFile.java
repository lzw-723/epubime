package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.PackageDocument;
import fun.lzwi.epubime.util.ContainerUtils;
import fun.lzwi.epubime.util.PackageDocumentUtils;

public class EpubFile {
    private ZipFile zipFile;
    private final static String container = "META-INF/container.xml";

    private PackageDocument packageDocument;

    public PackageDocument getPackageDocument() {
        return (PackageDocument) packageDocument.clone();
    }

    public EpubFile(File file) throws ZipException, IOException, ParserConfigurationException, SAXException {
        zipFile = new ZipFile(file);
        init();
    }

    public EpubFile(String path) {

        try {
            zipFile = new ZipFile(new File(path));
            init();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Epub文件异常", e);
        }
    }

    private void init() throws IOException, ParserConfigurationException, SAXException {
        InputStream opf = getInputStream(
                ContainerUtils.getRootFile(getInputStream(container)));
        packageDocument = PackageDocumentUtils
                .getPackageDocument(PackageDocumentUtils.getPackageElement(opf));
    }

    private InputStream getInputStream(String entry) throws IOException {
        return zipFile.getInputStream(zipFile.getEntry(entry));
    }

}
