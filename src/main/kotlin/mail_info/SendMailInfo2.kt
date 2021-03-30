package mail_info

import general_info.GeneralInfo
import org.apache.log4j.Logger
import java.io.File
import kotlin.collections.ArrayList

/**
 *
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. mail_info.SendMailInfo2Kt MCBS 1.2.3 "undefined" false false false "mcbs_64"  "EPIC-123" "issuelist.csv" bernd.moeller@md.de B_MS.ftl
 *
 * @param [product] das Produkt
 * @param [version] die zugehörige Version
 * @param [ibn] A Date
 * @param [sql] Info , ob SQL-Files existieren (true/false)
 * @param [config] Configuration ? (true | false)
 * @param [dependency] Dependencies ? (true | false  ::  Bla)
 * @param [extra] zusätzlich ausgebbarer Text
 * @param [epic] The epic, this release belongs to
 * @param [issues] List of release-issues
 * @param [mailaddress] die Zieladresse
 * @param [templatefile] das freemarker-template-file
 * @author bmoeller
 *
 * @see GeneralInfo
*/

fun main(args : Array<String>) {
    // Jira-Query
    val jiraquery : String
    // logging
    val logger = Logger.getLogger(GeneralInfo::class.java.name)
    // Auswertung der Parameter
    val product : String = args[0]
    logger.info("Produkt : $product")
    val version : String = args[1]
    logger.info("Version : $version")
    val ibn : String = args[2]
    logger.info("IBN : $ibn")
    val sql : String = args[3]
    logger.info("SQL : $sql")
    val config : String = args[4]
    logger.info("Config : $config")
    val dependency: String = args[5]
    logger.info("Dependency : $dependency")
    val extra : String = args[6]
    logger.info("Extra : $extra")
    val epic : String = args[7]
    logger.info("EPIC : $epic")
    val issues : String = args[8]
    logger.info("Issues : $issues")
    val mailaddress : String = args[9]
    logger.info("Mailaddress : $mailaddress")
    val templatefile : String = args[10]
    logger.info("Templatefile : $templatefile")


    // jiraquery = "https://jira.freenet-group.de/issues/?jql=project%20in%20(abrms%2C%20MCBS)%20AND%20Epos-Verkn%C3%BCpfung%3D${epic}%20AND%20fixVersion%20in%20(${version})"
    jiraquery = "https://jira.freenet-group.de/issues/?jql=project%20in%20(abrms%2C%20MCBS)%20AND issue in LinkedIssues (\"${epic}\", \"ist abh%C3%A4ngig von\")%20AND%20fixVersion%20in%20(${version})"

    var issuelist = "<ul>"
    // read issues from file and add to ArrayList
    val issueList = ArrayList<String>()
    var issuecontent : List<String>
    File(issues).forEachLine {
        logger.debug("Zeile : $it")
        issuecontent= it.split(';')
        logger.info(issuecontent[0])
        logger.info(issuecontent[1])
        issuelist=issuelist + "<li>"+issuecontent[0]+" :: " + issuecontent[1] + "</li>"
    }
    issuelist=issuelist + "</ul>"

    val g = GeneralInfo()

    //val issuelist = "<ul><li>MCBS-3408 :: keine Einträge </li><li>MCBS-3409 :: auch keine Einträge </li></ul>"
    g.sendMail2(product, version, ibn, sql, config, dependency, issuelist, extra , jiraquery , mailaddress, templatefile )

}




