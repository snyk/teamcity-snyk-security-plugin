<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants"/>

<div class="parameter">
  Severity threshold: <strong><props:displayValue name="${constants.severityThreshold}" emptyValue="not specified"/></strong>
  <c:if test="${empty propertiesBean.properties[constants.severityThreshold]}">
    <span class="smallNote">Note: running without specifying the threshold has the same effect as 'low' option, i.e. all vulnerabilities will be reported.</span>
  </c:if>
</div>
<div class="parameter">
  Fail on issues: <strong><props:displayCheckboxValue name="${constants.failOnIssues}"/></strong>
</div>
<div class="parameter">
  Monitor project on build: <strong><props:displayCheckboxValue name="${constants.monitorProjectOnBuild}"/></strong>
</div>
<div class="parameter">
  File: <strong><props:displayValue name="${constants.file}" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Organisation: <strong><props:displayValue name="${constants.organisation}" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Project name: <strong><props:displayValue name="${constants.projectName}" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Additional parameters: <strong><props:displayValue name="${constants.additionalParameters}" emptyValue="none specified"/></strong>
</div>
<div class="parameter">
  Custom build tool path: <strong><props:displayValue name="${constants.customBuildToolPath}" emptyValue="not specified"/></strong>
</div>
