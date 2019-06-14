package com.etranzact.switchit.utils;

/**
 *
 * @author joel.eze
 */
public class XMLUtils {

    String xml;

    public XMLUtils() {
    }

    public XMLUtils(String xml) {
        this.xml = xml;
    }

    public String getXMLValue(String tagName) {
        try {
            return removeNewLine(this.xml.substring(this.xml.indexOf("<" + tagName + ">") + tagName.length() + 2, this.xml
                    .indexOf("</" + tagName + ">")).trim());
        } catch (Exception sd) {
        }
        return "";
    }

    private String removeNewLine(String textValue) {
        textValue = textValue.replaceAll("\n", "");
        textValue = textValue.replaceAll("\t", "");
        textValue = textValue.replaceAll("\\n", "");
        textValue = textValue.replaceAll("\\t", "");
        textValue = textValue.replaceAll("\r", "");
        textValue = textValue.replaceAll("\\r", "");
        textValue = textValue.replaceAll("\r\n", "");
        textValue = textValue.replaceAll("\\r\\n", "");
        return textValue;
    }
}
