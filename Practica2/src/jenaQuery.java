import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;

import java.util.HashSet;
import java.util.Set;

public class jenaQuery {
    public static void main(String[] args) {
        String tendencias[] = {"Neymar", "Iago_Aspas", "Lionel Messi", "Prueba", "Iker_Casillas", "Cristiano_Ronaldo"};
        Set<String> resultados = new HashSet<String>();
        String sparqlEndpoint = "http://dbpedia-live.openlinksw.com/sparql";
        for (String tendencia : tendencias) {
//            String selectQuery = ...
//            org.apache.jena.query.Query query = QueryFactory.create(selectQuery);
//            QueryExecution exec = QueryExecutionFactory.sparqlService(
//                    sparqlEndpoint, query);
//            try {
//                ResultSet results = exec.execSelect();
//                while (results.hasNext()) {
//                    QuerySolution qs = results.nextSolution();
//                    RDFNode n = qs.get(variableName);
//                    if (!n.isLiteral())
//                        resultados.add(n.toString());
//                }
//            } finally {
//                exec.close();
//            }
            // RDF endpoints sobre los que realizar la query
            // Se han añadido varios puntos de búsqueda. Para realizar la consulta se deberá hacer UNION
            // Dentro de rdf:type:
            String typeEndpoints[] = {"yago:FootballPlayer110101634",
                    "yago:LaLigaFootballers", "yago:FootballPlayer110101634"};
            // Dentro de dct:subject:
            String subjectEndpoints[] = {"dbc:La_Liga_players", "dbc:Spanish_footballers",
                    "dbc:Spain_international_footballers"};
            // Dentro de dbp:wikiPageUsesTemplate:
            String wikiEndpoints[] = {"dbt:FIFA_player", "dbt:UEFA_player"};

            String tendenciaWithSpaces = tendencia.replace("_", " ");
            String askQuery = String.format("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                            "PREFIX yago: <http://dbpedia.org/class/yago/> " +
                            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                            "PREFIX dct: <http://purl.org/dc/terms/>" +
                            "PREFIX dbc: <http://dbpedia.org/page/Category:>" +
                            "PREFIX dbp: <http://dbpedia.org/property/wikiPageUsesTemplate>" +
                            "PREFIX dbt: <http://dbpedia.org/resource/Template:>" +
                            "ASK WHERE {" +
                            "{ ?x rdf:type yago:FootballPlayer110101634 . ?x rdfs:label \"%s\"@en }" +
                            "UNION" +
                            "{ ?x rdf:type yago:LaLigaFootballers . ?x rdfs:label \"%s\"@en }" +
                            "UNION" +
                            "{ ?x rdf:type yago:FootballPlayer110101634 . ?x rdfs:label \"%s\"@en }" + // Por que se repite?
                            "UNION" +
                            "{ ?x dct:subject dbc:LaLigaFootballers . ?x rdfs:label \"%s\"@en }" +
                            "UNION" +
                            "{ ?x dct:subject dbc:Spanish_footballers . ?x rdfs:label \"%s\"@en }" +
                            "UNION" +
                            "{ ?x dct:subject dbc:Spain_international_footballers . ?x rdfs:label \"%s\"@en }" +
                            "UNION" +
                            "{ ?x dbp:wikiPageUsesTemplate dbt:FIFA_player . ?x rdfs:label \"%s\"@en }" +
                            "UNION" +
                            "{ ?x dbp:wikiPageUsesTemplate dbt:UEFA_player . ?x rdfs:label \"%s\"@en } }"
                    , tendenciaWithSpaces, tendenciaWithSpaces,
                    tendenciaWithSpaces, tendenciaWithSpaces,
                    tendenciaWithSpaces, tendenciaWithSpaces,
                    tendenciaWithSpaces, tendenciaWithSpaces);

            org.apache.jena.query.Query query = QueryFactory.create(askQuery);
//            query.setPrefix("umbel-rc", "http://umbel.org/umbel/rc/SoccerPlayer");
//            query.setPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//            System.out.println("Query: " + query.toString());
            QueryExecution exec = QueryExecutionFactory.sparqlService(
                    sparqlEndpoint, query);
            if (exec.execAsk())
                resultados.add(tendencia);
        }

        mostrarResultados(tendencias, resultados);
    }

    private static void mostrarResultados(String tendencias[], Set<String> resultados) {
        System.out.println("Longitud de resultados: " + resultados.toArray().length);
        for (String tendencia : tendencias) {
            if (resultados.contains(tendencia)) {
                System.out.println("La tendencia: " + tendencia + " es un jugador de futbol");
            } else {
                System.out.println("La tendencia: " + tendencia + " NO es un jugador de futbol");
            }
        }
    }
}