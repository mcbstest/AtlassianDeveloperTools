package testrail

import gurock_testrail.TestRail
import org.apache.log4j.Logger
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Methode zum Setzen eines TestResult in TestRail <br>
 * - Auslesen des Projekts <br>
 * - Auslesen der Suite <br>
 * - Auslesen der Section <br>
 * - Auslesen des Testcase <br>
 * - Anlegen eines TestRun und Referenz auf den TestCase setzen
 * - Id des referenzierten Tests auslesen
 * - Ergebnis , Version , Kommentar und "SUCCESS" setzen
 *
 *  Struktur : Projekt : xyz , Suite : mcbs-core , Sections : mcbs,ms-cuba,ms-customer etc.
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. testrail.TestRailResult https://testrail-test.mobilcom.de bmoeller:blablabla Spielwiese mcbs ms-customer 4.1.1 1 83.4 https://bambooweb.mobilcom.de/browse/ABRMS-MSCMR40-2  http://artifactory.mobilcom.de/artifactory/webapp/#/artifacts/browse/tree/General/md-release/de/md/mcbs/customer/mcbs-customer/4.1.1 https://kiwi.freenet-group.de/display/AERP/ms-customer+4.1.1
 *
 * @author bmoeller
 *
 * @param [testrailURL] die URL fuer produktiven TestRail-Zugriff
 * @param [credentials] login:password fuer produktiven TestRail-Zugriff
 * @param [project] das zu bedienende Projekt
 * @param [suite] die im Projekt für diese Komponente relevante Suite
 * @param [component] die getestete Komponente (ms-customer) als Basis für weitere Namen wie Testcase, Section, TestRun etc
 * @param [version] die Version der getesteten Komponente
 * @param [result] 1=SUCCESS , 5=FAILED
 * @param [coverage] Numerischer Wert der Testabdeckung
 * @param [bambooresult] vollständige URL auf den Buildplan
 * @param [artifactoryresult] vollständige URL auf die erzeugten Artefakte
 * @param [confluenceresult]
 */

fun main(args : Array<String>) {
    // properties
    val testrailURL: String
    val credentials: String
    val project: String
    val suite: String
    val component: String
    val version: String
    val result : String
    val coverage : String
    val bambooresult : String
    val artifactoryresult : String
    val confluenceresult : String
    val testrailuser: String
    val testrailpassword : String
    // logging
    val logger = Logger.getLogger(TestRail::class.java.getName())
    // parameters
    logger.debug(args.toString())
    testrailURL = args[0]
    logger.info("JiraURL : ${testrailURL}")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    project = args[2]
    logger.info("Projekt : ${project}")
    suite = args[3]
    logger.info("Suite : ${suite}")
    component = args[4]
    logger.info("Komponente : ${component}")
    version = args[5]
    logger.info("Version : ${version}")
    result = args[6]
    logger.info("Result : ${result}")
    coverage = args[7]
    logger.info("Coverage : ${coverage}")
    bambooresult = args[8]
    logger.info("Bamboo-Result : ${bambooresult}")
    artifactoryresult = args[9]
    logger.info("Artifactory-Result : ${artifactoryresult}")
    confluenceresult = args[10]
    logger.info("Confluence : ${confluenceresult}")
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
    // Suite auslesen
    val suiteId = t.getSuiteByName(projectId,suite)
    // Subsection für die Testcases auslesen
    val sectionId = t.getSectionByName(projectId,suiteId,component)
    // TestcaseId aus der Section auslesen
    val testcaseId = t.getTestcaseByName(projectId,suiteId,sectionId,"tc_${component}")
    // Datum auslesen und für den Result-Name formatieren
    //val currentDate = LocalDateTime.now()
    //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    //val formattedDate = currentDate.format(formatter)
    // Testrun definieren , Testcase integrieren und Id auslesen
    val testrunId = t.addTestrun(projectId,suiteId, testcaseId,"${component}_${version}")
    // TestId zum TestRun auslesen
    val testId = t.getTests(testrunId, "tc_${component}")
    // Testergebnis setzen (1 : passed , 5 : failed)
    // Ergebniskommentar zusammensetzen
    var comment = "Automatisch erzeugtes Ergebnis : \nRelease-Page : '${confluenceresult}'\nBuild-Result : ${bambooresult} \n Artifactory : ${artifactoryresult}\nCoverage : ${coverage} % \n..."

    println(t.addTestresult(testId, "${result}", "${version}","${comment}"))


}