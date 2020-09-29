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
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TEST-Deployment:&nbsp;</b> <#if deployed == "true" > JA <#else> NEIN </#if><br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Config-Abhaengigkeiten:&nbsp;</b> <#if config == "true" > JA <#else> NEIN </#if><br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dependencies:&nbsp;</b> ${dependency}<br>
<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DB-Changes:&nbsp;</b> <#if sql == "true" > JA <#else> NEIN </#if><br>
<br>
<b>
	Enthaltene Aenderungen:
</b>
<br><br>
	Microservices :
<br>
	<a href="https://kiwi.freenet-group.de/display/AERP/${product}+${version}">"https://kiwi.freenet-group.de/display/AERP/${product}+${version}"</a>
<br><br>
	Viele Gruesse
<br>
	Releasebuild
</body>

</body>
</html>