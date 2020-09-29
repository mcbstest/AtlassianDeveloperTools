package sql_info

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.FileWriter
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.*
import kotlin.collections.ArrayList

/**
 * Method to get SQL-Infos for a given Build
 * - if the components are in "SQL Update Script , SQL-Skript (MCBS-DB)"
 * - the boolean is set to true
 * - the issue is added to a list
 * - the results are written to a properties-file
 *
 * call :
 * java -Dlog4j.configuration=./log4j_debug.properties  -cp AtlassianDeveloperTools-all-1.8.0.jar:.:./AtlassianDeveloperTools  sql_info.GetSQLInfosKt "https://jira.freenet-group.de/rest/api/2/" "https://bambooweb.mobilcom.de/rest/api/latest/result/" "mcbstest:qs_mcbs_11" "ABRMS-MCPMR42" "3" "xyz.properties"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff für die linked issues
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [planKey]
 * @param [buildNumber]
 * @param [componentFile]
 *
 * @author bmoeller
 *
 * @version 1.0
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {

    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameters
    logger.debug(args.toString())
    val jiraURL = args[0]
    logger.info("JiraURL : $jiraURL")
    val bambooURL = args[1]
    logger.info("BambooURL : $bambooURL")
    val credentials = args[2]
    logger.info("Credentials : $credentials")
    val planKey = args[3]
    logger.info("Plan : $planKey")
    val buildNumber = args[4]
    logger.info("Build : $buildNumber")
    // Component-File
    val componentFile : String = args[5]
    logger.info("Components : $componentFile")
    // connect to Jira (Standard)
    val j = AtlassianJiraIssue(jiraURL, credentials)
    val b = AtlassianBambooInfo(bambooURL, credentials)
    // Properties / Komponentenliste des Projekts auslesen
    val cp : Properties = j.readProperties(componentFile)
    val projectComponents = cp.getProperty("components")
    val compArray = projectComponents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    // referenced issues
    var complist: ArrayList<String>
    // list with sql-Issues list
    val sqlIssueList: ArrayList<String> = arrayListOf()
    // flag for "hasSQL"
    var hasSQL = FALSE
    // properties
    val properties = Properties()

    // get Issues for Build
    val issueList = b.getIssuesForBuild(planKey, buildNumber)
    // iterate over all implemented issues
    issueList.forEach {
        logger.info("Issue : $it")
        //get the components for every single issue
        complist = j.getComponentsForIssue(it)

        // neu
        // über alle Komponenten des Issue
        for (comp in complist) {
            logger.info("Komponente dazu : $comp")
            // in den Vergleich mit den Komponenten der Datei
            // sind eine Komponente des Issue und der Datei gleich
            for (i in compArray.indices) {
                logger.debug("compArray[i] : $i : ${compArray[i]}")
                // sind eine Komponente des Issue und der Datei gleich
                if (compArray[i] == comp) {
                    // bisher
                    if ((complist.contains("SQL Update Script")) || (complist.contains("SQL-Skript (MCBS-DB)"))) {
                        logger.info("${compArray[i].toString()} == $comp  hasSQL")
                        hasSQL = TRUE
                        sqlIssueList.add(it)
                    } else {
                        // ende bisher
                        logger.info("${compArray[i].toString()} == $comp  hasNoSQL")
                    }
                } else {
                    logger.info("${compArray[i].toString()} <> $comp")
                }
            }
        }
    }
    logger.info("SQL : $hasSQL Liste : $sqlIssueList")

    // Property-File for boolean "dependency"
    val propertiesFile = "sql.properties"
    val fileWriter = FileWriter(propertiesFile)
    if (hasSQL) {
        properties["hasSQL"] = hasSQL.toString()
        properties["sqlIssues"] = sqlIssueList.toString()
    } else {
        properties["hasSQL"] = hasSQL.toString()

    }
    properties.store(fileWriter, "SQL ...")

}