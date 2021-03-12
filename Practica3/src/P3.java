import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;

public class P3 {
    // Hacemos las variables accesibles para los distintos métodos
    private static OWLDataFactory factory;
    private static OWLOntology ont;
    private static IRI ontologyIRI;
    private static OWLOntologyManager manager;
    private static OWLReasoner reasoner;

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
            reasoner = new Reasoner.ReasonerFactory().createReasoner(ont);
            reasoner.precomputeInferences();
            // Ejemplo de consulta: consistencia
            System.out.println(reasoner.isConsistent());
            // Lanzamos consultas de la práctica
            createSeparatorWithText("Mostrando individuos de la clase: Hombre");
            showAllByClassName("Hombre");

            createSeparatorWithText("Mostrando individuos de la clase: Mafioso");
            showAllByClassName("Mafioso");

            createSeparatorWithText("Mostrando individuos que son la esposa de Vito");
            printEsposaVito();

            createSeparatorWithText("Mostrando individuos que son descendientes de Vito");
            printDescendientesVito();

            createSeparatorWithText("Mostrando individuos que son padre de Michael");
            printPadreMichael();

            createSeparatorWithText("Mostrando individuo que es abuela de Vincent");
            printAbuelaVincent();


            // Salvamos la ontología
            manager.saveOntology(ont, IRI.create(fileDest.toURI()));
            createSeparatorWithText("Ontología salvada correctamente");
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }

    private static void printEsposaVito() {
        // Funcionaría también con cónyugeDe
        // Source: https://stackoverflow.com/questions/27695956/retrieve-owl-individuals-with-same-object-properties-using-owl-api-4-0
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "esposaDe"));
        OWLIndividual vito = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "Vito"));
        OWLClassExpression classExpression = factory.getOWLObjectHasValue(property, vito.asOWLNamedIndividual());

        for (OWLNamedIndividual individual : reasoner.getInstances(classExpression, false).getFlattened()) {
            System.out.println(individual.getIRI());
        }
    }

    private static void printDescendientesVito() {
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "descendienteDe"));
        OWLIndividual vito = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "Vito"));
        OWLClassExpression classExpression = factory.getOWLObjectHasValue(property, vito.asOWLNamedIndividual());

        for (OWLNamedIndividual individual : reasoner.getInstances(classExpression, false).getFlattened()) {
            System.out.println(individual.getIRI());
        }
    }

    private static void printPadreMichael() {
        OWLObjectProperty esHijoDe = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "hijoDe"));
        OWLClass esPadre = factory.getOWLClass(IRI.create(ontologyIRI + "Padre"));
        OWLIndividual michael = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "Michael"));
        // Usamos la propiedad inversa de hijoDe -> salen todos los progenitores
        OWLClassExpression hijoDe = factory.getOWLObjectHasValue(esHijoDe.getInverseProperty(), michael.asOWLNamedIndividual());
        // Calculamos la interseccion
        OWLClassExpression intersection = factory.getOWLObjectIntersectionOf(hijoDe, esPadre);

        for (OWLNamedIndividual individual : reasoner.getInstances(intersection, false).getFlattened()) {
            System.out.println(individual.getIRI());
        }
    }

    private static void printAbuelaVincent() {
        OWLObjectProperty esAbueloDe = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "abueloDe"));
        OWLClass esMujer = factory.getOWLClass(IRI.create(ontologyIRI + "Mujer"));
        OWLIndividual vincent = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "Vincent"));
        // Usamos la propiedad inversa de hijoDe -> salen todos los progenitores
        OWLClassExpression nietoDe = factory.getOWLObjectHasValue(esAbueloDe.getInverseProperty(), vincent.asOWLNamedIndividual());
        // Calculamos la interseccion
        OWLClassExpression intersection = factory.getOWLObjectIntersectionOf(nietoDe, esMujer);

        for (OWLNamedIndividual individual : reasoner.getInstances(intersection, false).getFlattened()) {
            System.out.println(individual.getIRI());
        }
    }

    private static void showAllByClassName(String classname) {
        OWLClass tClass = factory.getOWLClass(IRI.create((ontologyIRI + classname)));
        for (OWLNamedIndividual instance : reasoner.getInstances(tClass, false).getFlattened()) {
            System.out.println(instance.getIRI());
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

    private static void createSeparatorWithText(String message) {
        System.out.println("*".repeat(24));
        System.out.println(message);
        System.out.println("*".repeat(24));
    }

}