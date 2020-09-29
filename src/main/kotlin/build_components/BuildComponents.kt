package build_components

import atlassian_bamboo.AtlassianBambooInfo
import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.util.*

/**
 * Methode zur Ausgabe der von einem build betroffenen Komponenten
 * - die JiraIssues des Build werden ausgelesen
 * - die relevanten Komponenten des Build-Produkts werden eingelesen (jiraResources)
 * - die nicht zu berücksichtigenden Prefixe (e.g. UTF) werden gelesen (jiraResources)
 * - die nicht zu berücksichtigenden Prefixe werden ignoriert
 * - die Komponenten werden gruppiert und ausgegeben
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. build_components.BuildComponentsKt https://bambooweb.mobilcom.de/rest/api/latest/result/ MCBS-MR74 12  https://jira.freenet-group.de/rest/api/2/ mcbstest:password jiraExceptions
 *
 * @param [bambooURL] die URL fuer produktiven Bamboo-Zugriff
 * @param [plankey] der Epic-Name fuer dieses release
 * @param [buildnumber] die Build-Nummer (bamboo)
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [version] die zu setzende Versionsnummer
 * @param [exceptionFile] Property-File mit nicht zu beruecksichtigenden Prefixes (UTF , M;SBS etc.) (erwartet in jiraResources)
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
    val exceptionFile : String
    val plankey : String
    val buildnumber : String
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // parameter
    logger.debug(args.toString())
    bambooURL = args[0]
    logger.info("BambooURL : ${bambooURL}")
    plankey = args[1]
    logger.info("Plan : ${plankey}")
    buildnumber = args[2]
    logger.info("Build : ${buildnumber}")
    jiraURL = args[3]
    logger.info("JiraURL : ${jiraURL}")
    credentials = args[4]
    logger.info("Credentials : $credentials")
    exceptionFile = args[5]
    logger.info("Exceptions : ${exceptionFile}")

    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Properties / Jira-Exceptions (Prefixes) auslesen
    val exceptions : Properties = j.readProperties(exceptionFile)
    val jiraExceptions = exceptions.getProperty("exception")
    val exceptionArray = jiraExceptions.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

    val b = AtlassianBambooInfo(bambooURL,credentials)
    val issuelist = b.getIssuesForBuild(plankey,buildnumber)

    var complist : ArrayList<String>
    var compSet : MutableSet<String> = mutableSetOf("A")

    // Exception-Issues entfernen
    for (ex in exceptionArray){
        if (issuelist.contains(ex)) { issuelist.remove(ex) }
    }


    // Komponenten der einzelnen Issues auslesen und in die Menge integrieren
    for (e in issuelist){
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


    println("Affected Release-Components in # "+ buildnumber + " : ")
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
    println("Semantic-Versions for # "+ buildnumber + " : ")
    println("=====================================")

    issuelist.forEach { e ->
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