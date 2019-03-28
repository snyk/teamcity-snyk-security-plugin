<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<div class="parameter">
  Severity threshold: <strong><props:displayValue name="snyk.severityThreshold" emptyValue="not specified"/></strong>
  <c:if test="${empty propertiesBean.properties['snyk.severityThreshold']}">
    <span class="smallNote">Note: running without specifying the threshold has the same effect as 'low' option, i.e. all vulnerabilities will be reported.</span>
  </c:if>
</div>

<div class="parameter">
  Monitor project on build: <strong><props:displayCheckboxValue name="snyk.monitorProjectOnBuild"/></strong>
</div>

<div class="parameter">
  File: <strong><props:displayValue name="snyk.file" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Organisation: <strong><props:displayValue name="snyk.organisation" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Project name: <strong><props:displayValue name="snyk.projectName" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Additional parameters: <strong><props:displayValue name="snyk.additionalParameters" emptyValue="none specified"/></strong>
</div>
