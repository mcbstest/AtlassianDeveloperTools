package general_info

//import com.sun.jersey.core.util.Base64
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.collections.HashMap

/**
 * General Info
 *
 * Mail-Info per Mail basierend auf einem Template
 *
 * @property logger : log4j.properties ...
 * @constructor keine Parameter
 *
 * @version 1.0.0
 *
 * @author bmoeller
 */
class GeneralInfo {

    val logger: Logger = Logger.getLogger(GeneralInfo::class.java.getName())

    /**
     * Methode zum Versand einer E-Mail. <br>
     *
     * Aufruf :
     * java -cp ...
     *
     * @param [product]	    Name des Produkts, das im Betreff erscheinen wird, z.B.: ms-cuba <br>
     * @param [version] 	Die auf Major.Minor.Patch reduzierte Version des Produkts <br>
     * @param [deployed]    Info, ob die Software bereits auf TEST deployed worden ist
     * @param [sql]         Liegen SQL-Skripte vor ? true/false <br>
     * @param [mailaddress] Eine Komma-separierte Liste der relevanten Mailempfänger <br>
     * @param [templatefile]Das in /src/main/resources/templates abgelegt Freemarker-Templatefile <br>
     * @param [extra]       Container für einen zusätzlichen Parameter
     * @param [headline]    Die vollständige Betreffzeile
     *
     * @author Bernd Moeller
     *
     * @version 1.1
     *
     * @since 1.0
     */
     fun sendMail(product: String, version: String, deployed: String, sql: String, config: String, dependency: String, mailaddress: String, templatefile: String, extra: String, headline: String, jiraquery: String, date1: String) {
        logger.info("Send Mail ...")
        logger.info("Headline : $headline")
        //val ccmailaddress = " "
        try {
            //Mail-Host-Properties setzen
            val props = Properties()
            props["mail.smtp.host"] = "mailhost.mobilcom.de"
            props["mail.smtp.port"] = 25
            // Message definieren
            val message = MimeMessage(Session.getInstance(props, null))
            //set message headers
            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");
            // Absender
            message.setFrom(InternetAddress("bernd.moeller@md.de"))
            // Adressatenliste
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailaddress))
            //message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccmailaddress))
            // Betreff
            message.subject = headline

            // Templatefile
            val cfg = Configuration()

            //Assume that the template is available under /src/main/resources/templates
            cfg.setClassForTemplateLoading(GeneralInfo::class.java, "/templates/")
            cfg.defaultEncoding = "UTF-8"

            cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            val template = cfg.getTemplate(templatefile)

            //Mail-Parameter uebergeben
            val paramMap = HashMap<String, String>()
            paramMap["version"] = version
            paramMap["product"] = product
            paramMap["deployed"] = deployed
            paramMap["sql"] = sql
            paramMap["config"] = config
            paramMap["dependency"] = String(dependency.toByteArray(Charsets.UTF_8), Charset.forName("UTF-8"))
            paramMap["extra"] = extra
            paramMap["jiraquery"] = jiraquery
            paramMap["date1"] = date1
            val out = StringWriter()

            template.process(paramMap, out)
            println(out.toString())
            var body = MimeBodyPart()
            body.setContent(out.toString(), "text/html")
            val multipart = MimeMultipart()
            multipart.addBodyPart(body)
            message.setContent(multipart)

