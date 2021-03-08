import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;

public class P3 {
    public static void main(String[] args) {
        try {
            // Carga ontología
            IRI ontologyIRI = IRI.create("http://sid.cps.unizar.es");
            File file = new File("SIDpractica3.owl");
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(file);
            OWLDataFactory factory = manager.getOWLDataFactory();
            // Añadimos axiomas de la práctica
            // ...
            // Crear el razonador
            OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ont);
            reasoner.precomputeInferences();
            // Ejemplo de consulta: consistencia
            System.out.println(reasoner.isConsistent());
            // Lanzamos consultas de la práctica
            // ...
            // Salvamos la ontología
            manager.saveOntology(ont, IRI.create(file.toURI()));
            System.out.println("Ontología salvada correctamente");
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }
}