package agile

import atlassian_jira.AtlassianJiraIssue
import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64
import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileInputStream

/**
 * Methode zum Erzeugen von Jira-Sprints
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. testrail.TestRailResult https://testrail-test.mobilcom.de bmoeller:blablabla Spielwiese mcbs ms-customer 4.1.1 1 83.4 https://bambooweb.mobilcom.de/browse/ABRMS-MSCMR40-2  http://artifactory.mobilcom.de/artifactory/webapp/#/artifacts/browse/tree/General/md-release/de/md/mcbs/customer/mcbs-customer/4.1.1 https://kiwi.freenet-group.de/display/AERP/ms-customer+4.1.1
 *
 * @author bmoeller
 *
 * @param [jiraURL] die URL fuer produktiven TestRail-Zugriff
 * @param [jiraCredentials] die Zugriffsdaten
 * @param [filename]  der Name der CSV-Datei
 * @param [board] die ID des Scrum-Boards  (454)
 *
 */

fun main (args : Array<String>) {
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // Parameter
    logger.debug(args.toString())
    val jiraURL: String = args[0]
    logger.info("JiraURL : $jiraURL")
    val jiraCredentials: String = args[1]
    logger.info("Credentials : $jiraCredentials")
    val filename: String = args[2]
    logger.info("Filename : $filename")
    val board: String = args[3]
    logger.info("Board : $board")

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
            val number = sprintDataList[0]
            val name : String = sprintDataList[1]
            val dev : String = sprintDataList[2]
            val git : String = sprintDataList[3]
            val ibn : String = sprintDataList[4]

            val j = AtlassianJiraIssue(jiraURL, jiraCredentials)
            j.addSprint("MCBS-Sprint $number (${name})","Dev: $dev / GIT: $git / IBN: $ibn",board)
        }
    }
    inputStream.close()

}