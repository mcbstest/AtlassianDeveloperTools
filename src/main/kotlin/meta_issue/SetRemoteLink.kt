package meta_issue
import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.util.*

/**
 * Methode zum Setzen der "mentioned in-Verknüpfung" an einem JiraIssue
 * Aufruf :
 * java -cp .../AtlassianDeveloperTools-all-1.1.jar:. meta_issue.SetRemoteLinkKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password "" "mcbs_35.1" "META-152"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [component] die Komponente (e.g. ms-cuba)
 * @param [version] die zu setzende Versionsnummer ohne Komponente (e.g. mcbs_35.1 oder 2.4.7)
 * @param [metaIssue] das zugehörige Meta-Issue
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    val jiraURL : String
    val credentials : String
    var component : String
    var version : String
    val issue : String
    var page : String
    var title : String
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    jiraURL = args[0]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    component = args[2]
    logger.info("Komponente : ${component}")
    version = args[3]
    logger.info("Version : ${version}")
    issue = args[4]
    logger.info("META-Issue : ${issue}")

    val j = AtlassianJiraIssue(jiraURL, credentials)
    if (component.equals("")) {
        page = version
        title= version
    } else {
        page = component+"+"+version
        title = "${component} ${version}"
    }
    j.setConfluenceLink("${page}","${title}", "${issue}")

}