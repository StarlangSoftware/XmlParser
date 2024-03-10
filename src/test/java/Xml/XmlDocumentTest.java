package Xml;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class XmlDocumentTest {

    @Test
    public void readTest1() {
        XmlDocument doc = new XmlDocument("test.xml");
        doc.parse();
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

    @Test
    public void readTest2() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("test.xml");
        XmlDocument doc = new XmlDocument(inputStream);
        doc.parse();
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
