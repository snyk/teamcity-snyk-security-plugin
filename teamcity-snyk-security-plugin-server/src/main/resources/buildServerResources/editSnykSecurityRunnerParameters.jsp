<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="runners" class="io.snyk.plugins.teamcity.common.runner.Runners"/>

<l:settingsGroup title="Snyk Settings">
  <tr>
    <th><label for="snyk.severityThreshold.select">Severity threshold:</label></th>
    <td>
      <props:selectProperty name="snyk.severityThreshold" className="mediumField" id="snyk.severityThreshold.select">
        <props:option value="low">low</props:option>
        <props:option value="medium">medium</props:option>
        <props:option value="high">high</props:option>
      </props:selectProperty>
      <span class="smallNote">Only report vulnerabilities of provided level or higher (low/medium/high).</span>
    </td>
  </tr>
  <tr>
    <th><label>Monitor project on build:</label></th>
    <td>
      <props:checkboxProperty name="snyk.monitorProjectOnBuild"/>
      <span class="smallNote">Take a current application dependencies snapshot for continuous monitoring by Snyk.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>File:</label></th>
    <td>
      <props:textProperty name="snyk.file" className="longField"/>
      <span class="smallNote">The path to the manifest file to be used by Snyk.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Organisation:</label></th>
    <td>
      <props:textProperty name="snyk.organisation" className="longField"/>
      <span class="smallNote">The Snyk organisation in which this project should be tested and monitored.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Project name:</label></th>
    <td>
      <props:textProperty name="snyk.projectName" className="longField"/>
      <span class="smallNote">A custom name for the Snyk project created for this TeamCity project on every build.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Additional parameters:</label></th>
    <td>
      <props:textProperty name="snyk.additionalParameters" className="longField" expandable="true"/>
      <span class="smallNote">Refer to the <a href="https://snyk.io/docs/using-snyk">Snyk CLI help page</a> for information on additional arguments.</span>
    </td>
  </tr>
</l:settingsGroup>

<l:settingsGroup title="Snyk Tool Settings">
  <tr>
    <th><label>Snyk API token:</label><l:star/></th>
    <td>
      <props:passwordProperty name="secure:snyk.apiToken" className="longField"/>
      <span class="smallNote">The API token to be used to authenticate to Snyk.</span>
      <span class="error" id="error_secure:snyk.apiToken"></span>
    </td>
  </tr>
  <tr>
    <th><label for="snyk.version.select">Snyk version:</label><l:star/></th>
    <td>
      <props:selectProperty name="snyk.version" className="mediumField" enableFilter="true" id="snyk.version.select">
        <c:forEach items="${runners.versions}" var="snykVersion">
          <props:option value="${snykVersion}">${snykVersion}</props:option>
        </c:forEach>
      </props:selectProperty>
      <span class="error" id="error_snyk.version"></span>
    </td>
  </tr>
</l:settingsGroup>
