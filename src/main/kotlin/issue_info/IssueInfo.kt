package issue_info

import atlassian_jira.AtlassianJiraIssue
import atlassian_bamboo.AtlassianBambooInfo
import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64
import general_info.GeneralInfo
import org.apache.log4j.Logger
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.ArrayList

/**
 * JIRA-API-Access  //  Method for testing new-JIRA-API-Calls
 */
fun main(args : Array<String>) {

    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())

    val issue = args[0]
    logger.info("Issue : ${issue}")

    val j = AtlassianJiraIssue("https://jira.freenet-group.de/rest/api/2/", "mcbstest:qs_mcbs_11")

    val g = GeneralInfo()
    val s = j.getIssueId("${issue}")
    // val l = j.getComponentsForIssue("${issue}")

    g.getIssuePullRequests(s)
    g.getIssueBuilds(s)
    g.getIssueDeployments(s)
}
