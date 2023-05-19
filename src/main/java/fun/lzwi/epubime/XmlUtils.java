package fun.lzwi.epubime;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {
    public static NodeList getElementsByTagName(File file, String tag)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        return doc.getElementsByTagName(tag);
    }

    public static void foreachNodeList(NodeList list, Consumer<Node> consumer) {
        Node node;
        for (int i = 0; i < list.getLength(); i++) {
            if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                consumer.accept(node);
            }
        }
    }

    public static int countChildNodes(NodeList list) {
        int count = 0;
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                count++;
            }
        }
        return count;
    }
}
