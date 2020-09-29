package jira_config

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger

/**
 * Method to create a version and a componentversion in Jira
 * - the version is created
 * - based on a componentlist and the created versionid the componentversions are created
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_config.CreateVersionKt https://bambooweb.mobilcom.de/rest/api/latest/result/  https://jira.freenet-group.de/rest/com.deniz.jira.mapping/latest/ mcbstest:password 0.0.0099 MCBS "Actions,Billing"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [jiraURL2] die URL fuer produktiven JIRA-Zugriff für componentversions (Jira-Plugin)
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version] die zu erzeugende Version
 * @param [project] der name des Projekts in Jira
 * @param [components] eine komma-separierte Lister von Komponenten, für die die componentversion zu erzeugen ist
  *
 * @author bmoeller
 *
 * @version 1.0
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameters
    logger.debug(args.toString())
    val jiraURL : String = args[0]
    logger.info("JiraURL : $jiraURL")
    val jiraURL2 : String = args[1]
    logger.info("JiraURL(2) : $jiraURL2")
    val credentials : String = args[2]
    logger.info("Credentials : $credentials")
    val version : String = args[3]
    logger.info("Version : $version")
    val project : String = args[4]
    logger.info("Project : $project")
    val components : String = args[5]
    logger.info("Components : $components")
    // split the components
    val compArray = components.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    // connect to Jira (Standard)
    val j = AtlassianJiraIssue(jiraURL, credentials)
    // get components
    val componentsMap = j.getProjectComponentsToMap(project)
    // create versionid
    val versionid = j.createVersion(project,version)
    if (versionid != "0") {
        // connect to alternative Jira (ComponentVersion-Plugin)
        val k = AtlassianJiraIssue(jiraURL2, credentials)
        // over all components
        for (comp in compArray) {
            // create componentversion
            val code = componentsMap[comp]
            k.createComponentVersion(versionid, code!!)
        }
    }

}

