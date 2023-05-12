package fun.lzwi.epubime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import fun.lzwi.Util;

public class ContainerCheckerTest {
    @Test
    public void testCheckContainer() throws SAXException, IOException, ParserConfigurationException {
        assertTrue("container.xml文档中container元素的xmlns属性等于urn:oasis:names:tc:opendocument:xmlns:container",
                ContainerChecker.checkContainer(Util.getFile("container.xml")));
    }

    @Test
    public void testCheckRootFiles() throws ParserConfigurationException, SAXException, IOException {
        assertTrue("container.xml文档中rootfile元素的mimetype属性等于application/oebps-package+xml",
                ContainerChecker.checkRootFiles(Util.getFile("container.xml")));
    }

    @Test
    public void testGetRootFile() throws ParserConfigurationException, SAXException, IOException {
        assertEquals("能获取package.opf路径", "EPUB/package.opf",
                ContainerChecker.getRootFile(Util.getFile("container.xml")));
    }
}
