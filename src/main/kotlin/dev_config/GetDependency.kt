package dev_config

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.io.FileWriter
import java.util.*
/**
 * Methode zur Abfrage , ob ein Issue Dependencies unterliegt
 * - die Issues werden ausgelesen
 * - hat customfield_54490 Inhalt, wird dieser ausgelesen und weitergegeben
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.4.jar:. dev_config.CheckConfigKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password ABRMS-1633
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issueFile] property-File mit den zu berücksichtigenden Komponenten
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */
fun main(args : Array<String>) {
    // properties
    val properties = Properties()
    val p = Properties()
    var dependency = false
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameter
    logger.debug(args.toString())
    val jiraURL : String = args[0]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[1]
    logger.info("Credentials : $credentials")
    val issueFile : String = args[2]
    logger.info("File : $issueFile")

    val dependencyIssueList : ArrayList<String> = ArrayList<String>()
    var depString : String
    var dependencystring = ""
    // Connections to Atlassian
    val j = AtlassianJiraIssue(jiraURL, credentials)

    val issues : List<String> = File(issueFile).readLines()

    issues.forEach {
        depString = j.getDependencyForIssue(it)
        logger.debug("$it :: $depString")

        if (depString == "null") {
            logger.debug("No dependency !")
        } else {
            logger.info("$it : ${depString}\n")
            dependencystring += " !! $it : $depString  "
            dependency = true
            properties["dependency"] = "true"
            dependencyIssueList.add(it)
        }
    }

    // Schreiben
    logger.info("Dependency : $dependency")
    // Property-File for boolean "dependency"
    val propertiesFile = "dependency.properties"
    val fileWriter = FileWriter(propertiesFile)
    if (dependency) {
        properties["dependency"] = "true"
    } else {
        properties["dependency"] = "false"
    }
    properties.store(fileWriter, "Dependency ...")
    // Property-File for dependend issues
    val issueListFile = "dependendIssues.properties"
    val issueListWriter = FileWriter(issueListFile)
    if (dependency) {
        //p.put("dependendIssueList", dependencyIssueList.toString())
        p["dependendIssueList"] = "\"${dependencystring}\""
    } else {
        p["dependendIssueList"] = "[ ]"
    }

    p.store(issueListWriter, "Dependend issues ...")

    // Output to be checked
    //logger.info(dependencyIssueList.toString())
    logger.info("D : $dependencystring ###")

}
