package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class EpubFile {

    private ZipFile zipFile;
    // private ZipInputStream zip;
    private InputStream container;
    private InputStream opf;

    public EpubFile(File file) throws ZipException, IOException, ParserConfigurationException, SAXException {
        // zip = new ZipInputStream(new FileInputStream(file));
        zipFile = new ZipFile(file);
        container = getInputStream("META-INF/container.xml");
        opf = getInputStream(ContainerReader.getRootFile(container));
    }

    public EpubFile(InputStream stream) {
        // TODO: 从InputStream构造
    }

    public EpubFile(String path) {
        // TODO: 从path构造
    }

    public EpubFile forEach(Consumer<ZipEntry> e) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            e.accept(entry);
        }
        return this;
    }

    protected ZipEntry getEntry(String name) {
        return zipFile.getEntry(name);
    }

    protected InputStream getInputStream(String entry) throws IOException {
        return zipFile.getInputStream(getEntry(entry));
    }

    public String getTitle() throws DOMException, ParserConfigurationException, SAXException, IOException {
        return OpfReader.getTitle(opf);
    }

    public String getLanguage() throws DOMException, ParserConfigurationException, SAXException, IOException {
        return OpfReader.getLanguage(opf);
    }

    public String getIdentifier() throws ParserConfigurationException, SAXException, IOException {
        return OpfReader.getIdentifier(opf);
    }
}
