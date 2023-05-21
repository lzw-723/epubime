package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.bean.MetaDC;

public class EpubFile {

    private ZipFile zipFile;
    private final String container = "META-INF/container.xml";
    private List<MetaDC> metaDCs;

    private List<String> titles = new ArrayList<>();
    private List<String> langs = new ArrayList<>();
    private List<String> ids = new ArrayList<>();

    public EpubFile(File file) throws ZipException, IOException, ParserConfigurationException, SAXException {
        // zip = new ZipInputStream(new FileInputStream(file));
        zipFile = new ZipFile(file);
        init();
    }

    public EpubFile(InputStream stream) {
        // TODO: 从InputStream构造
    }

    public EpubFile(String path) throws ZipException, IOException, ParserConfigurationException, SAXException {
        zipFile = new ZipFile(new File(path));
        init();
    }

    private void init() throws ParserConfigurationException, SAXException, IOException {
        InputStream opf = getInputStream(ContainerReader.getRootFile(getInputStream(container)));
        metaDCs = OpfUtils.getMetaDCs(opf);
        metaDCs.stream().forEach(dc -> {
            String name = dc.getName();
            String content = dc.getContent();

            if ("dc:title".equals(name)) {
                titles.add(content);
            } else if ("dc:language".equals(name)) {
                langs.add(content);
            } else if ("dc:identifier".equals(name)) {
                ids.add(content);
            }
        });
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
        return titles.get(0);
    }

    public String getLanguage() throws DOMException, ParserConfigurationException, SAXException, IOException {
        return langs.get(0);
    }

    public String getIdentifier() throws ParserConfigurationException, SAXException, IOException {
        return ids.get(0);
    }

    public List<String> getTitles() throws DOMException, ParserConfigurationException, SAXException, IOException {
        return titles;
    }

    public List<String> getLanguages() throws DOMException, ParserConfigurationException, SAXException, IOException {
        return langs;
    }

    public List<String> getIdentifiers() throws ParserConfigurationException, SAXException, IOException {
        return ids;
    }
}
