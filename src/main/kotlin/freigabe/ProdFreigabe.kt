package freigabe

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger

/**
 * Method to set "version for PROD" in epic , change state of epic ("geliefert") and add "PROD-version-comment" in the relevant test-orders
 * - the version for prod is set (customfield_41593)
 * - the state of the issue is changed by a transition-code (41 >> geliefert)
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. freigabe.ProdFreigabeKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password 0.0.0099 META-152
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version] die Versionsnummer
 * @param [epic] das Epic zum Release
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */


fun main(args : Array<String>) {
    // properties
    val jiraURL : String
    val credentials : String
    var version : String
    val epic : String
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameters
    logger.debug(args.toString())
    jiraURL = args[0]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    version = args[2]
    logger.info("Version : ${version}")
    epic = args[3]
    logger.info("EPIC : ${epic}")
    // JIRA-Connect
    val j = AtlassianJiraIssue(jiraURL, credentials)
    // set version for PROD
    j.setProdVersion(epic,version)
    // change issue-state (if necessary)
    j.setIssueState(epic,"41")
}
