package fun.lzwi.epubime.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {

    public static Document getDocument(InputStream inputStream)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        return doc;
    }

    public static NodeList getElementsByTagName(InputStream inputStream, String tag)
            throws ParserConfigurationException, SAXException, IOException {
        return getDocument(inputStream).getElementsByTagName(tag);
    }

    public static NodeList getElementsByTagName(File file, String tag)
            throws ParserConfigurationException, SAXException, IOException {
        return getElementsByTagName(new FileInputStream(file), tag);
    }

    public static void foreachNodeList(NodeList list, Consumer<Node> consumer) {
        Node node;
        for (int i = 0; i < list.getLength(); i++) {
            if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                consumer.accept(node);
            }
        }
    }

    public static Node getChildNodeByTagName(Node node, String tag) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (tag.equals(item.getNodeName())) {
                return item;
            }
        }
        return null;
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

    public static String getNodeAttribute(Node node, String name) {
        try {
            return node.getAttributes().getNamedItem(name).getTextContent();
        } catch (Exception e) {
            LoggerUtils.from(XmlUtils.class).info(node.getNodeName() + "不存在" + name + "属性");
        }
        return null;
    }

    public static String getNodeContent(Node node) {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            StringWriter stringWriter = new StringWriter();
            // FIXME: javax.xml.transform.TransformerException: 没有说明名称空间前缀
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
            return stringWriter.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
