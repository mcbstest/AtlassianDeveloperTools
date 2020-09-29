package git_start

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger

/**
 * Methode zum Setzen der "version for GIT" im Epic und "GIT-version-comment" in den zugeordneten TestOrders <br>
 * - die  "version for GIT" wird gesetzt (customfield_41592)
 * - die zugeordneten TestOrders werden ausgelesen
 * - ein Kommentar "Freigegebene Version für GIT" wird angefügt
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. git_start.GitStartKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password 0.0.0099 META-152
 *
 * @author bmoeller
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version] die zu hinterlegende Version
 * @param [epic] das Epic für das Release
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
    // set version for GIT
    j.setGitVersion(epic,version)

}