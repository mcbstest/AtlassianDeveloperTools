package testrail

import gurock_testrail.TestRail
import org.apache.log4j.Logger


/**
 * Methode zum Setzen eines Milestones in TestRail <br>

 * - Id des referenzierten Tests auslesen
 * - Id des Milestones auslesen
 * - Milestone an der testrunId aktualisieren
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.9.4.jar:. testrail.TestRailMilestone https://testrail-test.mobilcom.de bmoeller:blablabla Spielwiese mcbs ms-customer 4.1.1 1 83.4 https://bambooweb.mobilcom.de/browse/ABRMS-MSCMR40-2  http://artifactory.mobilcom.de/artifactory/webapp/#/artifacts/browse/tree/General/md-release/de/md/mcbs/customer/mcbs-customer/4.1.1 https://kiwi.freenet-group.de/display/AERP/ms-customer+4.1.1
 *
 * @author bmoeller
 *
 * @param [testrailURL] die URL fuer produktiven TestRail-Zugriff
 * @param [credentials] login:password fuer produktiven TestRail-Zugriff
 * @param [project] das zu bedienende Projekt
 * @param [testrun] der betroffene Testrun

 */

fun main(args : Array<String>) {
    val testrailuser: String
    val testrailpassword : String
    // logging
    val logger = Logger.getLogger(TestRail::class.java.name)
    // parameters
    logger.debug(args.toString())
    val testrailURL: String = args[0]
    logger.info("JiraURL : $testrailURL")
    val credentials: String = args[1]
    logger.info("Credentials : $credentials")
    val project: String = args[2]
    logger.info("Projekt : $project")
    val testrun = args[3]
    logger.info("Testrun : $testrun")

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
    // Testruns auslesen
    val testrunId = t.getTestrunByName(projectId,testrun)
    // Update
    t.closeTestRun(testrunId)

}