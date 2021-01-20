package jira_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.util.*

/**
 * Methode zum Setzen der FixVersion an allen JiraIssues aus einer Datei
 * - die JiraIssues werden aus der Datei eingelesen
 * - die relevanten Komponenten des Build-Produkts werden eingelesen (jiraResources)
 * - die Komponenten der Build-Issues werden mit den Produkt-Komponenten verglichen
 * - bei Uebereinstimmung wird die FixVersion gesetzt
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_issue.SetFixVersionsKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password ms-customer_7.3.3 MCBS-MR branch_issues.txt
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version] die zu setzende Versionsnummer
 * @param [componentFile] Property-File mit relevanten Komponenten (erwartet in jiraResources)
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
    val version : String = args[2]
    logger.info("Version : $version")
    val componentFile : String = args[3]
    logger.info("Components : $componentFile")
    val issues : String = args[4]
    logger.info("Issues : $issues")

    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Properties / Komponentenliste des Projekts auslesen
    val p : Properties = j.readProperties(componentFile)
    val projectComponents = p.getProperty("components")
    val compArray = projectComponents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var complist : ArrayList<String>
    // Iteration über die Liste
    val issueList: List<String> = issues.split(" ").map { it.trim() }
    issueList.forEach {
        println("$it :: SetFixVersion !")
        complist = j.getComponentsForIssue(it)
        logger.debug("CompList : $complist")
        for (comp in complist) {
            logger.debug("Comp : $comp")
            for (i in compArray.indices) {
                logger.debug("compArray[i] : $i : ${compArray[i]}")
                if (compArray[i] == comp) {
                    logger.info("Anpassen : $it : ${compArray[i]} ")
                    j.addFixVersion(it, version)
                }
            }
        }
    }
}