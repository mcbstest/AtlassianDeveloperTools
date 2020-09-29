package link_epos

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.io.InputStream
import java.util.ArrayList


/**
 * Methode zur Verknüpfung eines Issues mit dem zugehörigen Epos
 * basierend auf einer Liste der zu einem Build gehörenden Issues
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. link_epos.LinkIssue2EposKt https://bambooweb.mobilcom.de/rest/api/latest/result/ https://jira.freenet-group.de/rest/api/2/ mcbstest:password ABRMS-MR 137 META-152
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */
fun main(args : Array<String>) {
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    //
    val bambooURL : String
    val jiraURL : String
    val credentials : String
    val planKey : String
    val buildNumber : String
    val epos : String
    // Parameter
    bambooURL = args[0]
    logger.info("BambooURL : ${bambooURL}")
    jiraURL = args[1]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[2]
    logger.info("Credentials : ${credentials}")
    planKey = args[3]
    logger.info("Plankey : ${planKey}")
    buildNumber = args[4]
    logger.info("BuildNumber : ${buildNumber}")
    epos = args[5]
    logger.info("Epos : ${epos}")
    // call jira
    val j = AtlassianJiraIssue(jiraURL, credentials)
    // call bamboo, get issues
    val b = AtlassianBambooInfo(bambooURL,credentials)
    val issuelist = b.getIssuesForBuild(planKey,buildNumber)
    logger.debug("Liste : ${issuelist.toString()}")
   for (issue in issuelist) {
       j.linkEpic(issue,epos)
   }

}


