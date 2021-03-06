package referenced_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.util.*
import kotlin.collections.ArrayList

/**
 * Method to set Deployment-Comments at the referenced issues
 * - if the project is IAT
 * - a comment is set
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.7.0.jar:. referenced_issue.AddDeployInfosKt "https://jira.freenet-group.de/rest/api/2/" "https://bambooweb.mobilcom.de/rest/api/latest/result/" "mcbstest:qs_mcbs_11" "ms-bm_1.1.1" "ABRMS-MCPMR42" "3"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff für die linked issues
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version]
 * @param [planKey]
 * @param [buildNumber]
 * @param [stage] e.g. TEST
 *
 * @author bmoeller
 *
 * @version 1.0
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {

    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameters
    logger.debug(args.toString())
    val jiraURL = args[0]
    logger.info("JiraURL : $jiraURL")
    val bambooURL = args[1]
    logger.info("BambooURL : $bambooURL")
    val credentials = args[2]
    val version = args[3]
    logger.info("Version : $version")
    logger.info("Credentials : $credentials")
    val planKey = args[4]
    logger.info("Plan : $planKey")
    val buildNumber = args[5]
    logger.info("Build : $buildNumber")
    val stage = args[6]
    logger.info("Stage : $stage")
    // Component-File
    val componentFile : String = args[7]
    logger.info("Components : $componentFile")
    // connect to Jira (Standard)
    val j = AtlassianJiraIssue(jiraURL, credentials)
    val b = AtlassianBambooInfo(bambooURL, credentials)
    // Properties / Komponentenliste des Projekts auslesen
    val p : Properties = j.readProperties(componentFile)
    val projectComponents = p.getProperty("components")
    val compArray = projectComponents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    // referenced issues
    var ri: ArrayList<String>
    // referenced list
    val referencedList: ArrayList<String> = arrayListOf()

    // get Issues for Build
    val issueList = b.getIssuesForBuild(planKey, buildNumber)

    issueList.forEach {
        logger.debug("Issue : $it")
        // Kompoonenten holen
        val complist : java.util.ArrayList<String> = j.getComponentsForIssue(it)
        // über alle Komponenten des Issue
        for (comp in complist) {
            logger.debug("Comp : $comp")
            // in den Vergleich mit den Komponenten der Datei
            // sind eine Komponente des Issue und der Datei gleich
            for (i in compArray.indices) {
                logger.debug("compArray[i] : $i : ${compArray[i]}")
                // sind eine Komponente des Issue und der Datei gleich
                if (compArray[i] == comp) {
                    // dann die solve-Infos setzen
                    logger.info("Anpassen : $it : ${compArray[i]} ")
                    // referenced Issue in die tatsächliche Referenzliste schreiben
                    ri = j.getReferencedIssue(it)
                    logger.debug("Referenzen für $it : $ri")

                    ri.forEach { i ->
                        referencedList.add(i)
                    }
                }
            }
        }


    }
    logger.debug("Liste : $referencedList")
    val s = mutableSetOf<String>()
    // make unique
    referencedList.forEach {
        s.add(it)
    }
    logger.info(s.toString())

    // referenced issues markieren
    s.forEach {

        logger.debug("Setzen des Build-Kommentars an $it...")
        j.addComment(it, "Releasebuild : $version :: Deployment auf $stage")

    }

    /*
    //j.addComment("IAT-15070","Deployment erfolgte auf 'TEST'")
    */

}