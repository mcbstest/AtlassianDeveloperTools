package meta_issue

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Methode um JIRA-META-Issues zu erzeugen und zu beobachten
 * - Erzeugung des Issue im Projekt "META-MCBS"
 * - Beobachter werden gesetzt
 * Aufruf :
 * java -cp ./devtools/apps/atltools/AtlassianDeveloperTools-all- ....
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [filename] csv-File mit Sprint-Informationen
 * @param [watchers] eine komma-separierte Liste der anzulegenden Beobachter
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {

    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    logger.debug(args.toString())
    val jiraURL : String = args[0]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[1]
    logger.info("Credentials : $credentials")
    val filename : String = args[2]
    logger.info("File : $filename")
    val watchlist : String = args[3]
    val watchers  = watchlist.split(",")
    logger.info("Watchers : $watchers")

    val j = AtlassianJiraIssue(jiraURL, credentials)

    var feld : String
    // input-file
    val inputStream: FileInputStream = File("./jiraResources/${filename}").inputStream()
    // Zeilen lesen und ...
    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            // Ausgabe der Zeile
            logger.info(line)
            // Zeile splitten
            val sprintDataList = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            // Teile isolieren
            val sprintnumber = sprintDataList[0]
            val name: String = sprintDataList[1]
            // Entwicklung
            feld = sprintDataList[2]
            val dev = feld.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val dev_start = dev[0]
            var date: Date = SimpleDateFormat("dd.MM.yyyy").parse(dev_start)
            val devDate = SimpleDateFormat("yyyy-MM-dd").format(date)
            logger.info("Dev : $devDate")
            // GIT
            feld = sprintDataList[3]
            val git = feld.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val git_start = git[0]
            date = SimpleDateFormat("dd.MM.yyyy").parse(git_start)
            val gitDate = SimpleDateFormat("yyyy-MM-dd").format(date)
            logger.info("GIT : $gitDate")
            // IBN
            val ibn: String = sprintDataList[4]
            date = SimpleDateFormat("dd.MM.yyyy").parse(ibn)
            val ibnDate = SimpleDateFormat("yyyy-MM-dd").format(date)
            logger.info("IBN : $ibnDate")
            // Release-Nummer
            val releasenumber: String = sprintDataList[5]

            val issue = j.createMetaIssue(releasenumber, devDate, gitDate, ibnDate, name)
            logger.info("$issue")
            // interne Zuweisung
            j.setAssignee(issue, "mcbstest")
            // Beobachter
            watchers.forEach { watcher: String ->
                j.addWatcher(issue, watcher)
            }
            logger.info("#########################")
            logger.info("# Epic : $issue #")
            logger.info("#########################")
        }
    }
}