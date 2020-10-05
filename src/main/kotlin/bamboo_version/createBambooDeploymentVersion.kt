package bamboo_version

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.io.FileInputStream
import kotlin.system.exitProcess

/**
 * Methode zum Erzeugen einer zu deployenden Bamboo-Version
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.9.10.jar:. bamboo_version.createBambooDeploymentVersion  https://bambooweb.mobilcom.de/rest/api/latest/result/  "206799092"  ABRMS-VER-1204  bm-version_999998
 *
 * @author bmoeller
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [jiraCredentials] die Zugriffsdaten
 * @param [projectId]  die Id des Bamboo-Deployment-Projekts
 * @param [planResultKey] PlanKey + BuildNumber
 * @param [deploymentVersion] die anzulegende Version
 *
 */

fun main (args : Array<String>) {
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // Parameter
    logger.debug(args.toString())
    val bambooURL: String = args[0]
    logger.info("BambooURL : $bambooURL")
    val jiraCredentials: String = args[1]
    logger.info("Credentials : $jiraCredentials")
    val projectId: String = args[2]
    logger.info("ProjectId : $projectId")
    val planResultKey: String = args[3]
    logger.info("PlanResultKey : $planResultKey")
    val deploymentVersion: String = args[4]
    logger.info("DeploymentVersion : $deploymentVersion")
    // connect
    val b = AtlassianBambooInfo(bambooURL , jiraCredentials)
    // create Version
    if (b.createReleaseVersion(projectId,planResultKey,deploymentVersion)){
        exitProcess(0)
    } else {
        exitProcess(1)
    }

    // https://bambooweb.mobilcom.de/rest/api/latest/result/  "206799092"  ABRMS-VER-1204  bm-version_999998

}