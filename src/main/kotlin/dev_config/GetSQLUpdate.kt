package dev_config

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.io.FileWriter
import java.util.*

/**
 * Methode zur Abfrage , ob ein Issue Config-Abhängigkeiten unterliegt
 * - die Komponenten des Issue werden ausgelesen
 * - Enthält die Komponentenliste "ms-configuration" wird "TRUE" im Config-Propertyfile gesetzt, ansonsten false
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. dev_config.CheckConfigKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password MCBS-2774
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issueFile] Property-File mit nicht zu beruecksichtigenden Prefixes (UTF , M;SBS etc.) (erwartet in jiraResources)
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    val jiraURL : String
    val credentials : String
    var issues : String

    var properties = Properties()
    var sql: Boolean = false
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    jiraURL = args[0]
    logger.info("JiraURL : $jiraURL")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    issues = args[2]
    logger.info("Issues : $issues")
    issues=issues.trim()
    // Connections to Atlassian
    val j = AtlassianJiraIssue(jiraURL, credentials)

    var complist : ArrayList<String>
    // Iteration über die Liste
    val issueList: List<String> = issues.split(" ").map { it.trim() }

    issueList.forEach {
        complist = j.getComponentsForIssue("${it}")
        logger.debug("CompList : " + complist.toString())
        for (comp in complist) {

            // Auswertung
            if (complist.contains("SQL Update Script")) {
                //logger.info("${e} : Config ...")
                sql = true
                properties.put("sql", "true")
            } else {
                logger.debug("$it : Kein SQL ...")
            }
        }
    }

    // Schreiben
    logger.info("SQL : ${sql}")
    // Property-File
    var propertiesFile = "sql.properties"
    val fileWriter = FileWriter(propertiesFile)
    if (sql == true) {
        properties.put("sql", "true")
    } else {
        properties.put("sql", "false")
    }
    properties.store(fileWriter, "SQL ...")
}



