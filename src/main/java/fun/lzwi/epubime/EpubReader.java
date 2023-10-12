package fun.lzwi.epubime;

import fun.lzwi.epubime.document.*;
import fun.lzwi.epubime.document.section.element.ManifestItem;
import fun.lzwi.epubime.util.ContainerUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EpubReader {
    private EpubFile file;

    public EpubReader(EpubFile file) {
        super();
        this.file = file;
    }

    public Epub read() throws ParserConfigurationException, SAXException, IOException {
        Epub epub = new Epub();
        String opf = ContainerUtils.getRootFile(file.getInputStream(EpubFile.CONTAINER_PATH));
        // epub内部根目录
        String parent = Paths.get(opf).getParent().toString();
        PackageDocument packageDocument = new PackageDocumentReader(file.getInputStream(opf)).read();
        epub.setPackageDocument(packageDocument);
        String version = packageDocument.getVersion();
        if (EpubConstants.EPUB_VERSION_3.equals(version)) {
            ManifestItem nav = packageDocument.getManifest().getItems().stream()
                    .filter(i -> "nav".equals(i.getProperties()))
                    .findFirst().get();
            NavigationDocReader navigationDocReader = new NavigationDocReader(
                    file.getInputStream(parent + "/" + nav.getHref()));
            NavigationDocument navigationDocument;
            navigationDocument = navigationDocReader.read();
            epub.setNavigationDocument(navigationDocument);
        } else if (EpubConstants.EPUB_VERSION_2.equals(version)) {
            // ncx文件要求
            // Key NCX Requirements
            // https://idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#TOC2.4.1.2
            ManifestItem ncx = packageDocument.getManifest().getItems().stream()
                    .filter(i -> EpubConstants.EPUB_2_NCX_MediaType.equals(i.getMediaType()))
                    .findFirst().get();
            NCXReader reader = new NCXReader(file.getInputStream(parent + "/" + ncx.getHref()));
            NCX nav = reader.read();
            epub.setNCX(nav);
        }

        return epub;
    }
}
