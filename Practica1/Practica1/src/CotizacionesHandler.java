import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class CotizacionesHandler extends DefaultHandler {
    private Hashtable<String, Double> tabla = new Hashtable<String, Double>();

    // TODO: Añadir más cosas
    private String empresa;
    private boolean leeEmpresa;

    private Double cotizacion;
    private boolean leeCotizacion;

    @Override
    public void startDocument() throws SAXException {
        empresa = "";
        leeEmpresa = false;
        cotizacion = 0.0;
        leeCotizacion = false;
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        System.out.println("Comienza el elmento: <" + qName + ">");
        if (qName.equals("lista_cotizaciones")) {
            // Es un conjunto de empresas --> No hacemos nada
            leeEmpresa = false;
            leeCotizacion = false;
        } else if (qName.equals("empresa")) {
            // Es una empresa --> Guardar su valor en la variable temporal
            leeEmpresa = true;
            leeCotizacion = false;
        } else if (qName.equals("cotizaciones")) {
            // Es una cotizacion --> Asociar a la empresa que sea
            leeEmpresa = false;
            leeCotizacion = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // Nada
        System.out.println("Fin del elmento: <" + qName + ">");
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
//        System.out.println("Texto de dentro: " + new String(ch));
        if (leeEmpresa) {
            empresa = new String(ch);
        } else if (leeCotizacion) {
            String tmp = new String(ch);
            cotizacion = Double.parseDouble(tmp);
            tabla.put(empresa, cotizacion);
        }
    }

    public Hashtable<String, Double> getTabla() {
        return tabla;
    }
}