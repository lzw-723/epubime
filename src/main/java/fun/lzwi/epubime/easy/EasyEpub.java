package fun.lzwi.epubime.easy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        String opf = epub.getPackageDocument().getHref();
        try {
            String parent = EntryPathUtils.parent(opf);
            String cover;
            List<String> coverages = epub.getPackageDocument().getMetaData().getDc().getCoverages();
            Optional<String> c = coverages.stream().filter(s -> s.length() > 0).findFirst();
            if (c.isPresent()) {
                cover = c.get();
            } else {
                String coverId = epub.getPackageDocument().getMetaData().getMeta().get("cover");
                cover = epub.getPackageDocument().getManifest().getItemById(coverId).getHref();
            }
            return EntryPathUtils.parse(parent, cover);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Resource getResource(String href) {
        return new Resource(epubFile).setHref(href);
    }

    public String getHref(String entry) {
        Resource opf = new Resource(epubFile).setHref(epub.getPackageDocument().getHref());
        return new Resource(opf, entry).getPath();
    }

    public String getIdentifier() {
        List<String> identifiers = epub.getPackageDocument().getMetaData().getDc().getIdentifiers();
        return identifiers.size() > 0 ? identifiers.get(0) : null;
    }

    public String getLanguage() {
        List<String> languages = epub.getPackageDocument().getMetaData().getDc().getLanguages();
        return languages.size() > 0 ? languages.get(0) : null;
    }

    public String getDescription() {
        List<String> descriptions = epub.getPackageDocument().getMetaData().getDc().getDescriptions();
        return descriptions.size() > 0 ? descriptions.get(0) : null;
    }

    public String getDate() {
        List<String> dates = epub.getPackageDocument().getMetaData().getDc().getDates();
        return dates.size() > 0 ? dates.get(0) : null;
    }

    public String getModified() {
        return epub.getPackageDocument().getMetaData().getMeta().get("dcterms:modified");
    }

    public List<EasyResource> getResources() {
        return epub.getPackageDocument().getManifest().getItems().stream()
                .map(item -> new EasyResource(getHref(item.getHref()), item.getMediaType()))
                .collect(Collectors.toList());
    }

    /**
     * EasyResource
     */
    public static class EasyResource {
        private String href;
        private String type;

        /**
         * @param href
         * @param type
         */
        public EasyResource(String href, String type) {
            this.href = href;
            this.type = type;
        }

        /**
         * @return the href
         */
        public String getHref() {
            return href;
        }

        /**
         * @param href the href to set
         */
        public void setHref(String href) {
            this.href = href;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }

    }
}
