package sonar_info

import general_info.GeneralInfo
import org.apache.log4j.Logger
import java.io.FileWriter
import java.util.*
import kotlin.math.roundToInt

/**
 * Methode zur Abfrage der TestCoverage einer Sonar-Komponente
 * - die Coverage wird aus Sonar ausgelesen
 * - es wird eine property-Datei mit "coverage=xy%" erzeugt  >> coverage.properties
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-X.Y.Z.jar:. sonar_info.getComponentsCoverageKt ${bamboo.sonarURL} de.md.mcbs.ms ms-oidc
 *
 * @param [sonarURL] die URL fuer produktiven Sonar-Zugriff
 * @param [pfad] der Pfad der Komponente , so wie in SOnar hinterlegt
 * @param [application] der Name der Applikation, so , wie er in Sonar hinterlegt ist
 *
 * @author bmoeller
 *
 * @see GeneralInfo
 */
fun main(args : Array<String>) {
    // logging
    val logger = Logger.getLogger(GeneralInfo::class.java.name)

    // params
    val metrickey = "coverage"
    // parameter
    logger.debug(args.toString())
    val sonarURL : String = args[0]
    logger.info("Sonar : $sonarURL")
    val sonarCompP : String = args[1]
    logger.info("Pfad : $sonarCompP")
    val sonarCompA : String = args[2]
    logger.info("Applikation : $sonarCompA")

    val properties = Properties()
    logger.info("Sonar ... ")
    // sonar / general_info
    val g = GeneralInfo()

    val coverage : Float =  g.getSonarCoverage(sonarURL,sonarCompA,sonarCompP,metrickey).toFloat()
    val i : Int = coverage.roundToInt()
    logger.info("Coverage : $i")
    // Schreiben
    // Property-File
    val propertiesFile = "coverage.properties"
    val fileWriter = FileWriter(propertiesFile)
    properties["coverage"] = "$i"
    properties.store(fileWriter, "Coverage ...")
}