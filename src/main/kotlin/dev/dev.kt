package dev

import atlassian_jira.AtlassianJiraIssue
import atlassian_bamboo.AtlassianBambooInfo
import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64

import gurock_testrail.TestRail

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.net.URLEncoder.encode
import java.util.*
import kotlin.collections.ArrayList

/**
 * JIRA-API-Access  //  Method for testing new-JIRA-API-Calls
 */
fun main(args : Array<String>) {

    val j = AtlassianJiraIssue("https://jira.freenet-group.de/rest/api/2/", "mcbstest:qs_mcbs_11")

    /*
    // TestRail-Connect
    val t = TestRail("https://testrail-test.mobilcom.de", "bmoeller","Odin-2303")
    // ProjektId auslesen
    val projectId = t.getPrjByName("Spielwiese")
    // Suite auslesen
    val suiteId = t.getSuiteByName(projectId,"mcbs")
    // Subsection für die Testcases auslesen
    val sectionId = t.getSectionByName(projectId,suiteId,"ms-customer")
    // TestcaseId aus der Section auslesen
    val testcaseId = t.getTestcaseByName(projectId,suiteId,sectionId,"tc_ms-customer")
    // Datum auslesen und für den Result-Name formatieren
    val currentDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = currentDate.format(formatter)
    // Testrun definieren , Testcase integrieren und Id auslesen
    val testrunId = t.addTestrun(projectId,suiteId, testcaseId,"2020-01-10_ms-customer_1.11.12")
    // TestId zum TestRun auslesen
    val testId = t.getTests(testrunId, "tc_ms-customer")
    // Testergebnis setzen (1 : passed , 5 : failed)
    println(t.addTestresult(testId, "1", "0.0.1","Ich bin der Kommentar"))
    */


    //j.ping()

    //j.getIssueState("META-152")
    //j.setTestVersion("META-152","1.0.0")
    //j.setGitVersion("META-152","1.0.10")
    //j.setIssueState("META-152","31")
    //j.getIBNDate("META-152")
    //j.addComment("META-152", "Hallo")
    //j.setTestOrder("META-152","MCBS-2175")
    //println(j.getTestOrder("META-152").toString())
    //j.createIssue("MCBS", "Aufgabe" , "0.0.000", "test purpose only", "To be ignored", "IBN : 01.01.1970")

    // Properties / Komponentenliste des Projekts auslesen
    //val p : Properties = j.readProperties("MCBS-MR")
    //val projectComponents = p.getProperty("components")
    //val compArray = projectComponents.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    // Properties / Jira-Exceptions (Prefixes) auslesen
    //val exceptions : Properties = j.readProperties("jiraExceptions")
    //val jiraExceptions = exceptions.getProperty("exception")
    //val exceptionArray = jiraExceptions.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    // for(x in exceptionArray){
    //     println("Exc : ${x}")
    // }
    //val componentsMap = j.getProjectComponentsToMap("MCBS")
    //println("${componentsMap["Actions"]}")

    // j.getTransitions("MCBS-2440")
    /*
    val exc = arrayOf("SPOC","UTF")

    val issue = "SPOC-1234"
    val pref = issue.split("-")
    println(pref[0])

    if (exc.contains(pref[0])) {
        println("Contains !")
    } else {
        println("Contains not!")
    }
    */
    //j.getIssueRemoteLink("META-170")
    //j.setConfluenceLink("mcbs_35.1","META-152")

    //println("getIssueType ...")
    // Athentifizierung
   // val auth = String(Base64.encode("mcbstest:qs_mcbs_11"))
    //val client = Client.create()
    // Connect
    //val webResource = client.resource("https://jira.freenet-group.de/rest/api/2/issue/ABRMS-1828?expand=changelog")
    // Request
    //val response = webResource.header("Authorization","Basic " + auth).type("application/json").accept("application/json").get(String::class.java)
    //println("Response : $response)")

    /*
    val infile = File("log4j.properties")

    val outfile = File("log4j.temp")
    outfile.writeText("")
    infile.forEachLine { it ->
        println(it)
        if(it.startsWith("log4j.appender.file.MaxFileSize")) {
            outfile.appendText("log4j.appender.file.MaxFileSize=12345MB"+"\n")
        } else {
            outfile.appendText(it+"\n")
        }
    }
    File("log4j.temp").copyTo(File("newlog4j.properties"), true);
    */

    //j.createError("MCBS","Fehler","V 1.2.3","Summary","Description"," ","MCBS")

    //var componentList = j.getComponentsForIssue("ABRMS-1576")
    //componentList = j.getComponentsForIssue("ABRMS-1604")

    //val x = j.getKeywordsForIssue("ABRMS-1625")
    //println("${x.toString()}")

    //var x = j.getComponentsForIssue("ABRMS-2040")
    //println(x.toString())
    //x = j.getComponentsForIssue("MCBS-1738")
    //println(x.toString())

    //var y = j.getSemanticVersionForIssue("ABRMS-1746")
    //println(y)

    //j.getSemanticVersionForReleaseNew("es-plugin-collection_2.0")



    //val g = GeneralInfo()
    //val s = j.getIssueId("ABRMS-1792")
    //val l = j.getComponentsForIssue("ABRMS-1792")

    //g.getIssuePullRequests("1470128","stash","repository")
    //g.getIssueBuilds("1470128","bamboo","build")
    //g.getIssueDeployments("1470128","bamboo","build")

    //j.getIssueType("ABRMS-1803")

    //val auth = String(Base64.encode("mcbstest:qs_mcbs_11"))
    //val client = Client.create()
    // Connect
    //val webResource = client.resource("https://jira.freenet-group.de/rest/api/2/issue/MCBS-2585")
    //val webResource = client.resource("http://apigateway-test.mobilcom.de/v1/authoritiesInformation/version")
    // Request
    //val response = webResource.header("Authorization","Basic " + auth).type("application/json").accept(MediaType.APPLICATION_JSON).get(ClientResponse::class.java)
    //val response = webResource.header("Authorization","Basic " + auth).accept(MediaType.APPLICATION_JSON).get(ClientResponse::class.java)
    //println("Response : ${response}")
    //println("Response (Entity): ${response.getEntity(String::class.java)}")
    //println("Response : ${response}")
    //println("Response (Entity) : ${response.getEntity(String::class.java)}")
    //println("Response : ${response.status}")
    //println("Response : $response.")

    /*
    // Tempo
    val comp = "ms-commondata"
    // logging
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.getName())
    // bamboo-connect
    val b = AtlassianBambooInfo("https://bambooweb.mobilcom.de/rest/api/latest/result/", "mcbstest:qs_mcbs_11")
    //val j = AtlassianJiraIssue("https://jira.freenet-group.de/rest/api/2/", "mcbstest:qs_mcbs_11")
    // get Issues for Build
    val issueList = b.getIssuesForBuild("ABRMS-MSCMR38", "5")
    // output-file
    val file = File("TempoListe.csv")
    // Date
    val currentDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formattedDate = currentDate.format(formatter)

    logger.debug("Issues : ${issueList}")
    logger.info("Tempo : ")
    logger.info("==============================")
    // iterate over the list
    issueList.forEach { it ->
        logger.debug("Issue : ${it}")
        val type = j.getIssueType(it)
        if (type == "Anforderung") {
            logger.debug("Type : ${type}")
            logger.info("Issue : $it :: ${j.getReq_ID(it)} :: ${j.getAssignee(it)} ")
            file.appendText("https://jira.freenet-group.de/browse/$it ; ${j.getSummary(it)} ; ${formattedDate} ; ${j.getReq_ID(it)} ; ${comp} ; ${j.getAssignee(it)}\n")
        }
    }
    */

     //j.queryReqId("30","Fehler,Anforderung","MCBS,ABRMS")
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)

    /*
    // referenced issues
    var ri:   ArrayList<String> = arrayListOf()
    // referenced list
    val referencedList: ArrayList<String> = arrayListOf()

    // get Issues for Build
    // bamboo-connect
    val b = AtlassianBambooInfo("https://bambooweb.mobilcom.de/rest/api/latest/result/", "mcbstest:qs_mcbs_11")
    val issueList = b.getIssuesForBuild("ABRMS-MCPMR42", "3")

    issueList.forEach { it ->
        logger.debug("Issue : ${it}")

        ri = j.getReferencedIssue("${it}")
        logger.debug("Referenzen für ${it} : ${ri}")

        ri.forEach { i ->
            referencedList.add(i)
        }
    }
    logger.debug("Liste : ${referencedList}")

    referencedList.forEach { it ->
        logger.debug("Definieren der Version ...")
        logger.debug("Setzen der FixVersion an ${it}")
        logger.debug("Setzen des Build-Kommentars an ${it}")
    }

    j.createVersion("IAT", "ms-customerproduct-mcbs_2.0.2")
*/
    //j.addFixVersion("IAT-15070","ms-customerproduct-mcbs_2.0.4")

    //j.addComment("IAT-15070", "Releasebuild : ms-customerproduct-mcbs_2.0.1")

    //j.addComment("IAT-15070","Deployment erfolgte auf 'TEST'")

    // Get all Sprints to get the sprintId
    /*
    // Authentifizierung
    val auth = String(Base64.encode("bmoeller:freYa_1402"))
    val client = Client.create()
    // Connect
    val webResource = client.resource("https://jira.freenet-group.de/rest/agile/1.0/board/454/sprint?startAt=80&maxResults=60")
    // Request
    val response = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
    logger.info("Response : $response)")
    // Response auswerten
    val parser = JSONParser()
    val json = parser.parse(response) as JSONObject
    val values = json["values"] as JSONArray
    values.forEach {
        val entry: JSONObject = it as JSONObject
        val id = entry["id"].toString()
        val name = entry["name"].toString()
        val state = entry["state"].toString()
        val goal = entry["goal"].toString()
        logger.info("$id  ::  $name  \t::  $state  ::  $goal")

    }
    */
    // j.getIssueId("META-210")

    //j.getReferencedIssue("ABCD-1234")

    //j.addComment("SPOC-421275", "Deployment auf 'Prod'")
      //j.getTransitions("META-223")

/*
    val file = File("issueInfo.csv")
    file.writeText("issue;summary;status,components;semanticVersion;keywords;priority;fixVersions;affectedVersions;dependency;\n")
    var i = ArrayList<String>()
    val issueList : List<String> = File("branch_issues.txt").readLines()
    for (issue in issueList) {
        logger.info(issue)
        i = j.getIssueInfos(issue)
        logger.info(i)
        var infoString = ""
        for (infoItem in i) {
            logger.debug("InfoItem  : $infoItem")
            infoString = infoString + infoItem + ";"
        }
        logger.info(infoString)
        file.appendText("$infoString\n")
    }
*/

    val name = "Bernd"

    if (! name.startsWith("ABe")) {
        println ("kein B")
    }

    // get components
    //val componentList = j.getComponentsForIssue("ABC-3007.xml")


    //val b = AtlassianBambooInfo("https://bambooweb.mobilcom.de/rest/api/latest/result/", "mcbstest:qs_mcbs_11")
    //b.createBuildComment("ABRMS-VER","1252","ABRMS-1234 ABRMS-3456 ABRMS-9876")

}
