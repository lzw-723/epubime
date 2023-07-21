package fun.lzwi.epubime;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fun.lzwi.epubime.document.NavigationDocReader;
import fun.lzwi.epubime.document.NavigationDocument;
import fun.lzwi.epubime.document.PackageDocument;
import fun.lzwi.epubime.document.PackageDocumentReader;
import fun.lzwi.epubime.util.ContainerUtils;

public class EpubReader {
    private EpubFile file;

    public EpubReader(EpubFile file) {
        super();
        this.file = file;
    }

    public Epub read() throws ParserConfigurationException, SAXException, IOException {
        String opf = ContainerUtils.getRootFile(file.getInputStream(EpubFile.CONTAINER_PATH));
        PackageDocument packageDocument = new PackageDocumentReader(file.getInputStream(opf)).read();
        String nav = packageDocument.getManifest().getItems().stream().filter(i -> "nav".equals(i.getProperties()))
                .findFirst().get().getHref();

        String parent = "";
        Path dir = Paths.get(opf).getParent();
        if (dir != null) {
            parent = dir.toString();
        }

        NavigationDocReader navigationDocReader = new NavigationDocReader(file.getInputStream(parent + "/" + nav));
        NavigationDocument navigationDocument = navigationDocReader.read();

        Epub epub = new Epub();
        epub.setPackageDocument(packageDocument);
        epub.setNavigationDocument(navigationDocument);
        return epub;
    }
}
