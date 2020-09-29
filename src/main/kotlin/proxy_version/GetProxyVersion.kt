package proxy_version

import general_info.GeneralInfo
import org.apache.log4j.Logger
import java.io.File
import java.io.InputStream
/**
 * Methode zur Ermittlung der Proxy- bzw. Microservice-Versionen auf den relevanten stages
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. proxy_version.GetProxyVersionKt
 *
 * @author bmoeller
 *
 * @see GeneralInfo
 */
fun main(args : Array<String>) {
    // logging
    val logger = Logger.getLogger(GeneralInfo::class.java.getName())
    // stages
    val stages = arrayListOf("dev","test","git","prod")
    var proxy_entry : String
    var proxy : String
    val g = GeneralInfo()
    // output-file
    val file = File("proxies.csv")
    file.writeText("Proxy;;Dev;Test;GIT;Prod;\n")

    // input-file
    val inputStream: InputStream = File("./jiraResources/proxylist.csv").inputStream()
    val lineList = mutableListOf<String>()
    // read lines
    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
    // iterate lines
    lineList.forEach{
        val p  = it.split(";")
        proxy_entry = "${p[0]};${p[1]};"
        stages.forEach { stage ->
            //calls
            proxy = g.getProxyVersion(stage,p[0],p[1],p[2],p[3])
            proxy_entry = proxy_entry + proxy+";"

        }
        logger.info(proxy_entry)
        file.appendText(proxy_entry+"\n")
    }

}


