package fun.lzwi.epubime.easy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fun.lzwi.epubime.Epub;
import fun.lzwi.epubime.EpubFile;
import fun.lzwi.epubime.EpubReader;

public class EasyEpub {
    private Epub epub;

    public EasyEpub(String path) throws ParserConfigurationException, SAXException, IOException {
        super();
        epub = new EpubReader(new EpubFile(path)).read();
    }

    public EasyEpub(File file) throws ParserConfigurationException, SAXException, IOException {
        super();
        epub = new EpubReader(new EpubFile(file)).read();
    }

    public String getTitle() {
        List<String> titles = epub.getPackageDocument().getMetaData().getDc().getTitles();
        return titles.size() > 0 ? titles.get(0) : null;
    }

    public String getAuthor() {
        List<String> creators = epub.getPackageDocument().getMetaData().getDc().getCreators();
        return creators.size() > 0 ? creators.get(0) : null;
    }

    // TODO: 获取封面
    public void getCover() {
        // return epub.getPackageDocument().getMetaData().getDc().getCoverages().get(0);
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
