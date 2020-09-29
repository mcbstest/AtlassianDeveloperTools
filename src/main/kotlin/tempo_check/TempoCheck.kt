package tempo_check

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Method to evaluate Jira-Issues for tempo-requirement-ids
 * - Get Issues for Build
 * - Get IssueType
 * - If IssueType equals requirement :
 * - Check requirement-id
 * - Get Worker
 * - Send a Mail-List
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. tempo_check.TempoCheckKt https://bambooweb.mobilcom.de/rest/api/latest/result/ mcbstest:password MCBS-MR74 12 https://jira.freenet-group.de/rest/api/2/
 *
 * @param [jiraURL] der zuzuweisende username / login
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 *
 *  @author bmoeller
 *
 * @see AtlassianJiraIssue
 * @see AtlassianBambooInfo
 *
 * @since 1.0
 */

fun main(args : Array<String>) {
    // properties
    val credentials: String
    val jiraURL: String
    val timeSpan : String
    val issueTypes : String
    val projects : String

    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())

    // parameter
    logger.debug(args.toString())

    credentials = args[1]
    logger.info("Credentials : $credentials")
    jiraURL = args[0]
    logger.info("JiraURL : $jiraURL")
    timeSpan = args[2]
    logger.info("timeSpan : ${timeSpan}")
    issueTypes = args[3]
    logger.info("Issues : ${issueTypes}")
    projects = args[4]
    logger.info("Projekte : ${projects}")

    // bamboo-connect
    val j = AtlassianJiraIssue(jiraURL, credentials)
    j.queryReqId(timeSpan, issueTypes, projects )

}
