package dev_config

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
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
    val issueFile : String

    var properties = Properties()
    var config: Boolean = false
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    jiraURL = args[0]
    logger.info("JiraURL : $jiraURL")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    issueFile = args[5]
    logger.info("IssueFile : $issueFile")
    // Connections to Atlassian
    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Exception-Issues
    // Properties / Jira-Exceptions (Prefixes) auslesen
    val exceptions : Properties = j.readProperties(exceptionFile)
    val jiraExceptions = exceptions.getProperty("exception")
    val exceptionArray = jiraExceptions.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    for(x in exceptionArray){
        println("Exc : ${x}")
    }
    // Am Build beteiligte Jira-Issues aus Bamboo auslesen
    val issuelist = b.getIssuesForBuild(plankey,buildnumber)
    var complist : ArrayList<String>
    for (e in issuelist){

        val pref = e.split("-")
        if ( !exceptionArray.contains(pref[0])) {

            complist = j.getComponentsForIssue("${e}")
            logger.debug("CompList : " + complist.toString())
            for (comp in complist) {

                // Auswertung
                if (complist.contains("ms-configuration")) {
                    //logger.info("${e} : Config ...")
                    config = true
                    properties.put("config", "true")
                } else {
                    //logger.info("${e} : Keine Config ...")
                    //properties.put("config", "false")
                }
            }
        } else {
            println("${e} :: Keine Bearbeitung !")
        }

    }
    // Schreiben
    logger.info("Config : ${config}")
    // Property-File
    var propertiesFile = "config.properties"
    val fileWriter = FileWriter(propertiesFile)
    if (config == true) {
        properties.put("config", "true")
    } else {
        properties.put("config", "false")
    }
    properties.store(fileWriter, "Config ...")
}



