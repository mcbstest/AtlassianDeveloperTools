package jira_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.util.*

/**
 * Methode zum Setzen des Comments an allen JiraIssues aus einer Datei
 * - die JiraIssues werden aus der Datei eingelesen
 * - der Comment am Issue wird gesetzt
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_issue.SetFixVersionsKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password ms-customer_7.3.3 MCBS-MR branch_issues.txt
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [comment] der zu setzende Kommentar
 * @param [issues] Zeichenkette (sep.) mit zu berücksichtigenden Issues
  *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameter
    logger.debug(args.toString())
    val jiraURL : String = args[0]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[1]
    logger.info("Credentials : $credentials")
    val comment : String = args[2]
    logger.info("Comment : $comment")
    var issues : String = args[3]
    logger.info("Issues : $issues")
    issues=issues.trim()

    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Iteration über die Liste
    val issueList: List<String> = issues.split(" ").map { it.trim() }
    issueList.forEach {
        println("$it :: SetComment ...")
        j.addComment(it, comment)
    }
}