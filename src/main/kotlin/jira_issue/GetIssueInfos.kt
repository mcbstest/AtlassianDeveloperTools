package jira_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.util.*

/**
 * Methode zum Auslesen der relevanten Infos der Issues aus einer Datei (z.B. nach einerm Branch-Scan) <br>
 * Zurückgegeben wird eine Datei mit folgendem Inhalt (quasi Releasenotes) : <br>
 * issue;summary;status,components;semanticVersion;keywords;priority;fixVersions;affectedVersions;dependency <br>
 *
 * - die Liste/Datei wird ausgelesen
 * - die relevanten Infos werden in JIRA gesammelt
 * - die Infos werden in eine neue Datei geschrieben
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_issue.GetIssueInfosKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password branch_issues.txt issueInfo.csv
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issueList] Zeichenkette mit auszuwertenden Issues (Delimiter " ")
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
    val issues : String = args[2]
    logger.info("Issues : $issues")
    val infofile : String = args[3]
    logger.info("Info-File : $infofile")

    val j = AtlassianJiraIssue(jiraURL, credentials)

    val file = File(infofile)
    var i: ArrayList<String>
    //Keine Datei , besser String !  val issueList : List<String> = File(issuefile).readLines()
    val issueList: List<String> = issues.split(" ").map { it.trim() }
    for (issue in issueList) {
        logger.debug(issue)
        i = j.getIssueInfos(issue)
        logger.debug(i)
        if ( ! i.contains("No Issue")) {
            var infoString = ""
            for (infoItem in i) {
                logger.debug("InfoItem  : $infoItem")
                infoString = "$infoString$infoItem;"
            }
            infoString = infoString.replace("[", "", false)
            infoString = infoString.replace("]", "", false)
            logger.debug(infoString)
            file.appendText("$infoString\n")
        }
    }
}