package gurock_testrail

import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Klasse zur TestRail-Kommunikation
 * - Auslesen der Projekte
 * - Auslesen der Strukturen (Suites , Sections)
 * - Auslesen der TestCases
 * - Erzeugen von TestRuns
 * - Erzeugen von TestResults
  *
 * @param testrailURL Die TestRail-URL für die Etablierung des Zugriffs
 * @param testrailUser Das Login zu TestRail (in LDAP bekannt, eindeutige E-Mail-Adresse , API-Key in TestRail erzeugt)
 * @param testrailPasswd Das Passwort dazu
 *
 * @author Bernd Moeller
 *
 * @version 1.0
 *
 * @since 1.0
 */
class TestRail (testrailURL : String, testrailUser : String, testrailPasswd : String) {
    /** Instanz zum Aufruf der testRail-Methoden */
    val testrailClient = APIClient(testrailURL)

    /** User-Account zur TestRail-Anmeldung ( LDAP ) */
    val testrailUser = testrailUser

    /** Passwort zum TestRail-Account  */
    private val testrailPassword = testrailPasswd

    /** Logging */
    val logger = Logger.getLogger(TestRail::class.java.name)


    /**
     * Funktion zum Anlegen eines TestRail-Milestones
     * - Name-Pattern : "{Sprintnumber}-IBN! {name}
     * - Description : Dev : xx-yy , GIT: xx-yy
     * - Due_on : IBN
     *
     * @param prjid Die Id des Projektes , in dem der Milestone angelegt wird
     * @param number Die MCBS-Core-Sprintnummer (wie in Jira)
     * @param name Der Name des Sprints im Klartext
     * @param dev Der Entwicklungszeitraum als String
     * @param git Der Testzeitraum als String
     * @param ibn Der Inbetriebnahmetermin
     *
     * @author Bernd Moeller
     *
     * @version 1.95
     *
     * @since 1.0
     */
    fun addMilestone(prjid: String, number: String, name: String, dev: String, git: String, ibn: String) {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword

        logger.info("addMilestone ...")
        // hashmap
        val data: HashMap<String, Any> = HashMap<String, Any>()
        data["name"] = "${number}-IBN! ${name}"
        data["description"] = "Dev: ${dev} \nGIT: ${git}"
        val sdf = SimpleDateFormat("dd.MM.yyyy").parse(ibn)
        val unixTime: Long = sdf.getTime() / 1000L
        data["due_on"] = "${unixTime}"
        // check !
        logger.debug(data)
        // Request
        val milestone = testrailClient.sendPost("add_milestone/${prjid}", data) as JSONObject
        // Kontrollausgabe
        logger.debug(milestone)

    }



