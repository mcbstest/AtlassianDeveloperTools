package testrail

import gurock_testrail.TestRail
import org.apache.log4j.Logger
import java.io.File
import java.io.FileInputStream

/**
 * Methode zum Erzeugen von Testrail-Milestones
 * - Auslesen des ProjektId
 * - Setzen des Milestone
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. testrail.TestRailResult https://testrail-test.mobilcom.de bmoeller:blablabla Spielwiese mcbs ms-customer 4.1.1 1 83.4 https://bambooweb.mobilcom.de/browse/ABRMS-MSCMR40-2  http://artifactory.mobilcom.de/artifactory/webapp/#/artifacts/browse/tree/General/md-release/de/md/mcbs/customer/mcbs-customer/4.1.1 https://kiwi.freenet-group.de/display/AERP/ms-customer+4.1.1
 *
 * @author bmoeller
 *
 * @param [testrailURL] die URL fuer produktiven TestRail-Zugriff
 * @param [credentials] die Zugriffsdaten
 * @param [project]  der Name des Projects
 * @param [filename] der Name der CSV-Datei
 *
 */
fun main (args : Array<String>) {
    // properties
    val testrailuser: String
    val testrailpassword : String
    // logging
    val logger = Logger.getLogger(TestRail::class.java.name)
    // Parameter
    logger.debug(args.toString())
    val testrailURL: String = args[0]
    logger.info("JiraURL : $testrailURL")
    val credentials: String = args[1]
    logger.info("Credentials : $credentials")
    val project: String = args[2]
    logger.info("Projekt : $project")
    val filename: String = args[3]
    logger.info("Filename : $filename")
    // credentials splitten
    val cred = credentials.split(":")
    testrailuser=cred[0]
    testrailpassword=cred[1]
    logger.debug(testrailuser)
    logger.debug(testrailpassword)

    // TestRail-Connect
    val t = TestRail(testrailURL,testrailuser,testrailpassword)
    // ProjektId auslesen
    val projectId = t.getPrjByName(project)

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

            t.addMilestone(projectId,number,name,dev,git,ibn)
        }
    }
    inputStream.close()


}