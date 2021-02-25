import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

// Source: https://www.journaldev.com/1198/java-sax-parser-example
public class CotizacionesHandler extends DefaultHandler {
    private Hashtable<String, Double> tabla = new Hashtable<String, Double>();

    private String empresa;
    private boolean leeEmpresa;
    // Obtenemos los carácteres leídos por el SAXParser en el método characters()
    private StringBuilder data = null;

    private Double cotizacion;
    private boolean leeCotizacion;

    @Override
    public void startDocument() throws SAXException {
//        System.out.println("Comienza lectura de fichero...");
        empresa = "";
        leeEmpresa = false;
        cotizacion = 0.0;
        leeCotizacion = false;
    }

    @Override
    public void endDocument() throws SAXException {
//        System.out.println("Finaliza lectura de fichero...");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        System.out.println("Comienza el elmento: <" + qName + ">");
        if (qName.equals("empresa")) {
            // Es una empresa --> Guardar su valor en la variable temporal
            leeEmpresa = true;
        } else if (qName.equals("cotizaciones")) {
            // Es una cotizacion --> Asociar a la empresa que sea
            leeCotizacion = true;
        }
        // Inicializamos el contenedor donde se almacena la información leida
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // Nada
//        System.out.println("Fin del elmento: <" + qName + ">");
        if (leeEmpresa) {
            empresa = data.toString();
            leeEmpresa = false;
        } else if (leeCotizacion) {
            cotizacion = Double.parseDouble(data.toString());
            leeCotizacion = false;
        }
        if (qName.equalsIgnoreCase("cotizacion")) {
            // añadimos el par empresa - cotización a la tabla en caso de que sea un fin de elemento
            tabla.put(empresa, cotizacion);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }

    public Hashtable<String, Double> getTabla() {
        return tabla;
    }
}