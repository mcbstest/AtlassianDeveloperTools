<html>

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
	gebaut.
<br><br>
<b>
	Enthaltene Aenderungen / Release-Notes :
</b>
<br><br>
	SQL :
<br>
	${sql}
<br><br>
	MCBS :
<br>
	<a href="https://kiwi.freenet-group.de/display/AERP/${extra} ">"https://kiwi.freenet-group.de/display/AERP/${extra} "</a>
<br><br>
	Jira-Uebersicht zu (exakt) dieser Iteration :
<br>
	${jiraquery}

<br><br>
	Viele Gruesse
<br>
	Releasebuild
</body>

</body>
</html>

