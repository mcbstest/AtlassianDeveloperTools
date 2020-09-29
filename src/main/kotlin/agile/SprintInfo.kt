package agile

import atlassian_jira.AtlassianJiraIssue
import com.sun.jersey.api.client.Client
import com.sun.jersey.core.util.Base64
import org.apache.log4j.Logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

fun main (args : Array<String>) {
    val logger = Logger.getLogger(AtlassianJiraIssue::class.java.name)
    // Get all Sprints to get the sprintId
    // Authentifizierung
    val auth = String(Base64.encode("bmoeller:freYa_1402"))
    //val auth = String(Base64.encode("mcbstest:qs_mcbs_11"))
    val client = Client.create()
    // Connect
    val webResource = client.resource("https://jira.freenet-group.de/rest/agile/1.0/board/1344/sprint?startAt=77&maxResults=50")
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
}