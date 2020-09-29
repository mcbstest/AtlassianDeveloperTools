package jira_issue

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.util.*

/**
 * Methode zum Setzen der FixVersion an allen relevanten JiraIssues eines Build
 * - die JiraIssues des Build werden ausgelesen
 * - die relevanten Komponenten des Build-Produkts werden eingelesen (jiraResources)
 * - die nicht zu berücksichtigenden Prefixe (e.g. UTF) werden gelesen (jiraResources)
 * - die nicht zu berücksichtigenden Prefixe werden ignoriert
 * - die Komponenten der Build-Issues werden mit den Produkt-Komponenten verglichen
 * - bei Uebereinstimmung wird die FixVersion gesetzt
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. jira_issue.SetFixVersionKt https://bambooweb.mobilcom.de/rest/api/latest/result/ MCBS-MR74 12  https://jira.freenet-group.de/rest/api/2/ mcbstest:password 0.0.0099 META-152 MCBS-MR jiraExceptions
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [plankey] der Epic-Name fuer dieses release
 * @param [buildnumber] die Build-Nummer (bamboo)
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version] die zu setzende Versionsnummer
 * @param [componentFile] Property-File mit relevanten Komponenten (erwartet in jiraResources)
 * @param [exceptionFile] Property-File mit nicht zu beruecksichtigenden Prefixes (UTF , M;SBS etc.) (erwartet in jiraResources)
  *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // parameter
    logger.debug(args.toString())
    val bambooURL : String = args[0]
    logger.info("BambooURL : $bambooURL")
    val plankey : String = args[1]
    logger.info("Plan : $plankey")
    val buildnumber : String = args[2]
    logger.info("Build : $buildnumber")
    val jiraURL : String = args[3]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[4]
    logger.info("Credentials : $credentials")
    val version : String = args[5]
    logger.info("Version : $version")
    val componentFile : String = args[6]
    logger.info("Components : $componentFile")
    val exceptionFile : String = args[7]
    logger.info("Exceptions : $exceptionFile")

    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Properties / Komponentenliste des Projekts auslesen
    val p : Properties = j.readProperties(componentFile)
    val projectComponents = p.getProperty("components")
    val compArray = projectComponents.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    // Properties / Jira-Exceptions (Prefixes) auslesen
    //val exceptions : Properties = j.readProperties(exceptionFile)
    //val jiraExceptions = exceptions.getProperty("exception")
    //val exceptionArray = jiraExceptions.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
     //for(x in exceptionArray){
     //    println("Exc : $x")
     //}


    val b = AtlassianBambooInfo(bambooURL,credentials)
    val issuelist = b.getIssuesForBuild(plankey,buildnumber)
    var complist : ArrayList<String>
    for (e in issuelist){

        //val pref = e.split("-")
        //if ( !exceptionArray.contains(pref[0])) {
            println("$e :: SetFixVersion !")
            complist = j.getComponentsForIssue(e)
            logger.debug("CompList : $complist")
            for (comp in complist) {
                logger.debug("Comp : $comp")
                for (i in compArray.indices) {
                    logger.debug("compArray[i] : $i : ${compArray[i]}")
                    if (compArray[i] == comp) {
                        logger.info("Anpassen : $e : ${compArray[i]} ")
                        j.addFixVersion(e, version)
                    }
                }
            }
        //} else {
        //    println("$e :: Keine Bearbeitung !")
        //}
    }
}