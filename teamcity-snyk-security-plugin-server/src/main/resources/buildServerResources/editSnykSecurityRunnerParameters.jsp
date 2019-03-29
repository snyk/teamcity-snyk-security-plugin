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
    </td>
  </tr>
  <tr>
    <th><label>Monitor project on build:</label></th>
    <td>
      <props:checkboxProperty name="snyk.monitorProjectOnBuild"/>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>File:</label></th>
    <td>
      <props:textProperty name="snyk.file" className="longField"/>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Organisation:</label></th>
    <td>
      <props:textProperty name="snyk.organisation" className="longField"/>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Project name:</label></th>
    <td>
      <props:textProperty name="snyk.projectName" className="longField"/>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Additional parameters:</label></th>
    <td>
      <props:textProperty name="snyk.additionalParameters" className="longField" expandable="true"/>
    </td>
  </tr>
</l:settingsGroup>

<l:settingsGroup title="Snyk Tool Settings">
  <tr>
    <th><label>Snyk API token:</label><l:star/></th>
    <td>
      <props:passwordProperty name="secure:snyk.apiToken" className="longField"/>
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
    </td>
  </tr>
</l:settingsGroup>
