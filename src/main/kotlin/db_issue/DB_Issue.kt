package db_issue

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.FileOutputStream
import java.util.*
import kotlin.system.exitProcess

/**
 * Methode um JIRA-DB-Change-Orders zu erzeugen, zuzuweisen und zu beobachten
 * - Erzeugung des Issue im Projekt "SPOC"
 * - interne Zuweisung zur weiteren Bearbeitung (noch nicht an DBA)
 * - Beobachter werden gesetzt
 * - die endgültige Zuweisung erfolgt manuell nach der Vervollständigung
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. db_issue.DB_IssueKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password SPOC Service-Auftrag bmoeller bmoeller,bmoeller 0.0.0099 "   q_bm_m21 und q_smo" mcbs
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [project] das JiraProjekt, in dem der DB-Issue erzeugt werden soll
 * @param [ordertype] der OrderType als Text (e.g. Aufgabe , Service-Auftrag)
 * @param [assignee] der username / login
 * @param [watcher] eine komma-separierte Liste der anzulegenden Beobachter
 * @param [version] die versionsnummer
 * @param [epic] das Epic zum Release
 * @param [database] eine Zeichenkette mit den DB-Namen, die zu behandeln sind
 * @param [product] das betroffene Produkt (lediglich als Text)
 *
 * @author bmoeller
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
    val database : String
    val product : String

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
    database = args[7]
    logger.info("DB : ${database}")
    product = args[8]
    logger.info("Produkt : ${product}")

    val j = AtlassianJiraIssue(jiraURL, credentials)

    val description = "Bitte die DB-Changes auf :\\r\\n\\r\\n\\t    ${database} \\r\\n\\r\\n einspielen ... \\r\\n Danke! "
    var issue = j.createIssue("${project}","Service Auftrag", "${version}","${product} :: ${version} :: DB-Changes","${description}"," ")

    logger.info("DBChanges : ${issue}")
    // interne Zuweisung
    j.setAssignee(issue,assignee)
    // Beobachter
    watchers.forEach { watcher:String ->
        j.addWatcher(issue, watcher)
    }

    println("|${issue}|")
    val properties = Properties()

    properties.put("issue", "${issue}")
    var propertiesFile = "issue.properties"
    var fileOutputStream = FileOutputStream(propertiesFile)
    properties.store(fileOutputStream, "save to properties file")

}