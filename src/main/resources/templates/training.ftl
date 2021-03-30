<html>

<div style="font-size:30;color:white;background-color:green">
	<b>Release-Build und Freigabe : ${product}_${version} </b>
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
<b>DB-Changes :  </b> <#if sql == "true" > JA <#else> NEIN </#if>
<br><br>
<b>
	Enthaltene Aenderungen:
</b>
<br><br>
	Microservices :
<br>
	<a href="https://kiwi.freenet-group.de/display/AERP/${product}+${version}">"https://kiwi.freenet-group.de/display/AERP/${product}+${version}"</a>
<br><br>
	Viele Gruessee
<br>
	Releasebuild

Zeile
<#if tabelle != "none" >

${tabelle}

<#else>

NEIN

</#if>

</body>

</body>
</html>

