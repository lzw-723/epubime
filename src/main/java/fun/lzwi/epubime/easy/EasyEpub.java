package fun.lzwi.epubime.easy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fun.lzwi.epubime.Epub;
import fun.lzwi.epubime.EpubFile;
import fun.lzwi.epubime.EpubReader;
import fun.lzwi.epubime.Resource;
import fun.lzwi.epubime.util.EntryPathUtils;

public class EasyEpub {
    private EpubFile epubFile;
    private Epub epub;

    public EasyEpub(String path) throws ParserConfigurationException, SAXException, IOException {
        super();
        epubFile = new EpubFile(path);
        epub = new EpubReader(epubFile).read();
    }

    public EasyEpub(File file) throws ParserConfigurationException, SAXException, IOException {
        super();
        epubFile = new EpubFile(file);
        epub = new EpubReader(epubFile).read();
    }

    public String getTitle() {
        List<String> titles = epub.getPackageDocument().getMetaData().getDc().getTitles();
        return titles.size() > 0 ? titles.get(0) : null;
    }

    public String getAuthor() {
        List<String> creators = epub.getPackageDocument().getMetaData().getDc().getCreators();
        return creators.size() > 0 ? creators.get(0) : null;
    }

    public String getCover() {
        List<String> coverages = epub.getPackageDocument().getMetaData().getDc().getCoverages();
        if (coverages.size() > 0) {
            return coverages.get(0);
        }
        String coverId = epub.getPackageDocument().getMetaData().getMeta().get("cover");
        String cover = epub.getPackageDocument().getManifest().getItemById(coverId).getHref();
        try {
            return EntryPathUtils.parse(EntryPathUtils.parent(epub.getPackageDocument().getHref()), cover);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource getResource(String href) {
        return new Resource(epubFile).setHref(href);
    }

    public String getIdentifier() {
        List<String> identifiers = epub.getPackageDocument().getMetaData().getDc().getIdentifiers();
        return identifiers.size() > 0 ? identifiers.get(0) : null;
    }

    public String getLanguage() {
        List<String> languages = epub.getPackageDocument().getMetaData().getDc().getLanguages();
        return languages.size() > 0 ? languages.get(0) : null;
    }

    public String getModified() {
        return epub.getPackageDocument().getMetaData().getMeta().get("dcterms:modified");
    }
}