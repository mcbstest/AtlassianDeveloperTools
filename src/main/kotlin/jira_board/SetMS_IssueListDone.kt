package jira_board

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
/**
 * Method to modify the state of a list of issues from "finished" via "test" to "done"
 * - two transitions :
 *   741 : test
 *   821 : done
 *
 * Issues , die sich zum Zeitpunkt der Bearbeitung nicht in "Entwicklung abgeschlossen" befinden, werden nicht modifiziert.
 * Sofern mehr als 1 betroffene Komponente eingetragen ist, wird der Status nicht verändert.
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_board.SetMS_IssueListDoneKt https://bambooweb.mobilcom.de/rest/api/latest/result/ https://jira.freenet-group.de/rest/api/2/ mcbstest:qs_mcbs_11 "ABRMS-1234 ABRMS-4567"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issues] Liste der betroffenen Issues (blank-sep.)
 *
 * @author bmoeller
 *
 * @version 1.0
 * @see AtlassianJiraIssue
 */
fun main(args : Array<String>) {
    // Variablen
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // Parameters
    val jiraURL : String = args[0]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[1]
    logger.info("Credentials : $credentials")
    var issues : String = args[2]
    logger.info("Issues : $issues")
    issues=issues.trim()

    // JIRA-Connections
    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Iteration über die Liste
    val issueList: List<String> = issues.split(" ").map { it.trim() }
    issueList.forEach {
        logger.info("Issue : $it")
        val cl = j.getComponentsForIssue(it)

        if ((cl.size == 1) || ((cl.size == 2) && (cl.contains("ms-configuration"))) || ((cl.size == 2) && (cl.contains("SQL Update Script")))  ) {
            val state = j.getIssueState(it)
            if (state == "Entwicklung abgeschlossen") {
                j.setIssueState(it, "741")
                j.setIssueState(it, "821")
                if (j.getIssueState(it) == "Erledigt") {
                    j.addComment(it, "Done! by mcbstest@github")
                }
            } else if (state == "Testphase") {
                j.setIssueState(it, "821")
                if (j.getIssueState(it) == "Erledigt") {
                    j.addComment(it, "Done! by mcbstest@bamboo")
                }
            } else {
                logger.info("Issue : $it : Ausgangsstatus <> Entwicklung abgeschlossen !!")
            }
        } else {
            logger.info("More than 1 component ...")
        }
    }

}