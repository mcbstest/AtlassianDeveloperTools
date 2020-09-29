package sonar_issue


import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger


/**
 * Methode um JIRA-Sonar-Issues zu erzeugen, zuzuweisen und zu beobachten
 * - Erzeugung des Issue im angegebenen Projekt
 * - interne Zuweisung zur weiteren Bearbeitung
 * - Beobachter werden gesetzt
 * - die endgültige Zuweisung erfolgt manuell nach der Vervollständigung
 *
 * Aufruf :
 * java -Dlog4j.configuration=./log4j_debug.properties -cp ./devtools/apps/atltools/AtlassianDeveloperTools-all-1.1.jar:.:./devtools/apps/atltools/AtlassianDeveloperTools sonar_issue.SonarIssueKt https://jira.freenet-group.de/rest/api/2/ mcbstest:qs_mcbs_11 ABRMS Fehler bmoeller bmoeller "${bamboo.release.version}" product 80 79
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [project] das JiraProjekt, in dem der DB-Issue erzeugt werden soll
 * @param [ordertype] der OrderType als Text (e.g. Aufgabe , Service-Auftrag)
 * @param [assignee] der username / login
 * @param [watcher] eine komma-separierte Liste der anzulegenden Beobachter
 * @param [version] die versionsnummer
 * @param [product] Die Produktbezeichnung
 * @param [limit] die geforderte Coverage
 * @param [coverage] die tatsächliche Coverage
 * @param [awt] die Anforderungs-Id]
 *
 * @author bmoeller
 *
 * @since 1.5.0
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    val jiraURL : String
    val credentials : String
    val project : String
    val ordertype : String
    val assignee : String
    var watchlist : String
    var version : String
    val product : String
    val limit : String
    val coverage : String
    val awt : String

    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    logger.debug(args.toString())
    jiraURL = args[0]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    project = args[2]
    logger.info("Project : $project")
    ordertype = args[3]
    logger.info("Order : ${ordertype}")
    assignee = args[4]
    logger.info("Assignee : ${assignee}")
    watchlist = args[5]
    val watchers  = watchlist.split(",")
    logger.info("Watchers : ${watchers}")
    version = args[6]
    logger.info("Version : ${version}")
    product = args[7]
    logger.info("Produkt : ${product}")
    limit = args[8]
    logger.info("Limit : ${limit}")
    coverage = args[9]
    logger.info("Coverage : ${coverage}")
    awt = args[10]
    logger.info("Anf-Id : ${awt}")


    val j = AtlassianJiraIssue(jiraURL, credentials)

    val d1 = "Im Zuge des Releasebuild von :\\r\\n\\r\\n ${product} :: ${version} \\r\\n\\r\\n"
    val d2 = "wurde die Test-Coverage von : ${limit} % unterschritten ... \\r\\n\\r\\n "
    val d3 = "\\r\\n\\r\\n "
    val d4 = "Aktueller Stand : ${coverage} % \\r\\n\\r\\n"
    val d5 = "Bitte die Test-Abdeckung erhöhen !\\r\\n\\r\\n "

    val description = d1+d2+d3+d4+d5+"Danke! "
    logger.info(description)
    var issue = j.createError("${project}", "Test-Coverage zu ${product} :: ${version} :: ${coverage} % <= ${limit} %", "${description}", " ", "${product}", "${awt}")
    logger.info("Issue : ${issue}")
    // interne Zuweisung
    j.setAssignee(issue,assignee)
    // Beobachter
    watchers.forEach { watcher:String ->
        j.addWatcher(issue, watcher)
    }

    logger.info("|${issue}|")
}