package bamboo_comment

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.io.FileInputStream
import kotlin.system.exitProcess

/**
 * Methode zum Erzeugen eines Kommentars an einem Bamboo-Build
 * Primäre Nutzung : Setzen / Hinzufügen der Jira-Issues über die Kommentarfunktion
 * Ein Löschen oder sonstiges Modifizieren der Jira-Issues ist nicht möglich
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.10.2.jar:. bamboo_comment.createBambooCommentKt  https://bambooweb.mobilcom.de/rest/api/latest/result/  mcbstest:qs_mcbs_11  ABRMS-VER 1204  Kommentar
 *
 * @author bmoeller
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [jiraCredentials] die Zugriffsdaten
 * @param [planKey] PlanKey
 * @param [buildNumber]
 * @param [comment] der anzulegende Kommentar
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
    val planKey: String = args[2]
    logger.info("PlanKey : $planKey")
    val buildNumber: String = args[3]
    logger.info("BuildNumber : $buildNumber")
    val comment: String = args[4]
    logger.info("Comment : $comment")
    // connect
    val b = AtlassianBambooInfo(bambooURL , jiraCredentials)
    // create Comment
    b.createBuildComment(planKey,buildNumber,comment)
}