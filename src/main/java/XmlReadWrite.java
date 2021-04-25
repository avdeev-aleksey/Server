import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class XmlReadWrite {

    static void writeXml(ArrayList<String> arrayList, String fileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement =
                    doc.createElement("root");
            doc.appendChild(rootElement);

            for (String s : arrayList) {
                rootElement.appendChild(getMessage(doc, s));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(fileName));
            transformer.transform(source, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node getMessage(Document doc, String value) {
        Element item = doc.createElement("item");
        item.appendChild(doc.createTextNode(value));
        return item;
    }

    static ArrayList<String> readXml(String fileName) {

        File xmlFile = new File(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        ArrayList<String> itemList = new ArrayList<>();

        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                itemList.add(nodeList.item(i).getFirstChild().getNodeValue());
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return itemList;
    }
}