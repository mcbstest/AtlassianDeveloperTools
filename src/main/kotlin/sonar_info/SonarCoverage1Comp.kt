package sonar_info

import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64
import org.apache.log4j.Logger

import general_info.GeneralInfo
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.InputStream
import kotlin.math.roundToInt

/**
 * Methode zur Abfrage der TestCoverage einer Sonar-Komponente (sonar_components.csv)
 * - die Coverage wird aus Sonar ausgelesen
 * - die Steuerdatei (Sonar-Pfad ...) befindet sich als csv-File im Verzeichnis jiraResources
 * - es wird eine property-Datei mit "coverage=xy%" erzeugt  >> coverage.properties
 * - das Property sollte im Nachgang in eine geeignete Confluence-Seite integriert werden
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. sonar_info.SonarCoverage1CompKt ${bamboo.sonarURL} ms-cuba-gateway
 *
 * @param [sonarURL] die URL fuer produktiven Sonar-Zugriff
 * @param [application] der Name der Applikation, so , wie er in Sonar hinterlegt ist
 *
 * @author bmoeller
 *
 * @see GeneralInfo
 */
fun main(args : Array<String>) {
    // logging
    val logger = Logger.getLogger(GeneralInfo::class.java.getName())

    // params
    var sonarURL : String
    var metrickey : String
    var sonarCompP : String
    var sonarCompA : String
    var sonarComp : String
    //
    // parameter
    logger.debug(args.toString())
    sonarURL = args[0]
    logger.info("Sonar : ${sonarURL}")
    sonarComp = args[1]
    logger.info("Komponente : ${sonarComp}")
    
    logger.info("Sonar ... ")
    // sonar / general_info
    val g = GeneralInfo()
    // output-file
    val file = File("coverage.properties")
    // input-file
    val inputStream: InputStream = File("./jiraResources/sonar_components.csv").inputStream()

    // Zeilen lesen und verarbeiten
    inputStream.bufferedReader().useLines { lines ->
        lines.forEach {line : String ->
            logger.info(line)
            val compList = line.split(";".toRegex()).dropLastWhile({ line.isEmpty() }).toTypedArray()
            // Teile isolieren
            metrickey = compList[0]
            sonarCompP = compList[1]
            sonarCompA = compList[2]

            if (sonarComp == sonarCompA) {

                val coverage : Float =  g.getSonarCoverage(sonarURL,sonarCompA,sonarCompP,metrickey).toFloat()
                val i : Int = coverage.roundToInt()
                logger.info("Coverage : ${i}")
                file.appendText("coverage=${i}")
            }
        }
    }
}