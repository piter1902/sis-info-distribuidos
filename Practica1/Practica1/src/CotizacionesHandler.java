import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class CotizacionesHandler extends DefaultHandler {
    private Hashtable<String, Double> tabla = new Hashtable<String, Double>();

    // TODO: Añadir más cosas

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
    }

    public Hashtable<String, Double> getTabla() {
        return tabla;
    }
}