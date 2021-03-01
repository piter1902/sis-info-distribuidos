import java.util.*;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

public class jenaQuery {
    public static void main(String[] args) {
        String tendencias[] = {"Neymar", "Iago_Aspas", "Lionel Messi"};
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
            String typeEndpoints[] = {"yago:FootballPlayer110101634", "umbel-rc:SoccerPlayer",
                    "yago:LaLigaFootballers", "yago:FootballPlayer110101634"};
            // Dentro de dct:subject:
            String subjectEndpoints[] = {"dbc:La_Liga_players", "dbc:Spanish_footballers",
                    "dbc:Spain_international_footballers"};
            // Dentro de dbp:wikiPageUsesTemplate:
            String wikiEndpoints[] = {"dbt:FIFA_player", "dbt:UEFA_player"};

            String askQuery = ...
            org.apache.jena.query.Query query = QueryFactory.create(askQuery);
            QueryExecution exec = QueryExecutionFactory.sparqlService(
                    sparqlEndpoint, query);
            if (exec.execAsk())
                resultados.add(tendencia);
        }
    }

    private void mostrarResultados(String tendencias[], Set<String> resultados) {
        for (String tendencia : tendencias) {
            if (resultados.contains(tendencia)) {
                System.out.println("La tendencia: " + tendencia + " es un jugador de futbol");
            } else {
                System.out.println("La tendencia: " + tendencia + " NO es un jugador de futbol");
            }
        }
    }
}