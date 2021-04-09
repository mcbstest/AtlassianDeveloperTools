package jira_board

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
/**
 * Method to modify the state of a list of issues from "finished" to "test"
 * - two transitions :
 *   741 : test
 *
 * Issues , die sich zum Zeitpunkt der Bearbeitung nicht in "Entwicklung abgeschlossen" befinden, werden nicht modifiziert.
 * Sofern mehr als 1 betroffene Komponente eingetragen ist, wird der Status nicht verändert.
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_board.SetMS_IssuesDoneKt https://bambooweb.mobilcom.de/rest/api/latest/result/ https://jira.freenet-group.de/rest/api/2/ mcbstest:qs_mcbs_11 ABRMS-MAIR 59
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [plankey] der Epic-Name fuer dieses release
 * @param [buildnumber] die Build-Nummer (bamboo)
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
    val bambooURL : String = args[0]
    logger.info("BambooURL : $bambooURL")
    val jiraURL : String = args[1]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[2]
    logger.info("Credentials : $credentials")
    val planKey : String = args[3]
    logger.info("PlanKey : $planKey")
    val buildNumber : String = args[4]
    logger.info("BuildNumber : $buildNumber")

    // JIRA-Connections
    val j = AtlassianJiraIssue(jiraURL, credentials)
    val b = AtlassianBambooInfo(bambooURL,credentials)
    // get issues for the given build
    val issueList = b.getIssuesForBuild(planKey,buildNumber)

    // Kontrollausgabe
    for(x in issueList){
        logger.info("Issue : $x")
        val cl = j.getComponentsForIssue(x)

        if ((cl.size == 1) || ((cl.size == 2) && (cl.contains("ms-configuration"))) || ((cl.size == 2) && (cl.contains("SQL Update Script")))  ) {
            val state = j.getIssueState(x)
            if (state == "Entwicklung abgeschlossen") {
                j.setIssueState(x, "741")
                j.addComment(x, "GIT-Deployment / UnderTest! by mcbstest@bamboo")
            } else {
                logger.info("Issue : $x : Ausgangsstatus <> Entwicklung abgeschlossen !!")
            }
        } else {
            logger.info("More than 1 component ...")
        }
    }

}