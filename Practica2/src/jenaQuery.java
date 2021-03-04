import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import twitter4j.*;

import java.util.*;

public class jenaQuery {
    public static void main(String[] args) throws TwitterException {

        // Static version:
//        String tendencias[] = {"Neymar", "Iago_Aspas", "Lionel Messi", "Prueba", "Iker_Casillas", "Cristiano_Ronaldo",
//                "Torres", "Ronaldo", "Pablo Iglesias", ""};

        // Dynamic version
        List<String> tendencias = getTwitterTrends();

        Set<String> resultados = new HashSet<String>();
        String sparqlEndpoint = "http://dbpedia-live.openlinksw.com/sparql";
        tendencias.add("Neymar_Jr");
        tendencias.add("Neymar");
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

//            String tendenciaWithSpaces = tendencia.replace("_", " ");
            String tendenciaWithSpaces = tendencia;
            String askQuery = String.format("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                            "PREFIX yago: <http://dbpedia.org/class/yago/> " +
                            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                            "PREFIX dct: <http://purl.org/dc/terms/>" +
                            "PREFIX dbc: <http://dbpedia.org/page/Category:>" +
                            "PREFIX dbp: <http://dbpedia.org/property/wikiPageUsesTemplate>" +
                            "PREFIX dbt: <http://dbpedia.org/resource/Template:>" +
                            "PREFIX dbo: <http://live.dbpedia.org/ontology/wikiPageRedirects>" +
                            "PREFIX dbr: <http://dbpedia.org/resource/>" +
                            "ASK WHERE {" +
                            "{ ?x rdf:type yago:FootballPlayer110101634 . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x rdf:type yago:LaLigaFootballers . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x dct:subject dbc:LaLigaFootballers . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x dct:subject dbc:Spanish_footballers . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x dct:subject dbc:Spain_international_footballers . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x dbp:wikiPageUsesTemplate dbt:FIFA_player . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x dbp:wikiPageUsesTemplate dbt:UEFA_player . ?x rdfs:label \"%s\"@en}" +
                            "UNION" +
                            "{ ?x dbo:wikiPageRedirects ?y . ?y rdf:type yago:FootballPlayer110101634 .  FILTER (SAMETERM(?x, \"%s\"))}}"
                    , tendenciaWithSpaces, tendenciaWithSpaces,
                    tendenciaWithSpaces, tendenciaWithSpaces,
                    tendenciaWithSpaces, tendenciaWithSpaces,
                    tendenciaWithSpaces, tendenciaWithSpaces, tendenciaWithSpaces);

            org.apache.jena.query.Query query = QueryFactory.create(askQuery);

            QueryExecution exec = QueryExecutionFactory.sparqlService(
                    sparqlEndpoint, query);
            if (exec.execAsk())
                resultados.add(tendencia);
        }

        mostrarResultados(tendencias, resultados);
    }

    // Source: https://stackoverflow.com/questions/10431321/using-twitter4j-daily-trends/26649271
    private static List<String> getTwitterTrends() throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();
        Trends dailyTrends;
        List<String> list = new ArrayList<>();

//        ResponseList<Location> locations = twitter.getAvailableTrends();
//        for (Location loc : locations) {
//            System.out.println(loc.getCountryName() + " - " + loc.getWoeid() + " - " + loc.getPlaceCode());
//        }

        // woeid de españa se ha obtenido usando la función que se encuentra comentada encima
        dailyTrends = twitter.getPlaceTrends(23424950);

        // Recorremos las tendencias obtenidas
        for (Trend tren : dailyTrends.getTrends()) {
//            System.out.println(tren.getName());
//            System.out.println("*".repeat(30));
            // Se eliminan los posibles '#'
            list.add(tren.getName().replace("#", ""));
        }

        return list;
    }

    private static void mostrarResultados(List<String> tendencias, Set<String> resultados) {
        System.out.println("Longitud de resultados: " + resultados.toArray().length);
        for (String tendencia : tendencias) {
            if (resultados.contains(tendencia)) {
                System.out.println("La tendencia: \" " + tendencia + " \" es un jugador de futbol");
            } else {
                System.out.println("La tendencia: \" " + tendencia + " \" NO es un jugador de futbol");
            }
        }
    }
}