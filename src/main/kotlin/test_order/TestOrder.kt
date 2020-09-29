package test_order

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger
import java.io.File

/**
 * Method to create , assign and watch JIRA-TestOrders.
 * - If this is the first build of a release (bamboo-buildnumber = 1) then the version x.y.z (for the headline of the issue) is cut to x.y .
 * - If the buildnumber is higher, the version is taken completely (x.y.z) .
 * - Issue is created , assigned (internal), watchers are added
 * - planned activation-date is read out of epic and added to content .
 * - testorder is related to epic
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. test_order.TestOrderKt https://jira.freenet-group.de/rest/api/2/ mcbstest:password MCBS Auftrag bmoeller bmoeller,bmoeller 0.0.0099 META-152 1 "Z,T,C,A,B,K,G,S"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [project] das JiraProjekt , in dem die TestOrder anzulegen ist
 * @param [ordertype] der OrderType (e.g. Aufgabe , Service-Auftrag)
 * @param [assignee] der zuzuweisende username / login
 * @param [watcher] eine komma-separierte Liste der einzutragenden Beobachter
 * @param [version] die Version
 * @param [epic] das Epic zum Release
 * @param [buildnumber] die aktuelle bamboo-buildnumber
 * @param [testjob] eine komma-separierte Liste von Großbuchstaben als Abkürzung für die zu erzeugenden TestOrders. TestOrder-Inhalt wird gelesen aus "./jiraResources/TestOrderText.txt"
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 *
 * @since 1.0
 */
fun main(args : Array<String>) {
    val jiraURL : String
    val credentials : String
    val project : String
    val ordertype : String
    val assignee : String
    var watcher : String
    var version : String
    val epic : String
    val buildnumber : String
    val testjob : String

    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    logger.info(args)
    jiraURL = args[0]
    logger.info("JiraURL : $jiraURL")
    credentials = args[1]
    logger.info("Credentials : $credentials")
    project = args[2]
    logger.info("Projekt : $project")
    ordertype = args[3]
    logger.info("Order : $ordertype")
    assignee = args[4]
    logger.info("Assignee : $assignee")
    watcher = args[5]
    val watchers  = watcher.split(",")
    logger.info("Watchers : $watchers")
    version = args[6]
    logger.info("Version : $version")
    epic = args[7]
    logger.info("EPIC : $epic")
    buildnumber = args[8]
    testjob = args[9]
    logger.info("Tests : $testjob")
    val tests =  testjob.split(",")

    if (buildnumber == "1") {
        var splittedVersion = args[6].split(".")
        logger.info(splittedVersion[0])
        version = splittedVersion[0]+"."+splittedVersion[1]
    }
    // JIRA-Instanz
    val j = AtlassianJiraIssue(jiraURL, credentials)
    // Inbetriebnahmedatum holen
    val ibn : String = j.getIBNDate(epic)

    // Iteration über die testorder-Datei
    File("./jiraResources/TestOrderText.txt").forEachLine {
        logger.debug("Zeile : $it")
        val texts = it.split("|")
        val testtext = texts[0]
        val summary = texts[1]
        val description = texts[2]

        tests.forEach { t: String? ->
            if (t == testtext) {
                logger.debug("Test : $summary")
                // TestOrder erzeugen
                val issue = j.createIssue(project,ordertype, "0.0.0" , "QS :: ${version} :: ${summary}", description, "IBN! : ${ibn}")
                logger.info("TestOrder : ${issue}")
                // TestOrder zuweisen
                j.setAssignee(issue,assignee)
                // Beobachter setzen
                watchers.forEach { watcher:String ->
                    j.addWatcher(issue, watcher)
                }
                // TestOrder mit EPIC verknüpfen
                j.setTestOrder(epic,issue)
                logger.info("${issue} :: ${version} :: ${summary}")
            }
        }
    }

}
