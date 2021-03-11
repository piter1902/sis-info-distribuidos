import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;

public class P3 {
    // Hacemos las variables accesibles para los distintos métodos
    private static OWLDataFactory factory;
    private static OWLOntology ont;
    private static IRI ontologyIRI;
    private static OWLOntologyManager manager;

    public static void main(String[] args) {
        try {
            // Carga ontología
            ontologyIRI = IRI.create("http://sid.cps.unizar.es/");
            File file = new File("pruebas/el-padrino.owl");
            File fileDest = new File("pruebas/el-padrino-result.owl");
            manager = OWLManager.createOWLOntologyManager();
            ont = manager.loadOntologyFromOntologyDocument(file);
            factory = manager.getOWLDataFactory();
            // Añadimos axiomas de la práctica
            // Añadimos mafiosos
            addMafioso("Toni_El_Gordo");
            addMafioso("Javier_Tebas");
            addMafioso("Bartomeu");

            // Crear el razonador
            OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ont);
            reasoner.precomputeInferences();
            // Ejemplo de consulta: consistencia
            System.out.println(reasoner.isConsistent());
            // Lanzamos consultas de la práctica
            // ...
            // Salvamos la ontología
            manager.saveOntology(ont, IRI.create(fileDest.toURI()));
            System.out.println("Ontología salvada correctamente");
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }

    private static void addMafioso(String mafioso) throws OWLOntologyStorageException {
        OWLClass tClass = factory.getOWLClass(IRI.create(ontologyIRI + "Mafioso"));
        OWLNamedIndividual tIndividual = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + mafioso));
        System.out.println("Creado individuo: " + tIndividual.getIRI());

        OWLClassAssertionAxiom axiom1 = factory.getOWLClassAssertionAxiom(tClass, tIndividual);
        AddAxiom addAxiom1 = new AddAxiom(ont, axiom1);
        manager.applyChange(addAxiom1);
    }
}