package components4branch

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File
import java.util.*

/**
 * Methode zur Ausgabe der von einem build betroffenen Komponenten und semantic-versions
 * - die JiraIssues der Branches sind ausgelesen worden und liegen in der datei issues.txt
 * - die relevanten Komponenten des Build-Produkts werden eingelesen (jiraResources)

 * - die Komponenten werden gruppiert und ausgegeben
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. build_components.BuildComponentsKt https://bambooweb.mobilcom.de/rest/api/latest/result/ MCBS-MR74 12  https://jira.freenet-group.de/rest/api/2/ mcbstest:password jiraExceptions
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
  *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {
    // properties
    val jiraURL : String
    val bambooURL : String
    val credentials : String
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    bambooURL = args[0]
    logger.info("BambooURL : ${bambooURL}")
    jiraURL = args[1]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[2]
    logger.info("Credentials : $credentials")

    val j = AtlassianJiraIssue(jiraURL, credentials)

    val issueList = ArrayList<String>()

    var complist : ArrayList<String>
    var compSet : MutableSet<String> = mutableSetOf("A")

    File("issues.txt").forEachLine {
        logger.debug("Zeile : $it")
        val texts = it.split(";")
        val issue = texts[0]
        issueList.add(issue)
    }

    
    // Komponenten der einzelnen Issues auslesen und in die Menge integrieren
    for (e in issueList){
        complist = j.getComponentsForIssue("${e}")
        logger.debug("CompList : " + complist.toString())
        for (comp in complist) {
            compSet.add(comp)
        }
    }
    // Ausgabe
    compSet.remove("A")
    compSet.remove("MCBS-Utilities")
    compSet.add("md-bm")


    println("Affected Release-Components  : ")
    println("=====================================")
    println("General : ")
    if (compSet.contains("MCBS allgemein")) {
        println("     " + "MCBS allgemein")
        compSet.remove("MCBS allgemein")
    }
    if (compSet.contains("MCBSServices (ZAP)")) {
        println("     " + "MCBSServices (ZAP)")
        compSet.remove("MCBSServices (ZAP)")
    }
    if (compSet.contains("Microservices") ) {
        println("     " + "Microservices")
        compSet.remove("Microservices")
    }
    if (compSet.contains("developer-tools")) {
        compSet.remove("developer-tools")
    }


    try {
        var scriptSet: HashSet<String> = hashSetOf("SQL Update Script", "SQL-Skript (MCBS-DB)")
        println("=====================================")
        println("DB-Changes : ")
        scriptSet.forEach { e ->
            if (compSet.contains(e)) {
                println("     " + e)
                compSet.remove(e)
            }
        }

        val mcbsserviceSet: HashSet<String> = hashSetOf("PartyService", "ContractService", "InvoiceService", "NetworkService", "FeeService", "MarkService", "StockService")
        println("=====================================")
        println("MCBS-Services : ")
        mcbsserviceSet.forEach { e ->
            if (compSet.contains(e)) {
                println("     " + e)
                compSet.remove(e)
            }
        }

        println("=====================================")
        println("Microservices : ")

        for (e in compSet) {
            if (e.substring(0, 3).equals("ms-")) {
                println("     " + e)
            }
        }

        compSet.forEach { e ->
            if (e.substring(0, 3).equals("md-")) {
                println("     " + e)
            }
        }

        println("=====================================")
        println("Modules : ")
        compSet.forEach { e ->
            if ((! e.substring(0, 3).equals("ms-")) and (! e.substring(0, 3).equals("md-"))) {
                println("     " + e)
            }
        }
    } catch (e : Exception){
        e.printStackTrace()
    }

    println()
    println("Semantic-Versions for : ")
    println("=====================================")

    issueList.forEach { e ->
        val labels = j.getKeywordsForIssue(e)
        var k : String =""
        labels.forEach {
            if (it == "Patch") {
                k = "Patch"
            }
            if (it == "Minor") {
                k = "Minor"
            }
            if (it == "Major") {
                k = "Major"
            }
        }
        println("${e} : ${k}")
    }
}