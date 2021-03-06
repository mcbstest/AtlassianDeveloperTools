<#ftl encoding='UTF-8'>
<html>

<head>
    <meta charset="UTF-8" />
</head>

<div style="font-size:30;color:white;background-color:green">
	<b>Release-Build : ${product}_${version} <#if date1 != "undefined" > // IBN (geplant) : ${date1} <#else> </#if></b>
</div>

<body style="font-family:calibri">
<p>
	Hallo, 
<br><br>
	Es wurde eine neue Version von : 
<br><br> 
<b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${product}_${version} </b> 
<br><br> 
	gebaut und zur weiteren Verwendung freigegeben.
<br><br><hr><br>
<b>IBN (geplant) : </b> ${date1}
<br><br><hr><br>
<b>
	Ergaenzungen :
</b>
<br><br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dependencies:&nbsp;</b> ${dependency}<br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DB-Changes:&nbsp;</b> <#if sql == "true" > ${sql} <#else> NEIN </#if> <br>
<br><hr><br>
<b>
    Release-Inhalte :
</b>
${tabelle}
<br><hr><br>
<b>
	Releasenote :
</b>
<br><br>
	<a href="https://kiwi.freenet-group.de/display/AERP/${extra} ">"https://kiwi.freenet-group.de/display/AERP/${extra} "</a>
<br><br><hr><br>
<b>
	Jira-Uebersicht zu (exakt) dieser Iteration :
</b>
<br>
	<a href="${jiraquery} ">"${jiraquery} "</a>

<br><br>
	Viele Gruesse
<br>
	Releasebuild
</body>

</body>
</html>

