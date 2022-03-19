package Xml;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class XmlDocumentTest {

    @Test
    public void readTest() {
        XmlDocument doc = new XmlDocument("test.xml");
        try {
            doc.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        XmlElement root = doc.getFirstChild();
        assertEquals("frameset", root.getName());
        XmlElement firstChild = root.getFirstChild();
        assertEquals("role", firstChild.getName());
        assertEquals("ali veli \"deneme yapmak\" = anlamında > bir deyim", firstChild.getPcData());
        XmlElement secondChild = firstChild.getNextSibling();
        assertEquals("perceiver, alien \"x3\" to whom?", secondChild.getAttributeValue("descr"));
        assertEquals("PAG", secondChild.getAttributeValue("f"));
        assertEquals("2", secondChild.getAttributeValue("n"));
    }
}