            // und raus ...
            Transport.send(message)
            logger.info("Sent ...")

        }catch (e : Exception) {
            println(e.toString())
        }
     }

    /**
     * Methode zur Aktualisierung der Proxy-Liste (Version : Datum)
     *
     * @author Bernd Möller
     */
    fun writeProxy(proxy: String , version: String , date: String , filename: String){
        logger.info("writeProxyVersion (${proxy} , ${version}) ...")

        var updated = false
        try {
            val originalFile = File(filename)
            val br = BufferedReader(FileReader(originalFile))

            // Construct the new file that will later be renamed to the original
            // filename.
            val tempFile = File("tempfile.txt")
            val pw = PrintWriter(FileWriter(tempFile))
            var line: String? = null
            // Read from the original file and write to the new
            // unless content matches data to be removed.
            while (br.readLine().also({ line = it }) != null) {

                // System.out.println(line);
                if (line!!.contains(proxy)) {
                    line = "$proxy : $version : $date"
                    updated = true
                }
                pw.println(line)
                pw.flush()
            }
            pw.close()
            br.close()

            // Delete the original file
            if (!originalFile.delete()) {
                println("Could not delete file")
                return
            }

            // Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(originalFile)) println("Could not rename file")
        } catch (e: java.lang.Exception) {
            println("Dateifehler !")
            println(e.toString())
        }
        if (updated) {
            println("Updated !")
        } else {
            println("Not Updated !")
        }
    }



    /**
     * Methode zur Ermittlung der Proxy- und Microservice-Versionen
     *
     * @author Bernd Moeller
     *
     * @param [stage] : Die gewünschte stage (git, test, prod, ...)
     * @param [label] : Der "normale" Name
     * @param [proxy] : Der "Pfad" zum Endpoint "/version"
     *
     * @return Eine zusammengesetzte Zeichenkette mit P(roxy) und M(icroservice)
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getProxyVersion(stage : String, label : String, proxy : String , microservice : String, proxyservice : String) : String {

        logger.info("getProxyVersion (${proxy}) ...")
        // Client
        val client = Client.create()
        // Connect
        val webResource = client.resource("http://apigateway-${stage}.mobilcom.de/${proxy}/version")
        // val response = webResource.type("application/json").accept("application/json").get(ClientResponse::class.java)
        val response = webResource.accept("application/json").get(ClientResponse::class.java)
        //val responsestring = webResource.type("application/json").accept("application/json").get(String::class.java)
        //logger.info("Responsestring : ${responsestring.toString()}")
        logger.debug("Response : $response")
        logger.info("Status : ${response.status}")
        val responsestate = response.status

        if ( responsestate == 200) {
            val r = response.getEntity(String::class.java)
            logger.info("Response (r) : $r")
            // ResultString to JSON
            val parser = JSONParser()
            val json = parser.parse(r) as JSONObject
            logger.info("JSON : $json")
            // get fields
            val ms = json[microservice]
            val msp = json[proxyservice]
            // log fields
            logger.info("$label :: Proxy : $msp :: Microservice : $ms")
            return "P : $msp , M: $ms"
        } else {
            return "Fehler : $responsestate"
        }
    }

    /**
     * Methode zur Ermittlung der TestCoverage einer Komponente via Sonar
     *
     * @author Bernd Moeller
     *
     * @param [sonarURL] : Die Sonar-Basis-URL
     * @param [sonarCompA] : Die Applikation (so wie in Sonar hinterlegt)
     * @param [sonarCompP] : Der "Pfad" zur Applikation (so wie in Sonar hinterlegt)
     * @param [metricKey] : Die abzufragende Metric (i.a. coverage)
     *
     * @return Eine Zeichenkette, die den prozentualen Wert ohne Zusätze enthält
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getSonarCoverage (sonarURL : String , sonarCompA : String , sonarCompP : String, metricKey : String) : String {

        var coverage = ""
        // Client einrichten
        val client = Client.create()
        // component
        val component : String = if (sonarCompP == " ") {
            sonarCompA
        } else {
            "${sonarCompP}:${sonarCompA}"
        }
        // URL
        val webResource = client.resource("${sonarURL}?component=${component}&metricKeys=${metricKey}")
        // Aufruf mit Ergebnis als String
        val response = webResource.type("application/json").accept("application/json").get(String::class.java)
        // Ausgabe
        logger.debug(response.toString())
        // Antwort parsen
        val parser = JSONParser()
        // zunächst als JSONObject
        val json = parser.parse(response) as JSONObject
        logger.debug("JSON : $json")
        // Suche nach component
        val comp: JSONObject?
        comp = json["component"] as JSONObject
        logger.debug("comp : $comp")
        // Measures suchen (als Array)
        val measures = comp["measures"] as JSONArray
        logger.debug(measures)
        for (m in measures) {
            val metric = m as JSONObject
            val name = metric["metric"] as String
            if (name == metricKey) {
                coverage = metric["value"] as String
                logger.info("$sonarCompA :: $metricKey :: $coverage")
            }
        }
        return(coverage)
    }

    /**
     * Methode zur Erzeugung einer neuen Versionskennung basierend auf der bisherigen unter Berücksichtigung einer zu übergebenden SemanticVersion
     *
     * @author Bernd Moeller
     *
     * @param [version] : Die zu erhöhende Version
     * @param [semVer] : Die SemanticVersion, die die Erhöhung steuert
     *
     * @return Eine Zeichenkette, die die neue Versionskennung enthält
     *
     * @version 1.1
     *
     * @since 1.1
     */
    fun createNewVersion(version: String , semVer : String) : String{
        var patch : Int
        logger.info("Create new '${semVer}' based on $version")

        val x = version.split(".")
        if (x[2].endsWith("-SNAPSHOT")) {
            val p = x[2].split("-")
            patch = p[0].toInt()
        } else {
            patch = x[2].toInt()
        }
        var minor : Int = x[1].toInt()
        var major : Int = x[0].toInt()
        when (semVer) {
            "Patch" -> patch += 1
            "Minor" -> {
                minor += 1; patch = 0}
            "Major" -> {
                major += 1; minor = 0 ; patch = 0 }
        }
        return ("${major}.${minor}.${patch}")

    }

    /**
     * Methode zur Erzeugung einer neuen Versionskennung basierend auf der bisherigen unter Berücksichtigung einer zu übergebenden SemanticVersion
     *
     * @author Bernd Moeller
     *
     * @param [version] : Die zu erhöhende Version als 4-stellige Version
     * @param [semVer] : Die SemanticVersion, die die Erhöhung steuert
     *
     * @return Eine Zeichenkette, die die neue Versionskennung enthält
     *
     * @version 1.1
     *
     * @since 1.1
     */
    fun createNewMCBSComponentVersion(version: String , semVer : String) : String{
        logger.info("Create new '${semVer}' based on $version")

        val x = version.split(".")
        logger.info("Versionselemente : ${x.size}")

        var patch: Int = 0
        var minor: Int = 0
        var major: Int = 0
        if (x.size == 4) {
            if (x[3].endsWith("-SNAPSHOT")) {
                val p = x[3].split("-")
                patch = p[0].toInt()
            } else {
                patch = x[3].toInt()
            }
            minor = x[2].toInt()
            major = x[1].toInt()
        } else {
            if (x[2].endsWith("-SNAPSHOT")) {
                val p = x[2].split("-")
                patch = p[0].toInt()
            } else {
                patch = x[2].toInt()
            }
            minor = x[1].toInt()
            major = x[0].toInt()
        }

        // val comp : String = x[0]
        when (semVer) {
            "Patch" -> patch += 1
            "Minor" -> {
                minor += 1; patch = 0}
            "Major" -> {
                major += 1; minor = 0 ; patch = 0 }
        }
        return ("${major}.${minor}.${patch}")

    }

    /**
     * Methode zur Ermittlung der Details (PullRequests)  eines Tickets
     *
     * @author Bernd Moeller
     *
     * @param [applicationType] : Die abzufragende Applikation (stash , bamboo)
     * @param [dataType] : Die abzufragende Datzenstruktur (pullrequest, repository, build, deployment-environments)
     * @param [issueId] : Die Id des Tickets
          *
     * @return Eine Zeichenkette
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getIssuePullRequests (issueId : String )  {
        logger.info("===============")
        logger.info("PullRequests : ")
        logger.info("===============")
        // Authentifikation
        //val auth = String(Base64.encode("bmoeller:freYa_1402"))
        //val auth = String(Base64.encode("mcbstest:qs_mcbs_11"))
        val auth: String = Base64.getEncoder().encodeToString("bmoeller:freYa_1402".toByteArray())

        val c = HttpClient.newBuilder().build()



        //GET /rest/dev-status/1.0/issue/detail?issueId=<ISSUEID>&applicationType=bitbucket&dataType=pullrequest
        // Authentifikation
        //val auth = String(com.sun.jersey.core.util.Base64.encode("bmoeller:freYa_1402"))
        println(auth)
        // Client einrichten
        val client = Client.create()
        val webResource = client.resource("https://jira.freenet-group.de/rest/dev-status/1.0/issue/detail?issueId=${issueId}&applicationType=stash&dataType=pullrequest")
        // Aufruf
        val r = webResource.header("Authorization", "Basic $auth").type("application/json").accept("application/json").get(String::class.java)
        logger.debug(r.toString())




    /*    val request = HttpRequest.newBuilder()
                .uri(URI.create("https://jira.freenet-group.de/rest/dev-status/latest/issue/detail?issueId=${issueId}&applicationType=stash&dataType=pullrequest"))
                .header("Authorization","Basic  $auth")
                .build()
        val r = c.send(request, HttpResponse.BodyHandlers.ofString())
        logger.debug(r.body())

        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONObject = parser.parse(r.body()) as JSONObject
        logger.debug(json)

        // Suche nach detail
        val detailarray: JSONArray?
        detailarray = json["detail"] as JSONArray

        logger.debug("detailarray : $detailarray Size ${detailarray.size}")

        val detailobj = detailarray[0] as JSONObject

        logger.debug("detailobj : ${detailobj.keys}")


        val pr = detailobj["pullRequests"] as JSONArray
        for (pullrequest in pr) {

            val probj = pullrequest as JSONObject

            val srcobj = probj["source"] as JSONObject
            val srcrepository = srcobj["repository"] as JSONObject
            val srcreponame = srcrepository["name"]
            val srcbranch = srcobj["branch"]
            val destobj = probj["destination"] as JSONObject
            val destrepository = destobj["repository"] as JSONObject
            val destreponame = destrepository["name"]
            val destbranch = destobj["branch"]
            logger.info("PullRequest : "  + probj["name"] + " von : " + srcreponame +" :: " + srcbranch + " nach : " + destreponame + " :: " + destbranch)

        }
*/
    }

    /**
     * Methode zur Ermittlung der Details (Builds)  eines Tickets
     *
     * @author Bernd Moeller
     *
     * @param [applicationType] : Die abzufragende Applikation (stash , bamboo)
     * @param [dataType] : Die abzufragende Datzenstruktur (pullrequest, repository, build, deployment-environments)
     * @param [issueId] : Die Id des Tickets
     *
     * @return Eine Zeichenkette
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getIssueBuilds (issueId : String) {

        logger.info("=========")
        logger.info("Builds : ")
        logger.info("=========")
        // Authentifikation
        //val auth = String(Base64.encode("bmoeller:freYa_1402"))

        val auth: String = Base64.getEncoder().encodeToString("bmoeller:freYa_1402".toByteArray())

        val c = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://jira.freenet-group.de/rest/dev-status/latest/issue/detail?issueId=${issueId}&applicationType=bamboo&dataType=build"))
                .header("Authorization","Basic  $auth")
                .build()
        val r = c.send(request, HttpResponse.BodyHandlers.ofString())
        logger.debug(r.body())

        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONObject = parser.parse(r.body()) as JSONObject
        logger.debug(json)

        // Suche nach detail
        val detailarray: JSONArray?
        detailarray = json["detail"] as JSONArray

        logger.debug("detailarray : $detailarray Size ${detailarray.size}")
        val d=detailarray[0] as JSONObject
        val detailprojects = d["projects"] as JSONArray
        logger.debug(d.toString())
        logger.debug(d.size)

        for (p in detailprojects) {

            val prj = p as JSONObject
            logger.info("Projekt : " + prj["key"])
            logger.info("---------------")
            val plansarray = prj["plans"] as JSONArray
            for (plan in plansarray) {
                val buildplan = plan as JSONObject

                val build = buildplan["build"] as JSONObject
                logger.info("Build : " + buildplan["name"] + " #"+ build["buildNumber"]+ " : "+ build["buildState"])

            }

        }

    }

    /**
     * Methode zur Ermittlung der Details (Deployments and Environments)  eines Tickets
     *
     * @author Bernd Moeller
     *
     * @param [applicationType] : Die abzufragende Applikation (stash , bamboo)
     * @param [dataType] : Die abzufragende Datzenstruktur (pullrequest, repository, build, deployment-environments)
     * @param [issueId] : Die Id des Tickets
     *
     * @return Eine Zeichenkette
     *
     * @version 1.0
     *
     * @since 1.0
     */
    fun getIssueDeployments (issueId : String ) {
        logger.info("==============")
        logger.info("Deployments : ")
        logger.info("==============")
        // Authentifikation
        //val auth = String(Base64.encode("bmoeller:lumiX-03"))
        val auth: String = Base64.getEncoder().encodeToString("bmoeller:freYa_1402".toByteArray())

        val c = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://jira.freenet-group.de/rest/dev-status/latest/issue/detail?issueId=${issueId}&applicationType=bamboo&dataType=deployment-environment"))
                .header("Authorization","Basic  $auth")
                .build()
        val r = c.send(request, HttpResponse.BodyHandlers.ofString())
        logger.debug(r.body())

        // ResultString to JSON
        val parser = JSONParser()
        val json: JSONObject = parser.parse(r.body()) as JSONObject
        logger.debug(json)

        // Suche nach detail
        val detailarray: JSONArray?
        detailarray = json["detail"] as JSONArray

        logger.debug("detailarray : $detailarray Size ${detailarray.size}")
        val d=detailarray[0] as JSONObject
        val detailprojects = d["deploymentProjects"] as JSONArray
        logger.debug(d.toString())
        logger.debug(d.size)

        for (p in detailprojects) {

            val prj = p as JSONObject
            var stages = " "


            val envarray = prj["environments"] as JSONArray
            for (env in envarray) {
                val e = env as JSONObject
                //logger.info("Env : " + e["name"])
                stages += " , ${e["name"]}"
            }
            logger.info("Projekt : " + prj["name"] + "Version : " + prj["lastMainlineVersion"] + " auf : " + stages)

        }

    }


}




