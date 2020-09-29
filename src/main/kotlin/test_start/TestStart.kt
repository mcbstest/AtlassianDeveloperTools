package test_start

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger

/**
 * Method to set "version for TEST" in epic , change state of epic and add "TEST-version-comment" in the relevant test-orders
 * - the version for test is set (customfield_41591)
 * - the state of the issue is changed by a transition-code (31 >> start_test)
 * - the dependent test-orders are read
 * - a comment "Version for TEST" is added
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. test_start.TestStartKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password 0.0.0099 META-152
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
    // set version for TEST
    j.setTestVersion(epic,version)
    // change issue-state (if necessary)
    j.setIssueState(epic,"31")
    // Get test-orders for epic
    val testorderlist = j.getTestOrder(epic)
    testorderlist.forEach {
        logger.debug("${it} : ${version}")
        // and set "version under test" in test-order
        j.addComment(it,"!!  Freigegebene Version fuer TEST : MCBS ${version}" )
    }
}