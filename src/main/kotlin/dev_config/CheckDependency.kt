package dev_config

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.FileWriter
import java.util.*
/**
 * Methode zur Abfrage , ob ein Issue Dependencies unterliegt
 * - die Komponenten des Issue werden ausgelesen
 * - Enthält die Keywordliste "dependency" wird "TRUE" im Dependency-Propertyfile gesetzt, ansonsten false
 * - Neu : hat customfield_54490 Inhalt, wird dieser ausgelesen und weitergegeben
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.4.jar:. dev_config.CheckConfigKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password ABRMS-1633
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [issue] das zu prüfende Ticket
 * @param [exceptionFile] Property-File mit nicht zu beruecksichtigenden Prefixes (UTF , M;SBS etc.) (erwartet in jiraResources)
 * @param [componentFile] property-File mit den zu berücksichtigenden Komponenten
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */
//  "${bamboo.bambooURL}" "${bamboo.jiraURL}" "${bamboo.jiraCredentials}" "${bamboo.planKey}" "${bamboo.buildNumber}" jiraExceptions
fun main(args : Array<String>) {
    // properties
    val properties = Properties()
    val p = Properties()
    var dependency = false
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameter
    logger.debug(args.toString())
    val bambooURL: String = args[0]
    logger.info("BambooURL : $bambooURL")
    val jiraURL : String = args[1]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[2]
    logger.info("Credentials : $credentials")
    val plankey: String = args[3]
    logger.info("PlanKey : $plankey")
    val buildnumber : String = args[4]
    logger.info("Build : $buildnumber")
    val exceptionFile : String = args[5]
    logger.info("ExceptionFile : $exceptionFile")
    // Component-File
    val componentFile : String = args[6]
    logger.info("Components : $componentFile")

    val dependencyIssueList : ArrayList<String> = ArrayList<String>()
    var depString : String
    var dependencystring = ""
    // Connections to Atlassian
    val j = AtlassianJiraIssue(jiraURL, credentials)
    val b = AtlassianBambooInfo(bambooURL,credentials)
    // Properties / Komponentenliste des Projekts auslesen
    val cp : Properties = j.readProperties(componentFile)
    val projectComponents = cp.getProperty("components")
    val compArray = projectComponents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    // Exception-Issues
    // Properties / Jira-Exceptions (Prefixes) auslesen
    val exceptions : Properties = j.readProperties(exceptionFile)
    val jiraExceptions = exceptions.getProperty("exception")
    val exceptionArray = jiraExceptions.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for(x in exceptionArray){
        logger.debug("Exc : $x")
    }
    // Am Build beteiligte Jira-Issues aus Bamboo auslesen
    val issuelist = b.getIssuesForBuild(plankey,buildnumber)

    for (e in issuelist){

        val pref = e.split("-")
        if ( !exceptionArray.contains(pref[0])) {

            //neu
            logger.debug("Issue : $e")
            // Kompoonenten holen
            val complist : ArrayList<String> = j.getComponentsForIssue(e)
            // über alle Komponenten des Issue
            for (comp in complist) {
                logger.debug("Comp : $comp")
                // in den Vergleich mit den Komponenten der Datei
                // sind eine Komponente des Issue und der Datei gleich
                for (i in compArray.indices) {
                    logger.debug("compArray[i] : $i : ${compArray[i]}")
                    // sind eine Komponente des Issue und der Datei gleich
                    if (compArray[i] == comp) {

                        depString = j.getDependencyForIssue(e)
                        logger.debug(depString)

                        //(!depString.equals("") && (!depString.equals(null))
                        if (depString == "null") {

                        } else {
                            logger.info("$e : ${depString}\n")
                            dependencystring += " !! $e : ${depString}\n"
                            dependency = true
                            properties["dependency"] = "true"
                            dependencyIssueList.add(e)
                        }
                    }
                }
            }
        } else {
            println("$e :: Keine Bearbeitung !")
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
