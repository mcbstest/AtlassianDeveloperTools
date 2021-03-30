package mail_info

import general_info.GeneralInfo
import org.apache.log4j.Logger

/**
 *
 *
 * Aufruf :
 * java -cp ./build/libs/AtlassianDeveloperTools-all-1.0.jar:. mail_info.SendMailInfoKt MCBS 1.2.3 true false false false bernd.moeller@md.de "Extra-Text " "Neue Version " " gebaut" "EPIC-123"
 *
 * @param [product] das Produkt
 * @param [version] die zugehörige Version
 * @param [deployed] Info, ob bereits ein Deployment auf TEST erfolgt ist  (true | false)
 * @param [sql] Info , ob SQL-Files existieren (true/false)
 * @param [config] Configuration ? (true | false)
 * @param [dependency] Dependencies ? (true | false  ::  Bla)
 * @param [mailaddress] die Zieladresse
 * @param [templatefile] das freemarker-template-file
 * @param [extra] zusätzlich ausgebbarer Text
 * @param [pre] Mail-Header (part1)
 * @param [post] Mail-Header (part2)
 * @param [epic] The epic, this release belongs to
 * @param [date1] A Date
 *
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
    val deployed : String = args[2]
    logger.info("Deployed : $deployed")
    val sql : String = args[3]
    logger.info("SQL : $sql")
    val config : String = args[4]
    logger.info("Config : $config")
    val dependency: String = args[5]
    logger.info("Dependency : $dependency")
    val mailaddress : String = args[6]
    logger.info("Mailaddress : $mailaddress")
    val templatefile : String = args[7]
    logger.info("Templatefile : $templatefile")
    val extra : String = args[8]
    logger.info("Extra : $extra")
    val pre : String = args[9]
    logger.info("Betreff (Pre) : $pre")
    val post : String = args[10]
    logger.info("Betreff (Post) : $post")
    val epic : String = args[11]
    logger.info("EPIC : $epic")
    val date1 : String = args[12]
    logger.info("Datum : $date1")

    logger.info("Headline : $pre ${product}_${version} $post")

    // jiraquery = "https://jira.freenet-group.de/issues/?jql=project%20in%20(abrms%2C%20MCBS)%20AND%20Epos-Verkn%C3%BCpfung%3D${epic}%20AND%20fixVersion%20in%20(${version})"
    jiraquery = "https://jira.freenet-group.de/issues/?jql=project%20in%20(abrms%2C%20MCBS)%20AND issue in LinkedIssues (\"${epic}\", \"ist abh%C3%A4ngig von\")%20AND%20fixVersion%20in%20(${version})"

    val g = GeneralInfo()
    //g.sendMail(product, version, deployed, sql, config, mailaddress, templatefile, extra, "${pre} ${product}_${version} ${post}", jiraquery)
    //g.sendMail("es-plugin-collection", "1.2", "false", "false", "false", "bernd.moeller@md.de", "BuildInfoESPluginTemplate.ftl", "6.8.1", "${product}-${version}-${extra}", " ")
    //val tabelle = "<ul><li>MCBS-3408 :: keine Einträge </li><li>MCBS-3409 :: auch keine Einträge </li></ul>"
    g.sendMail(product, version, deployed, sql, config, dependency, mailaddress, templatefile, extra, "$pre $product ${version}${post}", jiraquery , date1 )

}




