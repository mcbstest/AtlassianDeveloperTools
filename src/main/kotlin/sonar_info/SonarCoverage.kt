package sonar_info

import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64
import org.apache.log4j.Logger

import general_info.GeneralInfo
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Methode zur Abfrage der TestCoverage aller in einer CSV-Datei hinterlegten Sonar-Komponenten (sonar_components.csv)
 * - die Coverage wird aus Sonar ausgelesen
 * - die Steuerdatei befindet sich als csv-File im Verzeichnis jiraResources
 * - es wird eine csv-Datei mit "applikation";"coverage" erzeugt  >> coverage.csv
 * - die Datei sollte im Nachgang an eine geeignete Confluence-Seite angehängt werden
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. sonar_info.SonarCoverageKt ${bamboo.sonarURL}
 *
 * @param [sonarURL] die URL fuer produktiven Sonar-Zugriff
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
    var def_file : String
    var met_file : String
    var metrickey : String
    var sonar_component_p : String
    var sonar_component_a : String
    //
    // parameter
    logger.debug(args.toString())
    sonarURL = args[0]
    logger.info("Sonar : ${sonarURL}")

    def_file = args[1]
    logger.info("Quelle : ${def_file}")

    met_file = args[2]
    logger.info("Metriken : ${met_file}")
    // sonar / general_info
    val g = GeneralInfo()
    // output-file
    val file = File(met_file)
    file.writeText("Applikation;Testabdeckung \n")

    logger.info("Sonar : ")
    // input-file
    val inputStream: FileInputStream = File("./jiraResources/${def_file}").inputStream()
    // Zeilen lesen und ...
    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->

            // Ausgabe der Zeile
            logger.debug(line)
            // Zeile splitten
            val compList = line.split(";".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            // Teile isolieren
            metrickey = compList[0]
            sonar_component_p = compList[1]
            sonar_component_a = compList[2]

            val coverage = g.getSonarCoverage(sonarURL,sonar_component_a, sonar_component_p, metrickey)
            logger.info("Coverage : ${coverage}")
            file.appendText("${sonar_component_a};${coverage}%\n")
        }
    }

}