package at.ac.tuwien.informatics.structure;

import at.ac.tuwien.informatics.structure.exception.NotOWL2QLException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;

import java.io.File;
import java.util.HashMap;

/**
 * Class that wraps OWLOntolgy objects from the OWL API.
 */
public class Ontology {

    /**
     * The OWL2 QL ontology to be used in QA.
     */
    private OWLOntology ontology;
    /**
     * A Map that maps simple class names/labels to the class in the ontology.
     */
    private HashMap<String, OWLClass> classMap;
    /**
     * A Map that maps simple role names to the properties in the ontology.
     */
    private HashMap<String, OWLProperty> propertyMap;

    /**
     * Initialize a new Ontology Wrapper from a file.
     * @param path The path to the ontology file.
     * @throws OWLOntologyCreationException If the ontology could not be loaded.
     * @throws NotOWL2QLException If the ontology is not in OWL2 QL.
     */
    public Ontology(String path) throws OWLOntologyCreationException, NotOWL2QLException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(path));
        OWLProfileReport report = new OWL2QLProfile().checkOntology(ontology);
        if (!report.isInProfile()) {
            throw new NotOWL2QLException();
        }
        this.ontology = ontology;
        generateClassMap();
        generatePropertyMap();
    }

    /**
     * Generate a map "A" -> ...#A (as OWLClass) from the ontology's signature.
     */
    private void generateClassMap() {
        this.classMap = new HashMap<>();
        for (OWLClass c : this.ontology.getClassesInSignature()) {
            if (!c.isTopEntity()) {
                classMap.put(c.toStringID().split("#")[1], c);
            }
        }
    }

    /**
     * Generate a map "r" -> ...#r (as OWLProperty) from the ontology's signature.
     */
    private void generatePropertyMap() {
        this.propertyMap = new HashMap<>();
        for (OWLProperty p : this.ontology.getObjectPropertiesInSignature()) {
            propertyMap.put(p.toStringID().split("#")[1], p);
        }
    }

    /**
     * Get the class map
     * @return Map of simple class names and their OWLClasses
     */
    public HashMap<String, OWLClass> getClassMap() {
        return classMap;
    }

    /**
     * Get the OWL2 QL Ontology.
     * @return The ontology OWLAPI object
     */
    public OWLOntology getOntology() {
        return ontology;
    }
}
