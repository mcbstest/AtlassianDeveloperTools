package jira_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.util.*

/**
 * Methode zum Auslesen der relevanten Infos der Issues aus einer File-Liste
 * - die Liste wird ausgelesen
 * - die relevanten Infos werden in JIRA gesammelt
 * - die Infos werden in eine neue Datei geschrieben
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_issue.GetIssueInfosKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password branch_issues.txt issueInfo.csv
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issueFile] File mit auszuwertenden Issues
 * @param [infoFile] File mit allen Infos
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
    val issuefile : String = args[2]
    logger.info("Issue-File : $issuefile")
    val infofile : String = args[3]
    logger.info("Info-File : $infofile")

    val j = AtlassianJiraIssue(jiraURL, credentials)

    val file = File("infofile")
    file.writeText("issue;summary;status,components;semanticVersion;keywords;priority;fixVersions;affectedVersions;dependency;\n")
    var i = ArrayList<String>()
    val issueList : List<String> = File(issuefile).readLines()
    for (issue in issueList) {
        logger.info(issue)
        i = j.getIssueInfos(issue)
        logger.info(i)
        var infoString = ""
        for (infoItem in i) {
            logger.debug("InfoItem  : $infoItem")
            infoString = infoString + infoItem + ";"
        }
        logger.info(infoString)
        file.appendText("$infoString\n")
    }

}