    /**
     * Funktion zum Auslesen der TestRail-Projekt-Id
     * - Auslesen der Projekte
     * - namentlicher Vergleich der Projekte mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param prjName Der Name des Projektes, dessen Id zurueckgegeben werden soll
     *
     * @return prjId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getPrjByName(prjName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword

        var prjid = "NONE"
        logger.info("getProjects ...")
        // Request
        val projects: JSONArray = testrailClient.sendGet("get_projects") as JSONArray
        // Kontrollausgabe
        logger.debug(projects)
        // Iteration ueber das result
        for (prj in projects) {
            val project = prj as JSONObject
            val prjname = project["name"]
            // ist es das erwartete ?
            if (prjname!! == prjName) {
                // dann die Id merken
                prjid = project["id"].toString()
            }
        }
        logger.info("$prjName :: $prjid")
        return prjid
    }

    /**
     * Funktion zum Auslesen der TestRail-Suite-Id
     * - Auslesen der Suiten
     * - namentlicher Vergleich der Suiten mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param prjId Die Id des Projekts
     * @param suiteName Der Name der Section, deren Id zurueckgegeben werden soll
     *
     * @return suiteId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getSuiteByName(prjId: String, suiteName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        var suiteid = "NONE"
        logger.info("getSuites ...")
        // Request
        val suites: JSONArray = testrailClient.sendGet("get_suites/${prjId}") as JSONArray
        logger.debug(suites)
        logger.debug(suites.size)
        // Iteration ueber das result
        for (suite in suites) {
            val s = suite as JSONObject
            val sName = s["name"]

            if (suiteName == sName) {
                suiteid = s["id"].toString()
            }

        }
        logger.info("$suiteName :: $suiteid")
        return suiteid

    }


    /**
     * Funktion zum Auslesen der TestRail-Section-Id
     * - Auslesen der Sections
     * - namentlicher Vergleich der Sections mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param suiteName Der Name der Section, deren Id zurueckgegeben werden soll
     *
     * @return sectionId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getSectionByName(prjId: String, suiteId: String, sectionName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        var sectionid = "NONE"
        logger.info("getSections ...")
        // Request
        val sections: JSONArray = testrailClient.sendGet("get_sections/${prjId}&suite_id=${suiteId}") as JSONArray
        logger.debug(sections)
        logger.debug(sections.size)
        // Iteration ueber das result
        for (section in sections) {
            val s = section as JSONObject
            val sName = s["name"]

            if (sectionName == sName) {
                sectionid = s["id"].toString()
            }

        }
        logger.info("$sectionName :: $sectionid")
        return sectionid

    }


    /**
     * Funktion zum Auslesen der TestRail-Section-Id
     * - Auslesen der Sections
     * - namentlicher Vergleich der Sections mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param prjId Die Id des Projekts
     * @param suiteId Die Id der Suite
     * @param sectionName Der Name der Section, deren Id zurueckgegeben werden soll
     *
     * @return sectionId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getTestcaseByName(prjId: String, suiteId: String, sectionId: String, testcaseName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        var testcaseid = "NONE"
        logger.info("getTestcases ...")
        // Request
        val testcases: JSONArray = testrailClient.sendGet("get_cases/${prjId}&suite_id=${suiteId}&section_id=${sectionId}") as JSONArray
        logger.debug(testcases)
        logger.debug(testcases.size)
        // Iteration ueber das result
        for (testcase in testcases) {
            val t = testcase as JSONObject
            val tName = t["title"]

            if (testcaseName == tName) {
                testcaseid = t["id"].toString()
            }

        }
        logger.info("$testcaseName :: $testcaseid")
        return testcaseid

    }


    /**
     * Funktion zum Anlegen eines TestRail-Testrunn
     * - Anlegen eines Testrun
     * - Rueckgabe der Id oder "NONE"
     *
     * @param prjId Die Id des Projekts
     * @param suiteId Die Id der Suite
     * @param sectionId Die Id der Section
     * @param testcaseId Die Id des TestCase
     * @param testrunName Der Name des Testrun, der angelegt werden soll
     *
     * @return testrunid
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun addTestrun(prjId: String, suiteId: String, testcaseId: String, testrunName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        val testrun: JSONObject
        logger.info("addTestrun ...")
        // hashmap
        val data: HashMap<String, Any> = HashMap<String, Any>()
        data["name"] = testrunName
        data["description"] = "..."
        data["suite_id"] = suiteId
        // nicht alle TestCases integrieren, sondern ...
        data["include_all"] = false
        // nur diesen einen ...
        data["case_ids"] = listOf(testcaseId)
        // check !
        logger.debug(data)
        // Request
        testrun = testrailClient.sendPost("add_run/${prjId}", data) as JSONObject
        // Kontrollausgaben
        logger.debug(testrun)
        logger.info(testrun["id"])
        return testrun["id"].toString()
    }


    /**
     * Funktion zum Auslesen der Id eines Tests eines Testrun
     * - Auslesen der Tests
     * - namentlicher Vergleich der Tests mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param testrunId Die Id des TestRun
     * @param testcaseName Der Name des Testcase, dessen Id am Testrun zu ermitteln ist
     *
     * @return sectionId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getTests(testrunId: String, testcaseName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        var testid = "NONE"
        logger.info("getTests ...")
        // Request
        val tests: JSONArray = testrailClient.sendGet("get_tests/${testrunId}") as JSONArray
        logger.debug(tests)
        logger.debug(tests.size)
        // Iteration ueber das result
        for (test in tests) {
            val t = test as JSONObject
            val tName = t["title"]
            // Gefunden ?
            if (testcaseName == tName) {
                // merken !
                testid = t["id"].toString()
            }

        }
        logger.info("$testcaseName :: $testid")
        return testid

    }


    /**
     * Funktion zum Anlegen eines TestResults
     * - Anlegen eines Testresults
     * - mit Status, Version und Kommentar
     * - Rueckgabe der Id oder "NONE"
     *
     * @param testId Die Id des Tests
     * @param status Der Status , der gesetzt werden soll
     * @param version Die getestete Version
     * @param comment Der Kommentar zum Test
     *
     * @return testresultid
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun addTestresult(testId: String, status: String, version: String, comment: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        val testresult: JSONObject
        val testresultid: String
        logger.info("addTestresult ...")
        // hashmap
        val data: HashMap<String, String> = HashMap<String, String>()
        data["status_id"] = status
        data["comment"] = comment
        data["version"] = version
        // Request
        testresult = testrailClient.sendPost("add_result/${testId}", data) as JSONObject
        // Kontrollausgaben
        logger.debug(testresult)
        logger.info(testresult["id"])
        testresultid = testresult["id"].toString()
        return testresultid
    }

    /**
     * Funktion zum Auslesen der MilestoneId
     * - Auslesen der Milestones
     * - namentlicher Vergleich der Milestones mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param prjId Die Id des Projekts
     * @param milestoneName Der Name des Milestones dessen Id gesucht wird
     *
     * @return sectionId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getMilestoneByName(prjId: String, milestoneName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        var milestoneId = "NONE"
        logger.info("getMilestones ...")
        // Request
        val milestones: JSONArray = testrailClient.sendGet("get_milestones/${prjId}") as JSONArray
        logger.debug(milestones)
        logger.debug(milestones.size)
        // Iteration ueber das result
        for (milestone in milestones) {
            val m = milestone as JSONObject
            val mName = m["name"]

            if (milestoneName == mName) {
                milestoneId = m["id"].toString()
            }

        }
        logger.info("$milestoneName :: $milestoneId")
        return milestoneId

    }

    /**
     * Funktion zum Auslesen der TestrunId
     * - Auslesen der Testruns
     * - namentlicher Vergleich der Testruns mit dem uebergebenen Namen
     * - Rueckgabe der Id oder "NONE"
     *
     * @param prjId Die Id des Projekts
     * @param testrunName Der Name des Testruns dessen Id gesucht wird
     *
     * @return testrunId
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getTestrunByName(prjId: String, testrunName: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        var testrunId = "NONE"
        logger.info("getTestruns ...")
        // Request
        val testruns: JSONArray = testrailClient.sendGet("get_runs/${prjId}") as JSONArray
        logger.debug(testruns)
        logger.debug(testruns.size)
        // Iteration ueber das result
        for (milestone in testruns) {
            val m = milestone as JSONObject
            val mName = m["name"]

            if (testrunName == mName) {
                testrunId = m["id"].toString()
            }

        }
        logger.info("$testrunName :: $testrunId")
        return testrunId
    }

    /**
     * Funktion zum Setzen des Milestones an einem Testrun
     *
     * @param testrunId Die Id des Testrun, der aktualisiert werden soll
     * @param milestonId Die Id des zu setzenden Milestones
     *
     * @return testrunid
     *
     * @author Bernd Moeller
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun setMilestone(testrunId: String, milestoneId: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        val testrun: JSONObject
        logger.info("updateTestrun ...")
        // hashmap
        val data: HashMap<String, Any> = HashMap<String, Any>()
        data["milestone_id"] = milestoneId
        // check !
        logger.debug(data)
        // Request
        testrun = testrailClient.sendPost("update_run/${testrunId}", data) as JSONObject
        // Kontrollausgaben
        logger.debug(testrun)
        logger.info(testrun["id"])
        return testrun["id"].toString()
    }

    fun closeTestRun(testrunId: String): String {
        // credentials
        testrailClient.user = testrailUser
        testrailClient.password = testrailPassword
        // Variablen
        val testrun: JSONObject
        logger.info("Close TestRun ...")
        // hashmap
        val data: HashMap<String, Any> = HashMap<String, Any>()
        data["testrun_id"] = testrunId
        // check !
        logger.debug(data)
        // Request
        testrun = testrailClient.sendPost("close_run/${testrunId}", data) as JSONObject
        // Kontrollausgaben
        logger.debug(testrun)
        logger.info(testrun["id"])
        return testrun["id"].toString()

    }
}