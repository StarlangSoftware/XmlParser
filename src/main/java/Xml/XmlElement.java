package Xml;

import java.util.ArrayList;

public class XmlElement {

    private String name = "";
    private String pcData = "";
    private ArrayList<XmlAttribute> attributes;
    private XmlElement parent;
    private XmlElement firstChild;
    private XmlElement nextSibling;

    /**
     * Constructor for xml element. Allocates memory and initializes an element.
     * @param name Name of the element
     * @param parent Parent of the Xml Element
     */
    public XmlElement(String name, XmlElement parent) {
        this.name = name;
        this.parent = parent;
        this.attributes = new ArrayList<>();
    }

    public String getName(){
        return this.name;
    }

    public String getPcData(){
        return this.pcData;
    }

    public XmlElement getFirstChild(){
        return this.firstChild;
    }

    public XmlElement getNextSibling() {
        return nextSibling;
    }

    public XmlElement getParent(){
        return this.parent;
    }

    /**
     * Sets the value of an attribute to a given value
     * @param attributeName Name of the attribute
     * @param attributeValue New attribute value
     */
    public void setAttributeValue(String attributeName, String attributeValue){
        for (XmlAttribute attribute : this.attributes) {
            if (attribute.getName().equals(attributeName)) {
                attribute.setValue(attributeValue);
            }
        }
    }

    /**
     * Finds the attribute with the given name of an Xml element
     * @param attributeName Name of the attribute
     * @return If the Xml element has such an attribute returns its value, otherwise it returns NULL
     */
    public String getAttributeValue(String attributeName){
        for (XmlAttribute attribute : this.attributes) {
            if (attribute.getName().equals(attributeName)) {
                return attribute.getValue();
            }
        }
        return "";
    }

    public int attributeSize(){
        return this.attributes.size();
    }

    public XmlAttribute getAttribute(int index){
        return attributes.get(index);
    }

    public void setNextSibling(XmlElement nextSibling){
        this.nextSibling = nextSibling;
    }

    public void setFirstChild(XmlElement firstChild){
        this.firstChild = firstChild;
    }

    public void addAttribute(XmlAttribute xmlAttribute){
        this.attributes.add(xmlAttribute);
    }

    public void setPcData(String pcData){
        this.pcData = pcData;
    }

    public boolean hasAttributes(){
        return this.attributes.size() != 0;
    }

}
