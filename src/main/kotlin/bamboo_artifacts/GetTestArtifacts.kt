package bamboo_artifacts

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger

/**
 * Methode zum Auslesen der Test-Artefakte eines Bamboo-Builds
 * - die Artefakte des Build werden ausgelesen
 * - die ausgelesenen Artefakte werden gefiltert
 * - die relevanten Artefakte werden in einem property-file abgespeichert
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. bamboo_artifacts.GetTestArtifactsKt https://bambooweb.mobilcom.de/rest/api/latest/result/ MCBS-MR74 12  mcbstest:password
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [plankey] der Epic-Name fuer dieses release
 * @param [buildnumber] die Build-Nummer (bamboo)
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 *
 * @author bmoeller
 *
 * @see AtlassianBambooInfo
 */

fun main(args : Array<String>) {
    // properties
    val bambooURL: String
    val credentials: String
    val plankey: String
    val buildnumber: String
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    bambooURL = args[0]
    logger.info("BambooURL : ${bambooURL}")
    plankey = args[1]
    logger.info("Plan : ${plankey}")
    buildnumber = args[2]
    logger.info("Build : ${buildnumber}")
    credentials = args[3]
    logger.info("Credentials : $credentials")

    val b = AtlassianBambooInfo(bambooURL,credentials)
    b.getTestArtifactsForBuild(plankey,buildnumber)
}