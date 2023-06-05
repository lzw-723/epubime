package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fun.lzwi.epubime.bean.MetaDC;
import fun.lzwi.epubime.bean.MetaItem;

public class EpubFile {
    Logger logger = Logger.getLogger(EpubFile.class.getName());

    private ZipFile zipFile;
    private final static String container = "META-INF/container.xml";

    private List<String> titles;
    private List<String> langs;
    private List<String> ids;
    private Node pkg;
    private Node metaData;
    private List<MetaItem> metaDataItems;
    private List<MetaDC> metaDCs;

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
        InputStream opf = getInputStream(ContainerReader.getRootFile(getInputStream(container)));
        pkg = OpfUtils.getPackage(opf);
        metaData = OpfUtils.getMetaData(pkg);
        metaDataItems = OpfUtils.getMetaDataItems(metaData);
        metaDCs = OpfUtils.getMetaDCs(metaDataItems);
        ids = OpfUtils.getIdentifiers(metaDCs);
        titles = OpfUtils.getTitles(metaDCs);
        langs = OpfUtils.getLanguages(metaDCs);
    }

    protected ZipEntry getEntry(String name) {
        return zipFile.getEntry(name);
    }

    protected InputStream getInputStream(String entry) throws IOException {
        return zipFile.getInputStream(getEntry(entry));
    }

    public String getTitle() {
        return titles.get(0);
    }

    public String getLanguage() {
        return langs.get(0);
    }

    public String getIdentifier() {
        return ids.get(0);
    }

    public List<String> getTitles() {
        return titles.stream().collect(Collectors.toList());
    }

    public List<String> getLanguages() {
        return langs.stream().collect(Collectors.toList());
    }

    public List<String> getIdentifiers() {
        return ids.stream().collect(Collectors.toList());
    }
}
