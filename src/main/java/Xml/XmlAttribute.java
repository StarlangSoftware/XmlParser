package Xml;

public class XmlAttribute {

    private String name;
    private String value;

    /**
     * Constructor for xml attribute. Initializes the attribute.
     * @param name Name of the attribute
     */
    public XmlAttribute(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String toString(){
        return this.name + "=\"" + this.value + "\"";
    }

}
