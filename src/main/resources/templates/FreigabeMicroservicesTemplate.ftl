<#ftl strip_whitespace=false>
<html>

<head>
    <meta charset="UTF-8" />
</head>

<div style="font-size:30;color:white;background-color:green">
	<b>Release-Build und Freigabe : ${product}_${version} <#if date1 != "undefined" > // IBN (geplant) : ${date1} <#else> </#if>  </b>
</div>

<body style="font-family:calibri">
<p>
	Hallo, 
<br><br>
	Es wurde eine neue Version von : 
<br><br> 
<b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${product}_${version} </b> 
<br><br> 
	gebaut, getestet und zur weiteren Verwendung freigegeben.
<br><br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Config-Abhaengigkeiten:&nbsp;</b> <#if config == "true" > JA <#else> NEIN </#if><br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dependencies:&nbsp;</b> ${dependency}<br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DB-Changes:&nbsp;</b> ${sql} <br>
<br>
${extra}
<b>
	Enthaltene Aenderungen:
</b>
<br>
	Microservices :
<br>
	<a href="https://kiwi.freenet-group.de/display/AERP/${product}+${version}">"https://kiwi.freenet-group.de/display/AERP/${product}+${version}"</a>
<br><br>
	Viele Gruesse
<br>
	Releasebuild
<br>


</body>

</body>
</html>