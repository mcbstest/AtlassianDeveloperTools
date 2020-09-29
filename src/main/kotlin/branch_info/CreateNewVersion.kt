package branch_info

import atlassian_jira.AtlassianJiraIssue
import general_info.GeneralInfo
import org.apache.log4j.Logger
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Methode zur Erzeugung einer neuen Version basierend auf der semanticVersion der Issues aus der übergebenen Datei
 * - die Issue werden ausgelesen und in eine Liste übertragen
 * - Zu jedem Issue werden die Komponenten ausgelesen
 * - sofern die Komponente zur übergebenen Komponente passt, wird die semanticVersion ausgelesen und in eine Liste übertragen
 * - zum Schluss wird die semanticVersion-List ausgewertet aun auf Basis der übergebenen Version und der höchsten semanticVersion eine neue Version per Datei zurückgegeben
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.4.jar:. branchInfo.CreateNewVersionKt 1.0.0 ms-oidc issuelist.txt https://jira.freenet-group.de/rest/api/2/ mcbstest:password
 *
 * @param [version] die anzupassende Versionskennung
 * @param [component] die Komponente, deren Version zu ermitteln ist
 * @param [issueFile] die Datei mit den Issues
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 * @see GeneralInfo
 */

fun main(args : Array<String>) {
    // properties
    val version : String
    val component : String
    val issueFile: String
    val jiraURL: String
    val credentials: String
    // the issues, read from file
    val issueList = ArrayList<String>()
    val semVerList = ArrayList<String>()

    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())

    // parameter
    logger.debug(args.toString())
    version = args[0]
    logger.info("Version : ${version}")
    component = args[1]
    logger.info("Component : ${component}")
    issueFile = args[2]
    logger.info("File : ${issueFile}")
    jiraURL = args[3]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[4]
    logger.info("Credentials : $credentials")

    val j = AtlassianJiraIssue(jiraURL, credentials)

    // read issues from file and add to ArrayList
    File(issueFile).forEachLine {
        logger.debug("Zeile : $it")
        val issue = it
        issueList.add(issue)
    }

    logger.info("Issue-List : $issueList")

    logger.info("Semantic-Versions for : $component")

    // Iteration über alle Issues , die auf dem Branch gefunden wurden
    issueList.forEach { e ->
        logger.debug("Issue : $e")
        // Komponenten des Issues aus Jira auslesen
        val componentList = j.getComponentsForIssue(e)
        componentList.forEach { c ->
            // passt die Komponente zu diesem Build ?
            if (c.toUpperCase() == component.toUpperCase()){
                // dann die semantic version auslesen
                val s = j.getSemanticVersionForIssue(e)
                logger.debug(s)

                logger.debug("$e : $s")
                semVerList.add(s)

            }
        }
    }
    logger.info("Semantic version s : $semVerList")
    val s : String
    s = when {
        semVerList.contains("Major") -> "Major"
        semVerList.contains("Minor") -> "Minor"
        semVerList.contains("Patch") -> "Patch"
        else -> "Patch"
    }
    logger.info(s)
    // Neue Version erzeugen
    val g = GeneralInfo()
    val v = g.createNewVersion(version , s )
    logger.info("=========================")
    logger.info("Semantic Version : $s")
    logger.info("New Version : $v")
    // write new version to property

    val outfile = File("newVersion.txt")
    outfile.writeText("")
    outfile.writeText(v)

}