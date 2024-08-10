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
    private final EpubFile file;

    public EpubReader(EpubFile file) {
        super();
        this.file = file;
    }

    public Epub read() throws ParserConfigurationException, SAXException, IOException {
        Epub epub = new Epub();
        String opf = ContainerUtils.getRootFile(file.getInputStream(EpubConstants.CONTAINER));
        // FUCK NP - NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE
        Path path = Paths.get(opf).getParent();
        String parent = "";
        if (path != null) {
            parent = path + "/";
        }
        PackageDocument packageDocument = new PackageDocumentReader(file.getInputStream(opf), opf).read();
        epub.setPackageDocument(packageDocument);
        String version = packageDocument.getVersion();
        // 仅epub3
        if (EpubConstants.EPUB_VERSION_3.equals(version)) {
            ManifestItem nav =
                    packageDocument.getManifest().getItems().stream().filter(i -> "nav".equals(i.getProperties())).findFirst().get();
            String navHref = parent + nav.getHref();
            NavigationDocReader navigationDocReader = new NavigationDocReader(file.getInputStream(navHref), navHref);
            NavigationDocument navigationDocument = navigationDocReader.read();
            epub.setNavigationDocument(navigationDocument);
        }
        // 部分epub3也兼容ncx
        // ncx文件要求
        // Key NCX Requirements
        // https://idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#TOC2.4.1.2
        ManifestItem ncx =
                packageDocument.getManifest().getItems().stream().filter(i -> EpubConstants.EPUB_2_NCX_MediaType.equals(i.getMediaType())).findFirst().get();
        String entry = parent + ncx.getHref();
        NCXReader reader = new NCXReader(file.getInputStream(entry), entry);
        NCX nav = reader.read();
        epub.setNCX(nav);

        return epub;
    }
}
