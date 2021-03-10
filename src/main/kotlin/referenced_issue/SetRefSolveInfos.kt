package referenced_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.util.*
import kotlin.collections.ArrayList

/**
 * Method to set Solve-Infos at the referenced issues
 * - if the project is IAT
 * - the version is created
 * - based on a referencelist the concerned issues are updated
 *
 * call :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.7.0.jar:. referenced_issue.SetSolveInfosKt "https://jira.freenet-group.de/rest/api/2/" "mcbstest:qs_mcbs_11" "ms-bm" "1.1.1" "ABRMS-1234 ABRMS-3456"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [component] die Komponente z. Version bzw. Component-File
 * @param [version] die zu erzeugende Version e.g. ms-billingaccount-frontend_99.99.99
 * @param [issues] Zeichenkette (sep.) mit zu berücksichtigenden Issues
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
    val credentials = args[1]
    logger.info("Credentials : $credentials")
    val component : String = args[2]
    logger.info("Component : $component")
    val version = args[3]
    logger.info("Version : $version")
    var issues = args[4]
    logger.info("Issues : $issues")
    issues=issues.trim()

    // connect to Jira (Standard)
    val j = AtlassianJiraIssue(jiraURL, credentials)

    // Properties / Komponentenliste des Projekts auslesen
    val p : Properties = j.readProperties(component)
    val projectComponents = p.getProperty("components")
    val compArray = projectComponents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    // referenced issues
    var ri: ArrayList<String>
    // referenced list
    val referencedList: ArrayList<String> = arrayListOf()
    // Iteration über die Liste
    val issueList: List<String> = issues.split(" ").map { it.trim() }
    issueList.forEach {
        logger.debug("Issue : $it")
        // Komponenten holen
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
        if (it.startsWith("IAT")) {
            logger.debug("Definieren der Version ...")
            j.createVersion("IAT", "${component}_${version}")
            logger.debug("Setzen der FixVersion an $it ...")
            j.addFixVersion(it, "${component}_${version}")
        }
        logger.debug("Setzen des Build-Kommentars an $it...")
        j.addComment(it, "Releasebuild : $component $version \\n Diese Lösung ist testbereit nach dem Deployment dieser Version auf der für den Test vorgesehenen Plattform (GIT / PET)")
        j.setAssignee(it,"jiraittste ")
    }

}