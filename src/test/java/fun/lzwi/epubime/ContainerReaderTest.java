package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

public class ContainerReaderTest {
    @Test
    public void testExistContainer() throws SAXException, IOException, ParserConfigurationException {
        assertTrue("container.xml文档中container元素的xmlns属性等于urn:oasis:names:tc:opendocument:xmlns:container",
                ContainerReader.existContainer(Util.getFile("container.xml")));
    }

    @Test
    public void testExistRootFiles() throws ParserConfigurationException, SAXException, IOException {
        assertTrue("container.xml文档中rootfile元素的mimetype属性等于application/oebps-package+xml",
                ContainerReader.existRootFiles(Util.getFile("container.xml")));
    }

    @Test
    public void testGetRootFile() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("能获取package.opf路径", "EPUB/package.opf",
                ContainerReader.getRootFile(Util.getFile("container.xml")));
    }
}