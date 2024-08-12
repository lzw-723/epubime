package fun.lzwi.epubime.util;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {

    public static Document getDocument(InputStream inputStream) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 忽略dtd验证
        builder.setEntityResolver((publicId, systemId) -> new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())));
        Document document = builder.parse(inputStream);
        inputStream.close();
        return document;
    }

    /**
     * 获取指定名字的Node的列表，浅层搜索，不递归
     *
     * @param inputStream
     * @param tag
     * @return NodeList
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static NodeList getElementsByTagName(InputStream inputStream, String tag) throws ParserConfigurationException, SAXException, IOException {
        return getDocument(inputStream).getElementsByTagName(tag);
    }

    public static NodeList getElementsByTagName(File file, String tag) throws ParserConfigurationException,
            SAXException, IOException {
        return getElementsByTagName(Files.newInputStream(file.toPath()), tag);
    }

    public static void foreachNodeList(NodeList list, Consumer<Node> consumer) {
        Node node;
        for (int i = 0; i < list.getLength(); i++) {
            if ((node = list.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                consumer.accept(node);
            }
        }
    }

    /**
     * 获取指定标签名的第一个子节点
     *
     * @param node 节点
     * @param tag  标签名
     * @return 子节点
     */
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

    public static String document2String(Document document) {
        try {
            document.setXmlStandalone(true);
            DocumentType doctype = document.getDoctype();
            NamedNodeMap entities = doctype.getEntities();
            for (int i = 0; i < entities.getLength(); i++) {
                entities.removeNamedItem(entities.item(i).getNodeName());
            }
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
