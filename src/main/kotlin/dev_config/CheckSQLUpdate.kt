package dev_config

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.FileWriter
import java.util.*

/**
 * Methode zur Abfrage , ob ein Issue über SQL-Updates verfügt
 * - die Komponenten des Issue werden ausgelesen
 * - Enthält die Komponentenliste "SQL Update Script" wird "TRUE" im SQL-Propertyfile gesetzt, ansonsten false
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.7.jar:. dev_config.CheckSQLUpdateKt https://jira.freenet-group.de/rest/api/2/ mcbstest:qs_mcbs_11 MCBS-2774
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issue] das zu prüfende Ticket
 * @param [exceptionFile] Property-File mit nicht zu beruecksichtigenden Prefixes (UTF , M;SBS etc.) (erwartet in jiraResources)
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    val jiraURL : String
    val bambooURL: String
    val credentials : String
    val plankey: String
    val buildnumber : String
    val exceptionFile : String

    var properties = Properties()
    var sql: Boolean = false
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    bambooURL = args[0]
    logger.info("BambooURL : ${bambooURL}")
    jiraURL = args[1]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[2]
    logger.info("Credentials : $credentials")
    plankey = args[3]
    logger.info("PlanKey : ${plankey}")
    buildnumber = args[4]
    logger.info("Build : ${buildnumber}")
    exceptionFile = args[5]
    logger.info("ExceptionFile : ${exceptionFile}")
    // Connections to Atlassian
    val j = AtlassianJiraIssue(jiraURL, credentials)
    val b = AtlassianBambooInfo(bambooURL,credentials)
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
                if (complist.contains("SQL Update Script")) {
                    //logger.info("${e} : Config ...")
                    sql = true
                    properties.put("sql", "true")
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



