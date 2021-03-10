import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
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
        String sparqlEndpointLive = "http://dbpedia-live.openlinksw.com/sparql";
        String sparqlEndpoint = "https://dbpedia.org/sparql";
//        tendencias.add("Neymar_Jr");
//        tendencias.add("Neymar");
//        tendencias.add("Messi");

        for (String tendencia : tendencias) {

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

            String tendenciaWithoutSpaces = tendencia.replace(" ", "_");

            String askQuery = String.format("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                            "PREFIX yago: <http://dbpedia.org/class/yago/> " +
                            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                            "PREFIX dct: <http://purl.org/dc/terms/>" +
                            "PREFIX dbc: <http://dbpedia.org/page/Category:>" +
                            "PREFIX dbp: <http://dbpedia.org/property/wikiPageUsesTemplate>" +
                            "PREFIX dbt: <http://dbpedia.org/resource/Template:>" +
                            "PREFIX dbo: <http://live.dbpedia.org/ontology/wikiPageRedirects>" +
                            "PREFIX dbr: <http://dbpedia.org/resource/>" +
                            "ASK {" +
                            "{ dbr:%s rdf:type yago:FootballPlayer110101634}" +
                            "UNION" +
                            "{ dbr:%s rdf:type yago:LaLigaFootballers}" +
                            "UNION" +
                            "{ dbr:%s dct:subject dbc:LaLigaFootballers}" +
                            "UNION" +
                            "{ dbr:%s dct:subject dbc:Spanish_footballers}" +
                            "UNION" +
                            "{ dbr:%s dct:subject dbc:Spain_international_footballers}" +
                            "UNION" +
                            "{ dbr:%s dbp:wikiPageUsesTemplate dbt:FIFA_player}" +
                            "UNION" +
                            "{ dbr:%s dbp:wikiPageUsesTemplate dbt:UEFA_player}}"
                    , tendenciaWithoutSpaces, tendenciaWithoutSpaces,
                    tendenciaWithoutSpaces, tendenciaWithoutSpaces,
                    tendenciaWithoutSpaces, tendenciaWithoutSpaces,
                    tendenciaWithoutSpaces, tendenciaWithoutSpaces);

            org.apache.jena.query.Query query = QueryFactory.create(askQuery);

            QueryExecution exec = QueryExecutionFactory.sparqlService(
                    sparqlEndpointLive, query);
            if (exec.execAsk()) {
                resultados.add(tendencia);
            } else {
                // Esta consulta se hace en caso de no sea futbolista con la consulta ASK anteriror
                // Se hace sobre la dbpedia original ya que se han tenido problemas de busqueda
                String selectQuery =
                        String.format("PREFIX dbo: <http://dbpedia.org/ontology/>" +
                                        "PREFIX dbr: <http://dbpedia.org/resource/>" +
                                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                                        "PREFIX yago: <http://dbpedia.org/class/yago/>" +
                                        "PREFIX umbel-rc: <http://umbel.org/umbel/rc/>" +
                                        "SELECT DISTINCT ?redirects " +
                                        "WHERE " +
                                        "{ {dbr:%s dbo:wikiPageRedirects ?redirects . ?redirects rdf:type dbo:SoccerPlayer }" +
                                        "UNION" +
                                        "{ dbr:%s dbo:wikiPageRedirects ?redirects . ?redirects rdf:type yago:FootballPlayer110101634}" +
                                        "UNION" +
                                        "{ dbr:%s dbo:wikiPageRedirects ?redirects . ?redirects rdf:type umbel-rc:SoccerPlayer}}",
                                tendenciaWithoutSpaces, tendenciaWithoutSpaces, tendenciaWithoutSpaces, tendenciaWithoutSpaces);

                query = QueryFactory.create(selectQuery);
                exec = QueryExecutionFactory.sparqlService(
                        sparqlEndpoint, query);
                try {
                    ResultSet results = exec.execSelect();
                    while (results.hasNext()) {
                        QuerySolution qs = results.nextSolution();
                        RDFNode n = qs.get("?redirects");

                        System.out.println("Obtenido: " + n.toString());
                        if (!n.isLiteral()) {
                            resultados.add(tendencia);
                        }
                    }
                } finally {
                    exec.close();
                }
            }
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
            System.out.println(tren.getName());
            System.out.println("*".repeat(30));
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