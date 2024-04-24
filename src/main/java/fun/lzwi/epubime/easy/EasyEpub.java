package fun.lzwi.epubime.easy;

import java.io.File;
import java.io.IOException;
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
        return epub.getPackageDocument().getMetaData().getDc().getTitles().get(0);
    }

    public String getAuthor() {
        return epub.getPackageDocument().getMetaData().getDc().getCreators().get(0);
    }

    // TODO: 获取封面
    public void getCover() {
        // return epub.getPackageDocument().getMetaData().getDc().getCoverages().get(0);
    }

    public String getIdentifier() {
        return epub.getPackageDocument().getMetaData().getDc().getIdentifiers().get(0);
    }

    public String getLanguage() {
        return epub.getPackageDocument().getMetaData().getDc().getLanguages().get(0);
    }

    public String getModified() {
        return epub.getPackageDocument().getMetaData().getMeta().get("dcterms:modified");
    }
}
