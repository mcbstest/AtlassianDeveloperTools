package atlassian_jira

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.core.util.Base64
import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * JIRA-API-Access
 *
 * Jira-Access via Atlassian-Jira-REST-API
 *
 * @property jiraURL : die URL fuer produktiven JIRA-Zugriff
 * @property credentials : login:password fuer produktiven JIRA-Zugriff
 * @property logger : log4j.properties ...
 * @constructor setzt die JIRA-URL und die credentials
 *
 * @version 1.0.0
 *
 * @author bmoeller
 */
class AtlassianJiraIssue(val jiraURL : String , val credentials : String) {

    val logger: Logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)

     /**
     * Einlesen eines Property-Files aus dem jiraResources-Verzeichnis. <br></br>
     * Das Property-File enthält unter dem key "components" eine Liste der zum Projekt gehörenden Komponenten <br></br>
     * Der Name ergibt sich aus einer übergebenen Variablen, die im Bamboo-Kontext als Variable an das Skript übergeben werden muss. <br></br>
     * Selbst erzeugte Konvention : "'Bamboo-Projekt'-'Bamboo-Plan' <br></br>
     *
     * @param  propFile Der Name des Property-Files in der Konvention 'Bamboo-Projekt'-'Bamboo-Plan' ohne Pfad oder Extension
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun readProperties(propFile: String): Properties {
        val prop = Properties()
        val input: InputStream?
        try {
            input = FileInputStream("./jiraResources/$propFile.properties")
            // load a properties file
            prop.load(input)
            // get the property value and print it out
            // System.out.println(prop.getProperty("components"));
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return prop
    }

    /**
     * Erzeugen eines neuen JIRA-Issue (TestOrder oder DB-Change-Order)
     * @param [project] das JiraProjekt, in dem der Issue zu erzeugen ist (e.g. MCBS)
     * @param [ordertype] der jira-order-type als text (e.g. Aufgabe , Service-Auftrag)
     * @param [summary] die brief-description / headline (QS :: Version : Summary)
     * @param [description] der eigentliche Inhalt des Issue
     * @param [ibn] der mögliche Zieltermin (e.g. IBN! : xx.yy.zzzz OR " " )
     * @return die Systemantwort
     */
    fun createIssue(project : String , ordertype : String , version : String, summary : String , description : String , ibn : String ) : String {
        logger.info("createIssue ...")
        //
        val desc = description + ibn
        logger.debug("Desc : $desc")
        // Data
        val data = "{\"fields\":{\"project\":{\"key\":\"$project\"},\"issuetype\":{\"name\":\"$ordertype\"},\"description\":\"$desc\" ,\"summary\":\"$summary\"}}"
                   // fehler data = "{\"fields\":{\"project\":{\"key\":\"MCBS\"},\"issuetype\":{\"name\":\"Auftrag\"},\"summary\":\"MCBS-Services\"}}"
                   // data = "{\"fields\":{\"project\":{\"key\":\"MCBS\"},\"issuetype\":{\"name\":\"${ordertype}\"},\"description\":\"Desc\" ,\"summary\":\"sum\"}}"
        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").post(String::class.java, data)
        logger.debug("Response : $response")
        // Response auswerten
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        return json["key"].toString()

    }

    /**
     * Erzeugen eines neuen JIRA-Issue (TestOrder oder DB-Change-Order)
     * @param [project] das JiraProjekt, in dem der Issue zu erzeugen ist (e.g. MCBS)
     * @param [ordertype] der jira-order-type als text (e.g. Aufgabe , Service-Auftrag)
     * @param [summary] die brief-description / headline (QS :: Version : Summary)
     * @param [description] der eigentliche Inhalt des Issue
     * @return die Systemantwort
     */
    fun createJiraIssue(project : String , ordertype : String , summary : String , description : String , ibn : String ) : String {
        logger.info("createJiraIssue ...")
        //
        val desc = description
        logger.debug("Desc : $desc")
        // Data
        val data = "{\"fields\":{\"project\":{\"key\":\"$project\"},\"issuetype\":{\"name\":\"$ordertype\"},\"description\":\"$desc\" ,\"summary\":\"$summary\"}}"
        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").post(String::class.java, data)
        logger.debug("Response : $response")
        // Response auswerten
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        return json["key"].toString()

    }

    /**
     * Erzeugen eines neuen JIRA-Issue (beliebig)
     * @param [project] das JiraProjekt, in dem der Issue zu erzeugen ist (e.g. MCBS)
     * @param [ordertype] der jira-order-type als text (e.g. Aufgabe , Service-Auftrag)
     * @param [summary] die brief-description / headline (QS :: Version : Summary)
     * @param [description] der eigentliche Inhalt des Issue
     * @param [components]
     * @param [assignee]
     * @param [env]
     * @param [awt]]
     * @return die Systemantwort
     */
    fun createJiraIssue2(project : String , ordertype : String , summary : String , description : String , components : String , assignee : String , env : String , awt : String) : String {
        logger.info("createJiraIssue2 ...")
        //
        val desc = description
        logger.debug("Desc : $desc")
        val data : String
        if ( (project == "SPOC") && (ordertype == "Fehler") ) {
            // Data
            data = "{\"fields\":{\"project\":{\"key\":\"${project}\"},\"issuetype\":{\"name\":\"Fehler\"},\"description\":\"$desc\" ,\"summary\":\"${summary}\",\"components\":[{\"name\":\"${components}\"}],\"assignee\":{\"name\":\"${assignee}\"},\"environment\":\"${env}\", \"customfield_13402\":\"${awt}\" }}"
        } else if ((project == "SUPAPP") && (ordertype == "Aufgabe")) {
            // Data
            data = "{\"fields\":{\"project\":{\"key\":\"$project\"},\"issuetype\":{\"name\":\"$ordertype\"},\"description\":\"$desc\" ,\"summary\":\"$summary\"}}"
        } else {
            throw Exception("Fehlerhafte Issue-Konstellation !!")
        }
        logger.info("Data : $data")

        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").post(String::class.java, data)
        logger.debug("Response : $response")
        // Response auswerten
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        return json["key"].toString()

    }



    /**
     * Erzeugen eines neuen JIRA-META-Issue (EPIC)
     * @param [mcbsrelease] die Nummer des MCBS-Release (e.g. 51)
     * @param [dev] der Beginn der Entwicklung
     * @param [git] der GIT-Start
     * @param [ibn] der geplante IBN-Termin
     * @return die Systemantwort
     */
    fun createMetaIssue(mcbsrelease : String, dev : String , git : String , ibn : String , sprintname : String) : String {
        logger.info("createMETAIssue ...")
        //
        val desc = "MCBS_$mcbsrelease :: $sprintname"
        val summary = "MCBS-Release $mcbsrelease"
        val eposname = "mcbs_$mcbsrelease"
        logger.debug("Desc : $desc")
        // Data
        val data = "{\"fields\":{\"project\":{\"key\":\"META\"},\"issuetype\":{\"name\":\"Epic\"},\"description\":\"$desc\" ,\"summary\":\"$summary\",\"customfield_13792\":\"$eposname\",\"customfield_10859\":\"$dev\",\"customfield_33092\":\"$git\",\"customfield_10863\":\"$ibn\"}}"
        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").post(String::class.java, data)
        logger.debug("Response : $response")
        // Response auswerten
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        return json["key"].toString()

    }


    /**
     * Erzeugen eines neuen JIRA-Error
     * @param [project] das JiraProjekt, in dem der Issue zu erzeugen ist (e.g. MCBS)
     * @param [summary] die brief-description / headline (QS :: Version : Summary)
     * @param [description] der eigentliche Inhalt des Issue
     * @param [ibn] der mögliche Zieltermin (e.g. IBN! : xx.yy.zzzz OR " " )
     * @param [comp] die betroffene Komponente
     * @param [awt] die Anforderungs-Id
     * @return die Systemantwort
     */
    fun createError(project: String, summary: String, description: String, ibn: String, comp: String, awt: String) : String {
        logger.info("createError ...")
        //
        val desc = description + ibn
        logger.debug("Desc : $desc")
        // Data
        val data = "{\"fields\":{\"project\":{\"key\":\"${project}\"},\"issuetype\":{\"name\":\"Fehler\"},\"description\":\"$desc\" ,\"summary\":\"${summary}\",\"components\":[{\"name\":\"${comp}\"}],\"assignee\":{\"name\":\"bmoeller\"},\"environment\":\"Sonar\", \"customfield_13402\":\"${awt}\" }}"

        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").post(String::class.java, data)
        logger.debug("Response : $response")
        // Response auswerten
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        return json["key"].toString()

    }


    /**
     * Funktion zum Setzen eines remote-links an einem Jira-Issue
     */
    fun setConfluenceLink (release : String , title : String ,issue : String) {
        logger.info("Create Remote-Link ...")
        val data = "{\"applicationType\":\"com.atlassian.confluence\", \"relationship\":\"mentioned in\",\"object\":{\"icon\": { \"url16x16\":\"http://www.openwebgraphics.com/resources/data/149/book_open.png\", \"title\":\"Support Ticket\" } , \"url\":\"https://kiwi.freenet-group.de/display/AERP/$release\",\"title\":\"$title\"}}"
        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource("https://jira.freenet-group.de/rest/api/2/issue/$issue/remotelink")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
            .accept("application/json").post(String::class.java, data)
        logger.info("Response : $response")

    }


    /**
     * Zuweisung eines gegebenen JiraIssue an einen User (= Login)
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [assignee] der zuzuweisende Benutzer (i.e. sein Login)
     */
    fun setAssignee(issue : String , assignee : String) {
        logger.debug("setAssignee ...")
        // Bearbeiter
        val data = "{\"name\":\"$assignee\"}"
        logger.debug("Assignee : $assignee")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/${issue}/assignee")
        logger.debug("${jiraURL}issue/${issue}/assignee")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").put(ClientResponse::class.java, data)
        // State
        logger.debug("Status : ${response.status}")
    }


    /**
     * Hinzufuegen eines Beobachters zu einem gegebenen JiraIssue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [watcher] der hinzuzufuegende Beobachter
     */
    fun addWatcher(issue : String , watcher : String) {
        logger.debug("addWatcher ...")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        val url = jiraURL+"issue/$issue/watchers"
        logger.debug("URL : $url")
        // Connect
        val webResource = client.resource(url)
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(ClientResponse::class.java, "\""+watcher+"\"")
        // State
        logger.debug("Status : ${response.status}")
    }


    /**
     * Methode zur Ermittlung der SemanticVersion einer Jira-FixVersion
     *
     * Es werden die Issues abgefragt, die über die übergebene FixVersion verfügen.
     * Aus diesen Issues wird die höchste SemanticVersion aus den Keywords extrahiert.
     * @author : Bernd Möller
     *
     * @param [version] : Die Jira-FixVersion, deren SemanticVersion ausgelesen werden soll
     *
     * @return Eine Zeichenkette, die die SemanticVersion des Release enthält
     *
     * @version 1.1
     *
     * @since 1.1
     */
    fun getSemanticVersionForReleaseNew(version: String): String {
        logger.info("getSemanticVersion ...")
        val semVerList = java.util.ArrayList<String>()
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        val query="fixVersion=$version"
        logger.info(query)
        // Connect
        val webResource = client.resource(jiraURL+"search?jql="+query)
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Response : $response)")
        // ResultString to JSON
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        val count  = json["total"]
        logger.info("Anzahl : $count")
        val issues = json["issues"] as JSONArray
        logger.info("Anz : $issues.size")
        issues.forEach {
            val entry : JSONObject = it as JSONObject
            val i = entry["key"].toString()
            logger.info("Key : $i")
            val fields = entry["fields"]
            logger.debug("Fields : $fields")
            // dann die Keywords auslesen
            val semver = getSemanticVersionForIssue(i)
            logger.info(semver)


            val k : String
            // SemanticVersion auswerten und in Liste schreiben
            k = when {
                semver.toUpperCase() == "PATCH" -> "Patch"
                semver.toUpperCase() == "MINOR" -> "Minor"
                semver.toUpperCase() == "MAJOR" -> "Major"
                else -> "None"
            }
            logger.info("$i :: $k")
            semVerList.add(k)


        }
        logger.info("Semantic version s : $semVerList")
        val s : String
        s = when {
            semVerList.contains("Major") -> "Major"
            semVerList.contains("Minor") -> "Minor"
            semVerList.contains("Patch") -> "Patch"
            else -> "Patch"
        }
        logger.info("SemanticVersion : $s")

        return s
    }



    /**
     * Methode zur Ermittlung der Zusammenfassung eines Jira-Issue
     *
     * @author : Bernd Möller
     *
     * @param [issue] : Der Jira-Issue, dessen Zusammenfassung ausgelesen werden soll
     *
     * @return Eine Zeichenkette, die die Zusammenfassung enthält
     *
     * @version 1.1
     *
     * @since 1.1
     */
    fun getSummary(issue: String) : String {
        logger.debug("getSummary ...")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue)
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Response : $response)")
        // ResultString to JSON
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        // fields herausziehen
        val fields  = json["fields"]
        logger.debug("Fields : $fields")
        val f: JSONObject = json["fields"] as JSONObject
        // issue_type herausziehen
        val summary = f["summary"]
        logger.info("Summary zu $issue : $summary")
        return summary.toString()
    }



    /**
     * Auslesen des state eines gegebenen JiraIssue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @return der Status des JiraIssue
     */
    fun getIssueState(issue : String) : String {
        logger.info("getIssueState ...")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue+"?expand=components")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Response : $response)")
        // ResultString to JSON
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        // fields herausziehen
        val fields  = json["fields"]
        logger.debug("Fields : $fields")
        val f: JSONObject = json["fields"] as JSONObject
        // status herausziehen
        val status = f["status"]
        logger.debug("Status : $status")
        val s: JSONObject = f["status"] as JSONObject
        // status.name
        val issuestate = s["name"]
        logger.info("aktueller Status : $issuestate")
        return issuestate.toString()
    }

    /**
     * Setzen des Status eines gegebenen JiraIssue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [transition] der transition-code (jira) , der erforderlich ist, um den nächsten Status zu erreichen (e.g. 31)
     */
    fun setIssueState(issue: String, transition: String) {
        logger.info("setIssueState ...")
        // Datenaenderung
        val data =  "{\"transition\":{\"id\": \"$transition\"}}"
        logger.debug("Data : $data")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue+"/transitions")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(ClientResponse::class.java, data)
        // Result
        logger.info("setIssueState : $response.status")
        if (response.status >= 300) {
            logger.debug("setIssueState $issue $transition : $response")
            logger.info("Fehler beim Setzen des Status ! ")
            getIssueState(issue)
        }
    }


    /**
     * Abfrage der möglichen Statusübergänge an einem JIRA-Issue
     */
    fun getTransitions(issue : String) {
        logger.info("getTransitions")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        //val uri = jiraURL+"issue/"+issue+"/transitions"
        //val webResource = client.resource(URLEncoder.encode(uri))
        val webResource = client.resource(jiraURL+"issue/"+issue+"/transitions")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        // Result
        logger.info("Transitions : $response")
    }



    /**
     * Setzen der "Version for Test"
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [version] einfache Zeichenkette als Versionskenner e.g. "1.0.19"
     */
    fun setTestVersion(issue: String, version: String) {
        logger.info("setTestVersion ...")
        // Datenaenderung
        val data = "{\"fields\": {\"customfield_41591\":\"${version}\"}}"
        logger.debug("Data : $data")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue+"?expand=components")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").put(ClientResponse::class.java, data)
        // Result
        logger.info("setTestVersion : ${response.status}")
        if (response.status >= 300) {
            logger.debug("setTestVersion $version $issue : response.toString()")
        }
    }

    /**
     * Setzen der "Version for Git"
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [version] einfache Zeichenkette als Versionskenner e.g. "1.0.19"
     */
    fun setGitVersion(issue: String, version: String) {
        logger.info("setGitVersion ...")
        // Datenaenderung
        val data = "{\"fields\": {\"customfield_41592\":\"${version}\"}}"
        logger.debug("Data : $data")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue+"?expand=components")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").put(ClientResponse::class.java, data)
        // Result
        logger.info("setGitVersion : ${response.status}")
        if (response.status >= 300) {
            logger.debug("setGitVersion $version $issue : response.toString()")
        }
    }


    /**
     * Setzen der "Version for Prod"
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [version] einfache Zeichenkette als Versionskenner e.g. "1.0.19"
     */
    fun setProdVersion(issue: String, version: String) {
        logger.info("setProdVersion ...")
        // Datenaenderung
        val data = "{\"fields\": {\"customfield_41593\":\"${version}\"}}"
        logger.debug("Data : $data")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue+"?expand=components")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").put(ClientResponse::class.java, data)
        // Result
        logger.info("setProdVersion : ${response.status}")
        if (response.status >= 300) {
            logger.debug("setProdVersion $version $issue : response.toString()")
        }
    }


    /**
     * Auswertung des "Datum der IBN" für ein gegebenes META-Issue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @return das "Datum der IBN" als Zeichenkette  ("01.12.2018")
     */
    fun getIBNDate(issue: String) :String {
        logger.info("getIBNDate ...")
        // Athentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource("${jiraURL}issue/${issue}")
        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Response : $response)")
        // ResultString to JSON
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        // fields herausziehen
        val fields  = json["fields"]
        logger.debug("Fields : $fields")
        val f: JSONObject = json["fields"] as JSONObject
        // Datum definieren bzw. extrahieren
        var ibnDate = "undefined"
        val d : String = f["customfield_10863"].toString()
        if (d != "null") {
            logger.info("IBNDate : $d")
            val date: Date = SimpleDateFormat("yyyy-MM-dd").parse(d)
            ibnDate = SimpleDateFormat("dd.MM.yyyy").format(date)
        } else {
            logger.info("IBN-Date is null")
        }
        logger.info("IBN-Date : $ibnDate")
        // return the date-string
        return ibnDate
    }

    /**
     * Hinzufügen eines Kommentars zu einem gegebenen JiraIssue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     * @param [comment] einfache Zeichenkette als Kommentar
     */
    fun addComment(issue : String, comment : String) {
        logger.info("addComment ...")
        val data = "{\"body\": \"$comment\"}"
        logger.debug("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issue/"+issue+"/comment")
        // Request
        try {
            val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(ClientResponse::class.java, data)
            logger.info("Response : $response.status")
        } catch(e : Exception){
            logger.info("Fehler beim Anlegen des Kommentars ...")
            logger.info(e.stackTrace)
        }
    }


    /**
     * Setzen einer Releation zwischen META-Issue und Test-Order
     * @param [issue] der JiraIssue (META-Issue) (pattern : MCBS-1234)
     * @param [testOrder] die TestOrder (JiraIssue) e.g. "MCBS-1234"
     */
    fun setTestOrder(issue : String, testOrder : String){
        logger.info("setTestOrder ...")
        //val data = '{"type":{"name":"Dependency"},"inwardIssue":{"key":"'+ issue +'"},"outwardIssue":{"key":"'+ testOrder +'"}}'
        val data = "{\"type\":{\"name\":\"Dependency\"},\"inwardIssue\":{\"key\":\"$issue\"},\"outwardIssue\":{\"key\":\"$testOrder\"}}"
        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"issueLink")
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(ClientResponse::class.java, data)
        // Kontrollausgabe
        logger.info("Response : $response.status")
    }


    /**
     * Auslesen der TestOrders eines gegebenen META-Issue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     */
    fun getTestOrder(issue : String) : ArrayList<String>  {
        logger.info("getTestOrder ...")
        //var orderList! : ArrayList<String> =
        val orderList: ArrayList<String> = arrayListOf()
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        // client einrichten
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL + "issue/"+issue)
        // Aufruf des Bamboo-Result
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug("Response : $response)")
        // ResultString to JSON
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        // retrieve fields
        val f: JSONObject = json["fields"] as JSONObject
        // retrieve issuelinks
        //val issues = f.get("issuelinks")
        //logger.debug("Issues : $issues")
        val issueList = f["issuelinks"] as JSONArray
        // retrieve array of entries
        for (x in 0 until issueList.size) {
            // use array-entry as object
            val entry : JSONObject = issueList[x] as JSONObject
            //logger.debug(entry.get("outwardIssue"))
            // retrieve outwarIssue
            try {
                val oi : JSONObject = entry["outwardIssue"] as JSONObject
                // retrieve relation
                val relation : JSONObject = entry["type"] as JSONObject
                logger.debug(relation["name"])
                // if "DEPENDENCY"
                if (relation["name"] == "Dependency") {
                    logger.debug(oi["key"])
                    // add key to list
                    orderList.add(oi["key"].toString())
                }
            } catch(e : Exception){
                logger.debug("No valid dependency !")
            }
        }
        logger.info(orderList.toString())
        return orderList
    }


    /**
     * Auslesen der Referenz-Issues eines gegebenen Issue
     * @param [issue] der JiraIssue (pattern : MCBS-1234)
     */
    fun getReferencedIssue(issue : String) : ArrayList<String>  {
        logger.info("getReferencedIssue ...")
        //var orderList! : ArrayList<String> =
        val orderList: ArrayList<String> = arrayListOf()
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        // client einrichten
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL + "issue/"+issue)
        // Aufruf des Bamboo-Result
        val response : String
        try {
            response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
            // logger.debug("Response : $response)")

            // ResultString to JSON
            val parser = JSONParser()
            val json = parser.parse(response) as JSONObject
            // retrieve fields

            val f: JSONObject = json["fields"] as JSONObject
            // retrieve issuelinks
            //val issues = f.get("issuelinks")
            //logger.debug("Issues : $issues")
            val issueList = f["issuelinks"] as JSONArray
            // retrieve array of entries
            for (x in 0 until issueList.size) {
                // use array-entry as object
                val entry : JSONObject = issueList[x] as JSONObject
                //logger.debug(entry.get("outwardIssue"))
                // retrieve outwarIssue
                try {
                    val oi : JSONObject = entry["inwardIssue"] as JSONObject
                    // retrieve relation
                    val relation : JSONObject = entry["type"] as JSONObject
                    logger.debug(relation["name"])
                    //println("##################")
                    //println(relation.get("name"))
                    // if "implements"
                    if (relation["name"] == "implements") {
                        val key = oi["key"].toString()
                        logger.debug(key)
                        // add key to list
                        orderList.add(oi["key"].toString())
                    }
                } catch(e : Exception){
                    logger.debug("No valid reference !")
                }
            }


        } catch (e:Exception) {
            logger.info(e.message)
        }

        //logger.info(orderList.toString())
        //println(orderList.toString())
        return orderList
    }


    /**
     * Funktion zum Auslesen der Komponenten eines Jira-Issues
     * - Auslesen des uebergebenen Issues , expandiert um die Komponenten
     * - Auswertung des JSON-Objects
     * - Rueckgabe der einzelnen Komponenten in einer Liste
     *
     * @param issue Das Ticket, zu dem die Komponenten ermittelt werden sollen
     *
     * @return compList Liste der Komponenten (nur) dieses Tickets
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getComponentsForIssue(issue: String): ArrayList<String> {
        // Liste für Komponenten des Tickets
        val compList = ArrayList<String>()
        logger.info("Issue : $issue")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        //static String jiraURL = "https://jira.freenet-group.de/rest/api/2/issue/";
        logger.debug("${jiraURL}issue/${issue}?expand=components")
        val webResource = client.resource("${jiraURL}issue/${issue}?expand=components")
        // Aufruf
        // val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        val r = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(ClientResponse::class.java)
        logger.debug(r.status)
        if ( (r.status != 404) && (r.status != 406) ) {


            val response = r.getEntity(String::class.java)

            // ResultString to JSON
            val parser = JSONParser()
            var json: JSONObject? = null
            try {
                json = parser.parse(response) as JSONObject
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            // logger.debug("JSON : ${json}")
            // Suche nach fields
            val fieldsobj: JSONObject?
            fieldsobj = json!!["fields"] as JSONObject
            // logger.debug("fieldsobj : ${fieldsobj}")
            // compobj = (JSONObject) fieldsobj.get("components");
            val components = fieldsobj["components"] as JSONArray
            for (comp in components) {
                val jsonNumber = comp as JSONObject
                val cn = jsonNumber["name"] as String
                logger.debug("Component : $cn")
                compList.add(cn)
            }
        } else {
            logger.info(r.status)
        }
        logger.info("Components : $compList")
        return compList
    }


    /**
     * Funktion zum Setzen der FixVersion. <br></br>
     * Jira-API-Aufruf zum Schreiben der Daten eines Jira-Issues. <br></br>
     * Konkret wird als Datenstruktur eine 'update'-Anweisung gesetzt, <br></br>
     * das array 'fixVersions' um einen Eintrag ergaenzt  <br></br>
     *
     * @param  issue Das zu prüfende und ggf. zu modifizierende Ticket
     * @param  version Die ggf. als fixVersion zu setzende Version
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun addFixVersion(issue: String, version: String) {
        logger.info("Set Issue : $issue to FixVersion : $version : ")
        // update-statement
        val data = "{\"update\": {\"fixVersions\" : [{\"add\":{\"name\" : \"$version\"}}]}}"
        logger.debug("Data : $data")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        // Connect
        val webResource = client.resource("${jiraURL}issue/${issue}")
        // Aufruf

        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").put(ClientResponse::class.java, data)

        val statusCode = response.status
        if (statusCode == 204) {
            logger.info("OK !")
        } else {
            logger.info("FEHLER !")
            logger.info("Response : $response")
        }
    }

    /**
     * Funktion zum Erzeugen einer Version. <br></br>
     * Die uebergebene Versionskennung wird im uebergebenen Projekt angelegt <br></br>
     *
     * @param [project] Das Jira-Projekt, in dem die Version zu erzeugen ist
     * @param  [version] Die zu erzeugende Version
     * @return Die Id der erzeugten Version
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     *
     */
    fun createVersion(project : String, version : String) : String{
        logger.info("Create Version ...")
        // data
        val data = "{\"name\": \"${version}\",\"project\": \"${project}\"}"
        logger.info("Data : $data")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        // Connect
        logger.info("${jiraURL}version")
        val webResource = client.resource("${jiraURL}version")
        // Aufruf
        val id : String
        id = try {
            val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(String::class.java, data)
            logger.info("Response : $response")
            // ResultString to JSON
            val parser = JSONParser()
            val json: JSONObject = parser.parse(response) as JSONObject
            logger.info("Version-Id (${version}) : " + json["id"])
            json["id"].toString()
        } catch(e : Exception) {
            logger.info("Fehler beim Anlegen der Version ...")
            logger.info(e.stackTrace)
            "0"
        }
        return id
    }

    /**
     * Funktion zum Erzeugen einer ComponentVersion. <br></br>
     * Die uebergebene VersionsId wird mit der uebergebenen Komponente verknüpft <br></br>
     *
     * @param [versionid] Die Versions-Id, die zuvor erzeugt worden ist
     * @param  [component] Die zu verknuepfende Komponente
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun createComponentVersion(versionid : String , component : String) {
        logger.info("Create ComponentVersion for $component")
        // componentVersion setzen
	    val data = "{\"componentId\": \"${component}\" , \"versionId\": \"${versionid}\"}"
        logger.info("Data : $data")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL)
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(String::class.java, data)
        logger.info("Response : $response")


    }

    /**
     * Funktion zum Auslesen der Projektkomponenten in eine HashMap <br></br>
     *
     * @param [project] Der Projekt-Name
     * @return  Eine HashMap mit <"name","id">
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun getProjectComponentsToMap(project : String) : HashMap<String, String> {
        // HashMap to be returned
        val compmap = hashMapOf<String, String>()
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        val webResource = client.resource("${jiraURL}project/${project}/components")
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(response.toString())
        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONArray = parser.parse(response) as JSONArray
        // put components:id to HashMap
        for (comp in json){
            val jsoncomp = comp as JSONObject
            val x = jsoncomp["name"] as String
            val y = jsoncomp["id"] as String
            compmap[x] = y
        }
        return compmap
    }


    /**
     * Funktion zum Auslesen der Keywords eines Jira-Issues
     * - Auslesen des uebergebenen Issues , expandiert um die Komponenten
     * - Auswertung des JSON-Objects
     * - Rueckgabe der Keywords in einer Zeichenkette
     *
     * @param issue Das Ticket, zu dem die Komponenten ermittelt werden sollen
     *
     * @return keywords Liste mit den vergebenen Keywords
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getKeywordsForIssue(issue: String):  ArrayList<String> {
        // Liste für Komponenten des Tickets
        logger.debug("Issue : $issue")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        //static String jiraURL = "https://jira.freenet-group.de/rest/api/2/issue/";
        val webResource = client.resource("${jiraURL}issue/${issue}?expand=components")
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(response.toString())
        // ResultString to JSON
        val parser = JSONParser()
        var json: JSONObject? = null
        try {
            json = parser.parse(response) as JSONObject
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        logger.debug("JSON : $json")
        // Suche nach fields
        val fieldsobj: JSONObject?
        fieldsobj = json!!["fields"] as JSONObject
        logger.debug("fieldsobj : $fieldsobj")
        // compobj = (JSONObject) fieldsobj.get("components");
        val labels = fieldsobj["labels"] as ArrayList<String>
        logger.debug("Labels : $labels")
        println(labels)
        return labels
    }


    /**
     * Funktion zum Auslesen der SemanticVersion eines Jira-Issues
     * - Auslesen des uebergebenen Issues , expandiert um die Komponenten
     * - Auswertung des JSON-Objects
     * - Rueckgabe der SemanticVersion in einer Zeichenkette
     *
     * @param issue Das Ticket, zu dem die Komponenten ermittelt werden sollen
     *
     * @return String
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getSemanticVersionForIssue(issue: String):  String {
        val semver : String
        // Liste für Komponenten des Tickets
        logger.debug("Issue : $issue")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        //static String jiraURL = "https://jira.freenet-group.de/rest/api/2/issue/";
        val webResource = client.resource("${jiraURL}issue/${issue}?expand=components")
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(response.toString())
        // ResultString to JSON
        val parser = JSONParser()
        var json: JSONObject? = null
        try {
            json = parser.parse(response) as JSONObject
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        logger.debug("JSON : $json")
        // Suche nach fields
        val fieldsobj: JSONObject?
        fieldsobj = json!!["fields"] as JSONObject
        logger.debug("fieldsobj : $fieldsobj")

        semver = try {
            val semverobj = fieldsobj["customfield_53394"] as JSONObject
            semverobj["value"].toString()
        } catch (e : Exception) {
            logger.info("Keine gesetzte SemanticVersion ...")
            // throw Exception("Keine gesetzte SemanticVersion !!")
            "Patch"
        }

        logger.info(semver)

        return semver

    }


    /**
     * Funktion zum Auslesen der Dependency eines Jira-Issues
     * - Auslesen des uebergebenen Issues , expandiert um die Komponenten
     * - Auswertung des JSON-Objects
     * - Rueckgabe von "Issue : Dependency \n"  in einer Zeichenkette
     *
     * @param issue Das Ticket, zu dem die Komponenten ermittelt werden sollen
     *
     * @return String
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getDependencyForIssue(issue: String):  String {
        var dep : String
        // Liste für Komponenten des Tickets
        logger.debug("Issue : $issue")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        //static String jiraURL = "https://jira.freenet-group.de/rest/api/2/issue/";
        val webResource = client.resource("${jiraURL}issue/${issue}?expand=components")
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(response.toString())
        // ResultString to JSON
        val parser = JSONParser()
        var json: JSONObject? = null
        try {
            json = parser.parse(response) as JSONObject
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        logger.debug("JSON : $json")
        // Suche nach fields
        val fieldsobj: JSONObject?
        fieldsobj = json!!["fields"] as JSONObject
        logger.debug("fieldsobj : $fieldsobj")

        try {
            dep = fieldsobj["customfield_54490"].toString()
            logger.info("$issue : $dep")

        } catch (e : Exception) {
            logger.info("Keine gesetzte Dependency ...")
            // throw Exception("Keine gesetzte SemanticVersion !!")
            dep="null!"
        }

        logger.debug("D : $dep")

        return dep

    }



    /**
     * Funktion zum Setzen der Epos-Version an einem Jira-Issue (customfield_13790).
     * Sofern der Jira-Issue bereits geschlossen ist, wird die Aktion auf der Konsole mit "Fehler !" quittiert
     *
     * @param    issue    Das Jira-Ticket
     * @param    epic    Die uebergebene EPOS-Version, die verlinkt werden soll.
     *
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun linkEpic(issue: String, epic: String) {
        logger.info("Set Issue : $issue to EPOS : $epic : ")
        // zu setzende Datenstruktur
        val data = "{\"fields\": {\"customfield_13790\":\"$epic\"}}"
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        val webResource = client.resource("${jiraURL}issue/${issue}")

        try {
            // Issue auslesen, EPIC isolieren
            val r = webResource.header("Authorization", "Basic $auth").type("application/json")
                    .accept("application/json").get(String::class.java)
            logger.debug("r : $r")
            val parser = JSONParser()
            var json: JSONObject? = null
            try {
                json = parser.parse(r) as JSONObject
            } catch (e: ParseException) {
                println("EXCEPTION !!!")
                e.printStackTrace()
            }
            //System.out.println(json);
            val j = json!!["fields"] as JSONObject
            // customfield_13790 leer ?
            if (j["customfield_13790"] == null) {
                // Aufruf
                val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                        .accept("application/json").put(ClientResponse::class.java, data)
                // System.out.println(response);
                val statusCode = response.status
                if (statusCode == 204) {
                    logger.info("OK !")
                } else {
                    logger.info("FEHLER ! Bad Request / Issue bereits geschlossen ?")
                }
            } else {
                logger.info("EPOS bereits gesetzt : " + j["customfield_13790"].toString())
            }
            //}
        } catch (e: Exception) {
            logger.info("Unable to identify issue : $issue... skip !")
            logger.info(e.toString())
        }
    }


    /**
     * Funktion zum Auslesen der IssueId <br></br>
     *
     * @param [issue] Das Ticket
     * @return  Eine Zeichenkette mit der numerischen IssueId
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun getIssueId(issue : String) : String {
        logger.info("Get IssueId : $issue ")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        val webResource = client.resource("${jiraURL}issue/${issue}")
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(response.toString())
        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONObject = parser.parse(response) as JSONObject
        // put components:id to HashMap
        val id = json["id"] as String
        logger.info("IssueId : $id")
        return id
    }

    /**
     * Funktion zum Auslesen der IssueId <br></br>
     *
     * @param [issue] Das Ticket
     * @return  Eine Zeichenkette mit der numerischen IssueId
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun getIssueInfos(issue : String) : ArrayList<String> {
        logger.debug("Get IssueInfos : $issue ")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        val webResource = client.resource("${jiraURL}issue/${issue}")
        // Aufruf

    try {

        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(response.toString())
        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONObject = parser.parse(response) as JSONObject
        // fields / infos
        val key = json["key"] as String
        logger.debug("Issue : $key")
        // fields herausziehen
        val f: JSONObject = json["fields"] as JSONObject
        // Summary
        val summary = f["summary"] as String
        logger.debug("Summary : $summary")
        // Status
        val s: JSONObject = f["status"] as JSONObject
        val status: String = s["name"].toString()
        logger.debug("Status : $status")
        // Komponenten
        // Liste für Komponenten des Tickets
        val compList = ArrayList<String>()
        val components = f["components"] as JSONArray
        for (comp in components) {
            val jsonNumber = comp as JSONObject
            val cn = jsonNumber["name"] as String
            logger.debug("1 Component : $cn")
            compList.add(cn)
        }
        logger.debug("Liste : $compList")
        // Semantic Version
        var semanticVersion: String = " "
        try {
            val semVer = f["customfield_53394"] as JSONObject
            semanticVersion = semVer["value"].toString()
            logger.debug("SemanticVersion : $semanticVersion")
        } catch (ex: TypeCastException) {
            // ex.printStackTrace()
            logger.debug("keine semanticVersion ...")
            semanticVersion = "Patch"
        }

        // Liste für Labels des Tickets
        val labels = f["labels"] as JSONArray
        logger.debug(labels)

        // Priority
        val prio = f["priority"] as JSONObject
        val priority = prio["name"].toString()
        logger.debug("Priority : $priority")
        // FixVersions
        // Liste für fixVersions des Tickets
        val fixversionList = ArrayList<String>()
        val fixVersions = f["fixVersions"] as JSONArray
        for (fv in fixVersions) {
            val jsonNumber = fv as JSONObject
            val f = jsonNumber["name"] as String
            logger.debug("1 fixVersion : $f")
            fixversionList.add(f)
        }
        logger.debug("Liste : $fixversionList")
        // Liste für fixVersions des Tickets
        val versionList = ArrayList<String>()
        val versions = f["versions"] as JSONArray
        for (v in versions) {
            val jsonNumber = v as JSONObject
            val singleVersion = jsonNumber["name"] as String
            logger.debug("1 Version : $singleVersion")
            versionList.add(singleVersion)
        }
        logger.debug("Liste : $versionList")
        // Dependencies
        var dependency: String = " "
        try {
            dependency = f["customfield_54490"] as String
        } catch (ex: TypeCastException) {
            // ex.printStackTrace()
            logger.debug("keine Dependency ...")
            dependency = "keine"
        }

        logger.debug("Dependency : $dependency")

        // Ausgabe
        logger.debug("Issue : $key,$summary,$status,$compList,$semanticVersion,$labels,$priority,$fixversionList,$versionList,$dependency ")
        // Rückgabe
        val issueInfo = ArrayList<String>()
        issueInfo.add(key)
        issueInfo.add(summary)
        issueInfo.add(status)
        issueInfo.add(compList.toString())
        issueInfo.add(semanticVersion)
        issueInfo.add(labels.toString())
        issueInfo.add(priority)
        issueInfo.add(fixversionList.toString())
        issueInfo.add(versionList.toString())
        issueInfo.add(dependency)
        return issueInfo

    } catch (e: Exception) {
        val nixInfo = ArrayList<String>()
        nixInfo.add("No Issue")
        return nixInfo
    }

    }

    /**
     * Funktion zum Auswerten einer JIRA-Query <br>
     * Hier : Abfrage der Issues ohne AnforderungsId <br>
     * Erzeugt eine Datei mit releavnten Infos zur weiteren Verarbeitung
     *
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun queryReqId(timeSpan : String , issueTypes : String, projects : String) {
        logger.info("queryIssues ...")
        // Ausgabedatei
        val file = File("missingRequirements.csv")
        file.writeText("Id;Type;Created;Summary;Assignee;Requirement-Id;\n")
        // Data
        val data = "{\"jql\":\"project in (${projects}) AND labels != 'X' AND issuetype in (${issueTypes}) AND 'Anforderungs ID' ~ 'PRIOMD-6696' AND created >= -${timeSpan}d\",\"maxResults\":200,\"fields\":[\"id\",\"issuetype\",\"key\",\"assignee\",\"summary\",\"created\"]}"
        logger.info("Data : $data")
        // Authentifizierung
        val auth = String(Base64.encode(credentials))
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL+"search")

        // Request
        val response = webResource.header("Authorization", "Basic $auth").type("application/json")
                .accept("application/json").post(String::class.java,data)
        logger.debug("Response : $response")
        // Response auswerten
        val parser = JSONParser()
        val json = parser.parse(response) as JSONObject
        val issues = json["issues"] as JSONArray
        issues.forEach {
            val entry: JSONObject = it as JSONObject
            val issue = entry["key"].toString()
            //println(issue)
            //val fields = entry.get("fields")
            val f: JSONObject = entry["fields"] as JSONObject
            // issue_type herausziehen
            val i: JSONObject = f["issuetype"] as JSONObject
            val issuetype = i["name"]

            val summary = f["summary"]
            //println(summary)
            var created = f["created"].toString()
            val date: Date = SimpleDateFormat("yyyy-MM-dd").parse(created)
            created = SimpleDateFormat("dd.MM.yyyy").format(date)

            val a: JSONObject = f["assignee"] as JSONObject
            val name = a["displayName"]

            logger.info("https://jira.freenet-group.de/browse/${issue} ;${issuetype} ;${created} ;${summary} ;${name} ;")
            file.appendText("https://jira.freenet-group.de/browse/${issue} ;${issuetype} ;${created} ;${summary} ;${name} ; \n")
        }

    }


    /**
     * Funktion zum Erzeugen eines Sprints
     * auf Basis einer CSV-Datei
     *
     * @param [versionid] Die Versions-Id, die zuvor erzeugt worden ist
     * @param  [component] Die zu verknuepfende Komponente
     * @author Bernd Moeller
     * @version 1.0
     * @since 1.0
     */
    fun addSprint(name : String , goal : String , board : String ) {
        logger.info("Create Sprint ...")
        // componentVersion setzen
        val data = "{\"name\": \"${name}\" , \"goal\": \"${goal}\", \"originBoardId\": \"${board}\"}"
        logger.info("Data : $data")
        // Authentifikation
        val auth = String(Base64.encode(credentials))
        // Client einrichten
        val client = Client.create()
        // Connect
        val webResource = client.resource(jiraURL)
        // Aufruf
        val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").post(String::class.java, data)
        logger.info("Response : $response")


    }



}
