package Xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlDocument {

    private XmlElement root;
    private XmlTokenType lastReadTokenType = XmlTokenType.XML_END;
    private String data;
    private int position;
    private char nextChar;

    /**
     * Creates an empty xml document.
     * @param fileName Name of the xml file
     */
    public XmlDocument(String fileName) {
        try {
            data = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            data = "";
        }
        this.position = 0;
    }

    public XmlDocument(InputStream inputStream) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try{
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            data = result.toString("UTF-8");
        } catch (IOException e) {
            data = "";
        }
        this.position = 0;
    }

    public char readChar(){
        if (this.position < data.length()){
            char ch = data.charAt(this.position);
            this.position++;
            return ch;
        } else {
            lastReadTokenType = XmlTokenType.XML_END;
            return Character.MIN_VALUE;
        }
    }

    /**
     * Reads a token character by character from xml file.
     * @param previousChar Previous character read
     * @param extraAllowed If true, space or slash is allowed in the token, otherwise it is not allowed
     * @param quotaAllowed If true, quota is allowed in the token, otherwise it is not allowed
     * @return Token read
     */
    public String readToken(char previousChar, boolean extraAllowed, boolean quotaAllowed){
        char ch = previousChar;
        String buffer = "";
        while ((ch != '\'' || extraAllowed) && (ch != '\"' || quotaAllowed) && (ch != '=' || quotaAllowed) && (ch != ' ' || extraAllowed) && (ch != '/' || extraAllowed) && (ch != '<') && (ch != Character.MIN_VALUE) && (ch != '>' || quotaAllowed)) {
            buffer += ch;
            ch = this.readChar();
        }
        this.nextChar = ch;
        return buffer;
    }

    public String readToken(char previousChar){
        return readToken(previousChar, false, false);
    }

    public String readToken(char previousChar, boolean extraAllowed){
        return readToken(previousChar, extraAllowed, false);
    }

    /**
     * Parses a tag like mytag or /mytag
     * @return Token read
     */
    public String parseTag(){
        char ch = this.readChar();
        if (ch == '/') {
            this.lastReadTokenType = XmlTokenType.XML_CLOSING_TAG_WITHOUT_ATTRIBUTES;
            ch = this.readChar();
        } else {
            this.lastReadTokenType = XmlTokenType.XML_OPENING_TAG_WITH_ATTRIBUTES;
        }
        String token = this.readToken(ch);
        ch = this.nextChar;
        if (ch == '>' && this.lastReadTokenType == XmlTokenType.XML_OPENING_TAG_WITH_ATTRIBUTES){
            this.lastReadTokenType = XmlTokenType.XML_OPENING_TAG_WITHOUT_ATTRIBUTES;
        }
        if (this.lastReadTokenType == XmlTokenType.XML_CLOSING_TAG_WITHOUT_ATTRIBUTES && ch != '>') {
            this.lastReadTokenType = XmlTokenType.XML_END;
            return "";
        }
        else{
            return token;
        }
    }

    /**
     * Parses an attribute value like "attribute value" or 'attribute value'
     * @return Attribute value read
     */
    public String parseAttributeValue(){
        char ch = this.readChar();
        if (ch == '"') {
            this.lastReadTokenType = XmlTokenType.XML_ATTRIBUTE_VALUE;
            return "";
        }
        String token = this.readToken(ch, true);
        ch = this.nextChar;
        if (ch != '"') {
            this.lastReadTokenType = XmlTokenType.XML_END;
            return "";
        }
        this.lastReadTokenType = XmlTokenType.XML_ATTRIBUTE_VALUE;
        return token;
    }

    /**
     * Parses a tag like closing tag
     * @return ""
     */
    public String parseEmptyTag(){
        char ch = this.readChar();
        if (ch != '>') {
            this.lastReadTokenType = XmlTokenType.XML_END;
        } else {
            this.lastReadTokenType = XmlTokenType.XML_CLOSING_TAG_WITH_ATTRIBUTES;
        }
        return "";
    }

    public String getNextToken(XmlTextType xmlTextType){
        char ch = this.readChar();
        while (ch == ' ' || ch == '\t' || ch == '\n'){
            ch = this.readChar();
        }
        switch (ch){
            case  '<':
                return this.parseTag();
            case '"':
                if (xmlTextType == XmlTextType.XML_TEXT_VALUE){
                    String token = this.readToken(ch, true, true);
                    ch = this.nextChar;
                    this.lastReadTokenType = XmlTokenType.XML_TEXT;
                    this.position--;
                    return token;
                } else {
                    return this.parseAttributeValue();
                }
            case  '/':
                return this.parseEmptyTag();
            case  '=':
                if (xmlTextType == XmlTextType.XML_TEXT_VALUE){
                    String token = this.readToken(ch, true, true);
                    ch = this.nextChar;
                    this.lastReadTokenType = XmlTokenType.XML_TEXT;
                    this.position--;
                    return token;
                } else {
                    this.lastReadTokenType = XmlTokenType.XML_EQUAL;
                }
                break;
            case  '>':
                if (xmlTextType == XmlTextType.XML_TEXT_VALUE){
                    String token = this.readToken(ch, true, true);
                    ch = this.nextChar;
                    this.lastReadTokenType = XmlTokenType.XML_TEXT;
                    this.position--;
                    return token;
                } else {
                    this.lastReadTokenType = XmlTokenType.XML_OPENING_TAG_FINISH;
                }
                return "";
            case Character.MIN_VALUE:
                return "";
            default:
                String token;
                if (xmlTextType == XmlTextType.XML_TEXT_VALUE){
                    token = this.readToken(ch, true, true);
                } else {
                    token = this.readToken(ch, true);
                }
                ch = this.nextChar;
                this.lastReadTokenType = XmlTokenType.XML_TEXT;
                this.position--;
                return token;
        }
        return "";
    }

    /**
     * Parses given xml document
     */
    public void parse() {
        XmlTextType textType = XmlTextType.XML_TEXT_ATTRIBUTE;
        boolean siblingClosed = false;
        String token;
        XmlAttribute xmlAttribute = null;
        XmlElement sibling = null;
        XmlElement parent = null;
        XmlElement current = null;
        token = this.getNextToken(textType);
        while (this.lastReadTokenType != XmlTokenType.XML_END){
            switch (this.lastReadTokenType){
                case XML_OPENING_TAG_WITH_ATTRIBUTES:
                case XML_OPENING_TAG_WITHOUT_ATTRIBUTES:
                    current = new XmlElement(token, parent);
                    if (parent != null) {
                        if (sibling != null && siblingClosed) {
                            sibling.setNextSibling(current);
                            sibling = current;
                        } else {
                            parent.setFirstChild(current);
                        }
                    } else {
                        if (this.root == null){
                            this.root = current;
                        }
                    }
                    parent = current;
                    siblingClosed = false;
                    if (this.lastReadTokenType == XmlTokenType.XML_OPENING_TAG_WITH_ATTRIBUTES){
                        textType = XmlTextType.XML_TEXT_ATTRIBUTE;
                    } else {
                        textType = XmlTextType.XML_TEXT_VALUE;
                    }
                    break;
                case XML_OPENING_TAG_FINISH:
                    textType = XmlTextType.XML_TEXT_VALUE;
                    siblingClosed = false;
                    break;
                case XML_CLOSING_TAG_WITH_ATTRIBUTES:
                    sibling = current;
                    parent = current.getParent();
                    textType = XmlTextType.XML_TEXT_VALUE;
                    siblingClosed = true;
                    break;
                case XML_CLOSING_TAG_WITHOUT_ATTRIBUTES:
                    if (token.equals(current.getName())) {
                        sibling = current;
                        parent = current.getParent();
                    } else {
                        if (token.equals(current.getParent().getName())) {
                            sibling = parent;
                            parent = current.getParent().getParent();
                            current = current.getParent();
                        }
                    }
                    siblingClosed = true;
                    textType = XmlTextType.XML_TEXT_VALUE;
                    break;
                case XML_ATTRIBUTE_VALUE:
                    if (!token.isEmpty()){
                        token = this.replaceEscapeCharacters(token);
                        xmlAttribute.setValue(token);
                    } else {
                        xmlAttribute.setValue("");
                    }
                    current.addAttribute(xmlAttribute);
                    textType = XmlTextType.XML_TEXT_ATTRIBUTE;
                    break;
                case XML_EQUAL:
                    textType = XmlTextType.XML_TEXT_NOT_AVAILABLE;
                    break;
                case XML_TEXT:
                    if (textType == XmlTextType.XML_TEXT_ATTRIBUTE) {
                        xmlAttribute = new XmlAttribute(token);
                    } else {
                        if (textType == XmlTextType.XML_TEXT_VALUE){
                            token = this.replaceEscapeCharacters(token);
                            current.setPcData(token);
                        }
                    }
                    break;
                default:
                    break;
            }
            token = this.getNextToken(textType);
        }
    }

    public XmlElement getFirstChild(){
        return this.root;
    }

    public String replaceEscapeCharacters(String token){
        String result = token;
        while (result.contains("&quot;")){
            result = result.replace("&quot;", "\"");
        }
        while (result.contains("&amp;")){
            result = result.replace("&amp;", "&");
        }
        while (result.contains("&lt;")){
            result = result.replace("&lt;", "<");
        }
        while (result.contains("&gt;")){
            result = result.replace("&gt;", ">");
        }
        while (result.contains("&apos;")){
            result = result.replace("&apos;", "'");
        }
        return result;
    }
}
