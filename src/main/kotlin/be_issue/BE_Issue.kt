package be_issue

import atlassian_jira.AtlassianJiraIssue
import org.apache.log4j.Logger

/**
 * Methode um JIRA-BE-Orders zu erzeugen, zuzuweisen und zu beobachten
 * - Erzeugung des Issue im Projekt "SUPAPP"
 * - interne Zuweisung zur weiteren Bearbeitung
 * - Beobachter werden gesetzt
 * - die endgültige Zuweisung erfolgt manuell nach der Vervollständigung
 *
 * Aufruf :
 * java -cp ./devtools/apps/atltools/AtlassianDeveloperTools-all-1.1.jar:.:./devtools/apps/atltools/AtlassianDeveloperTools be_issue.BE_IssueKt https://jira.freenet-group.de/rest/api/2/ mcbstest:qs_mcbs_11 SUPAPP Aufgabe bmoeller bmoeller "${bamboo.release.version}" ContractService "${bamboo.mcbsepic}" "${bamboo.db_changes}" "${bamboo.mcbsserv.releaseVersion}" "${bamboo.deploy_asap}" "${bamboo.jiraproduct}"
 *
 * @param [jiraURL] die URL fuer produktiven JIRA-Zugriff
 * @param [credentials] login:password fuer produktiven JIRA-Zugriff
 * @param [project] das JiraProjekt, in dem der DB-Issue erzeugt werden soll
 * @param [ordertype] der OrderType als Text (e.g. Aufgabe , Service-Auftrag)
 * @param [assignee] der username / login
 * @param [watcher] eine komma-separierte Liste der anzulegenden Beobachter
 * @param [version] die versionsnummer
 * @param [product] Die Produktbezeichnung
 * @param [epic] das Epic zum Release
 * @param [dbchanges] ja / nein
 * @param [rel_dep] Release-Dependency (weist auf Release-Abhängigkeiten und auch auf das zug. Artifactory-Release hin
 * @param [deploy_asap] Hinweis auf die Dringlichkeit
 * @param [jira_product] das betroffene Produkt in jira-Notation ( mcbsserv , cose )
 *
 * @author bmoeller
 *
 * @see AtlassianJiraIssue
 */

fun main(args : Array<String>) {

    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    logger.debug(args.toString())
    val jiraURL : String = args[0]
    logger.info("JiraURL : $jiraURL")
    val credentials : String = args[1]
    logger.info("Credentials : $credentials")
    val project : String = args[2]
    logger.info("Project : $project")
    val ordertype : String = args[3]
    logger.info("Order : $ordertype")
    val assignee : String = args[4]
    logger.info("Assignee : $assignee")
    val watchlist : String = args[5]
    val watchers  = watchlist.split(",")
    logger.info("Watchers : $watchers")
    val version : String = args[6]
    logger.info("Version : $version")
    val product : String = args[7]
    logger.info("Produkt : $product")
    val epic : String = args[8]
    logger.info("Epic : $epic")
    val dbchanges : String = args[9]
    logger.info("DB-Changes : ${dbchanges}")
    val rel_dep : String = args[10]
    logger.info("Release-Dependency : $rel_dep")
    val deploy_asap : String = args[11]
    logger.info("Deployment (asap) : $deploy_asap")
    val jira_product : String = args[12]
    logger.info("Jira_Product : $jira_product")


    val j = AtlassianJiraIssue(jiraURL, credentials)
    val ibn = j.getIBNDate(epic)
    val summary = j.getSummary(epic)
    val s = j.getSemanticVersionForReleaseNew("${jira_product}_${version}")

    val d1 = "Es wurde eine neue Backend-Komponente zur Integration erzeugt  :\\r\\n\\r\\n $product :: $version \\r\\n\\r\\n"
    val d2 = "Semantic-Version : ${s}\\r\\n\\r\\n "
    val d3 = "Relase : https://jira.freenet-group.de/browse/${epic}  (${summary})\\r\\n "
    val d4 = "IBN (Prod) : ${ibn}\\r\\n\\r\\n "
    val d5 = "Release-Dependency : ${rel_dep}\\r\\n\\r\\n "
    val d6 = "Deployment / Integration (asap) : $deploy_asap \\r\\n\\r\\n "
    val d7 = "DB-Changes (Ja/Nein): $dbchanges \\r\\n\\r\\n "
    val d8 : String
    d8 = if (dbchanges == "Nein") {
        "Artifactory :  \\r\\n\\r\\n "
    } else {
        "Artifactory : http://artifactory.mobilcom.de/artifactory/webapp/#/artifacts/browse/tree/General/md-release/de/md/mcbs/${rel_dep} \\r\\n\\r\\n "
    }
    val d9 = "Releasenotes : https://kiwi.freenet-group.de/display/AERP/${product}+${version}\\r\\n \\r\\n"
    val d10 = "Bemerkungen : \\r\\n \\r\\n \\r\\n"

    val description = d1+d2+d3+d4+d5+d6+d7+d8+d9+d10+"Danke! "
    logger.info(description)
    val issue = j.createIssue(project, ordertype, version,"Backend-Komponente $product :: $version zur Integration", description," ")
    logger.info("$issue")
    // interne Zuweisung
    j.setAssignee(issue,assignee)
    // Beobachter
    watchers.forEach { watcher:String ->
        j.addWatcher(issue, watcher)
    }
    logger.info("#########################")
    logger.info("# Backend : $issue #")
    logger.info("#########################")
}