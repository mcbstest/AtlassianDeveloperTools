package meta_issue

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.FileWriter
import java.util.*

/**
 * Method to get SQL-Infos for a given Build
 * - if the components are in "SQL Update Script , SQL-Skript (MCBS-DB)"
 * - the boolean is set to true
 * - the issue is added to a list
 * - the results are written to a properties-file
 *
 * call :
 * java -Dlog4j.configuration=./log4j_debug.properties  -cp AtlassianDeveloperTools-all-1.8.0.jar:.:./AtlassianDeveloperTools  meta_issue.GetIBNDateKt "https://jira.freenet-group.de/rest/api/2/" "mcbstest:qs_mcbs_11" "META-206"
 *
 * @property jiraURL die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issue]
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
    val credentials = args[1]
    logger.info("Credentials : $credentials")
    val issue = args[2]
    logger.info("Issue : $issue")
    //
    val properties = Properties()
    // connect to Jira (Standard)
    val j = AtlassianJiraIssue(jiraURL, credentials)

    //get the IBN-Date
    val ibndate = j.getIBNDate(issue)

    // Property-File for IBN-Date
    val propertiesFile = "ibn.properties"
    val fileWriter = FileWriter(propertiesFile)
    if (ibndate == "undefined") {
        properties["IBN-Date"] = "undefined"
    } else {
        properties["IBN-Date"] = ibndate
    }
    //
    properties.store(fileWriter, "IBN ...")
}