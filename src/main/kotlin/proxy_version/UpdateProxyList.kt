package proxy_version

import atlassian_jira.AtlassianJiraIssue
import general_info.GeneralInfo
import org.apache.log4j.Logger
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Method to update the proxylist
 * File is downloaded from Confluence-Page (Bamboo) (proxyversions.csv)
 * Entry belonging to this proxy is updated
 * File has to by uploaded by Bamboo
 *
 * call :
 * java -Dlog4j.configuration=./log4j_debug.properties  -cp AtlassianDeveloperTools-all-x.y.z.jar:.:./AtlassianDeveloperTools  meta_issue.GetIBNDateKt "https://jira.freenet-group.de/rest/api/2/" "mcbstest:qs_mcbs_11" "META-206"
 *
 * @param [proxy] der Name des Proxy
 * @param [version] the version of the proxy
 * @param [file] the filename
 *
 * @author bmoeller
 *
 * @version 1.9.13
 * @see AtlassianJiraIssue
 */
fun main (args: Array<String>) {

    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameters
    logger.debug(args.toString())
    val proxy = args[0]
    logger.info("Proxy : $proxy")
    val version = args[1]
    logger.info("Version : $version")
    val file = args[2]
    logger.info("File : $file")

    val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy")
    val date = Date()
    // System.out.println(dateFormat.format(date));
    val gi = GeneralInfo()
    gi.writeProxy(proxy, version, dateFormat.format(date), file)

}