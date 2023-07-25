package qiwi.test.com.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class XMLParser {
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;

    public XMLParser() throws ParserConfigurationException {
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
    }

    public String parse(String code, String source) throws IOException, SAXException {
        InputSource is = new InputSource(new StringReader(source));
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Valute");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if (eElement.getElementsByTagName("CharCode")
                        .item(0)
                        .getTextContent().equals(code)) {
                    return code + "(" + eElement
                            .getElementsByTagName("Name")
                            .item(0)
                            .getTextContent() + "): " + eElement
                            .getElementsByTagName("Value")
                            .item(0)
                            .getTextContent();
                }
            }
        }
        return null;
    }
}
