package fun.lzwi.epubime;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.NCX;
import fun.lzwi.epubime.document.NCXReader;
import fun.lzwi.epubime.document.NavigationDocReader;
import fun.lzwi.epubime.document.NavigationDocument;
import fun.lzwi.epubime.document.PackageDocument;
import fun.lzwi.epubime.document.PackageDocumentReader;
import fun.lzwi.epubime.document.section.element.ManifestItem;
import fun.lzwi.epubime.util.ContainerUtils;

public class EpubReader {
    private EpubFile file;

    public EpubReader(EpubFile file) {
        super();
        this.file = file;
    }

    public Epub read() throws ParserConfigurationException, SAXException, IOException {
        Epub epub = new Epub();
        String opf = ContainerUtils.getRootFile(file.getInputStream(EpubFile.CONTAINER_PATH));
        // FUCK NP - NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE
        Path path = Paths.get(opf).getParent();
        String parent = "";
        if (path != null) {
            parent = path.toString();
        }
        PackageDocument packageDocument = new PackageDocumentReader(file.getInputStream(opf)).read();
        epub.setPackageDocument(packageDocument);
        String version = packageDocument.getVersion();
        if (EpubConstants.EPUB_VERSION_3.equals(version)) {
            ManifestItem nav = packageDocument.getManifest().getItems().stream().filter(i -> "nav".equals(i.getProperties())).findFirst().get();
            NavigationDocReader navigationDocReader = new NavigationDocReader(file.getInputStream(parent + "/" + nav.getHref()));
            NavigationDocument navigationDocument;
            navigationDocument = navigationDocReader.read();
            epub.setNavigationDocument(navigationDocument);
        } else if (EpubConstants.EPUB_VERSION_2.equals(version)) {
            // ncx文件要求
            // Key NCX Requirements
            // https://idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#TOC2.4.1.2
            ManifestItem ncx = packageDocument.getManifest().getItems().stream().filter(i -> EpubConstants.EPUB_2_NCX_MediaType.equals(i.getMediaType())).findFirst().get();
            NCXReader reader = new NCXReader(file.getInputStream(parent + "/" + ncx.getHref()));
            NCX nav = reader.read();
            epub.setNCX(nav);
        }

        return epub;
    }
}
