package atlassian_bamboo

import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64
import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.PrintWriter


/**
 * JIRA-API-Access
 *
 * Jira-Access via Atlassian-Jira-REST-API
 *
 * @property bambooURL : die URL fuer produktiven Bamboo-Zugriff
 * @property credentials : login:password fuer produktiven Bamboo-Zugriff
 * @property logger : log4j.properties ...
 * @constructor Setzt die JIRA-URL und die credentials
 */
class AtlassianBambooInfo(val bambooURL : String, val credentials : String) {

    val logger: Logger = Logger.getLogger(AtlassianBambooInfo::class.java.name)

    /**
     * Funktion zum Auslesen der beinhalteten Jira-Issues in einem Bamboo-Build
     * - Auslesen der Bamboo-Build-Infos , expandiert um die Jira-Issues
     * - Auswertung des JSON-Objekts
     * - Rueckgabe der beinhalteten JIRA-Issues in einer Liste
     *
     * @param [plankey] Der Bamboo-PlanKey (z.B. "MCBS-MR63") zu dem die Issues ausgelesen werden sollen
     * @param [buildnumber] Die laufende Nummer des Bamboo-Builds zu dem die Issues ausgelesen werden sollen
     *
     * @return issueList Eine Liste von Issues, die laut Bamboo in diesem Build neu hinzugekommen sind
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getIssuesForBuild(plankey: String, buildnumber: String): ArrayList<String> {
        // Liste fuer Tickets im Build
        val issueList = ArrayList<String>()
        // Authentifikation
        val auth = String(Base64.encode(credentials))

        // Client einrichten
        val client = Client.create()
        // URL setzen
        val webResource = client.resource("$bambooURL$plankey-$buildnumber?expand=jiraIssues")
        // Aufruf des Bamboo-Result
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Result : $response")
        // ResultString to JSON
        val parser = JSONParser()
        var json: JSONObject? = null
        try {
            json = parser.parse(response) as JSONObject
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        // Suche nach "jiraIssues"
        val issuesobj: JSONObject?
        issuesobj = json!!["jiraIssues"] as JSONObject
        //System.out.println(issuesobj);
        // Issues im Array
        val issuearray = issuesobj["issue"] as JSONArray
        for (i in issuearray) {
            val ji = i as JSONObject
            val jirakey = ji["key"] as String
            //logger.debug("Issue : ${jirakey}");
            // issueKey to List
            issueList.add(jirakey)
        }
        logger.debug("IssueList : $issueList")
        // Liste zurückgeben
        return issueList
    }

    /**
     * Funktion zum Auslesen der Test-Artefakte eines Bamboo-Build
     * - Auslesen der Bamboo-Build-Infos , expandiert um die Jira-Issues
     * - Auswertung des JSON-Objekts
     * - Ausgabe der Artefakte / Pfade in eine Datei (artifacts.txt)
     *
     * @param [plankey] Der Bamboo-PlanKey (z.B. "MCBS-MR63") zu dem die Issues ausgelesen werden sollen
     * @param [buildnumber] Die laufende Nummer des Bamboo-Builds zu dem die Issues ausgelesen werden sollen
     *
     * @return ??
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getTestArtifactsForBuild(plankey : String , buildnumber : String) {
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        // URL setzen
        val webResource = client.resource("${bambooURL}${plankey}-${buildnumber}?expand=artifacts")
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Result : $response")
        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONObject?
        try {
            // Datei zum Schreiben öffnen
            val writer = PrintWriter("artifacts.txt")  // java.io.PrintWriter
            // Antwort parsen
            json = parser.parse(response) as JSONObject
            println(json.toString())
            // Suche nach "jiraIssues"
            val artifactssobj: JSONObject?
            artifactssobj = json["artifacts"] as JSONObject
            println(artifactssobj)
            // Issues im Array
            val artifactsarray = artifactssobj["artifact"] as JSONArray
            for (a in artifactsarray) {
                // ArrayElement isolieren
                val x = a as JSONObject
                logger.debug("x (ArrayElement) : $x")
                val name = x["name"]
                if (name != null) {
                    if((name == "business-common-result") || (name == "contract-result") || (name == "fee-result") || (name == "invoice-result") || (name == "mark-result") || (name == "network-result") || (name == "party-result") || (name == "stock-result") || (name == "cucumber-result") || (name == "junit-result")){
                        logger.info("Name : $name")
                        val l = x["link"] as JSONObject
                        logger.debug("L (Link-Objekt) : $l")
                        val link = l["href"] as String
                        logger.info(link)
                        // Eintrag in die Datei
                        writer.append("${name}=${link}\n")
                    }
                }

            }
            // Datei schliessen
            writer.close()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    /**
     * Funktion zum Erzeugen einer für das Deployment erforderlichen Release-Version
     *
     * @param [??]
     *
     * @return ??
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun createReleaseVersion(project : String , planResultKey : String , version : String) {
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        // data
        val data = "{\"planResultKey\": \"${planResultKey}\",\"name\": \"${version}\"}"
        // URL setzen
        try {
            val webResource = client.resource("https://bambooweb.mobilcom.de/rest/api/latest/deploy/project/${project}/version")
            val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(String::class.java, data)
            logger.debug("Result : $response")
        } catch(e : Exception) {
            logger.info("Fehler beim Anlegen der Version ...")
            logger.info(e.printStackTrace())
        }

    }
}
