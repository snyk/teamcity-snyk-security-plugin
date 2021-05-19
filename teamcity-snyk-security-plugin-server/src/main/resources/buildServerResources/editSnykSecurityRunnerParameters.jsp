<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="io.snyk.plugins.teamcity.common.SnykSecurityRunnerConstants"/>
<jsp:useBean id="runners" class="io.snyk.plugins.teamcity.common.runner.Runners"/>

<l:settingsGroup title="Snyk Settings">
  <tr>
    <th><label for="${constants.severityThreshold}.select">Severity threshold:</label></th>
    <td>
      <props:selectProperty name="${constants.severityThreshold}" className="mediumField" id="${constants.severityThreshold}.select">
        <props:option value="low">low</props:option>
        <props:option value="medium">medium</props:option>
        <props:option value="high">high</props:option>
        <props:option value="critical">critical</props:option>
      </props:selectProperty>
      <span class="smallNote">Only report vulnerabilities of provided level or higher (low/medium/high/critical).</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Fail on issues:</label></th>
    <td>
      <c:choose>
        <c:when test="${propertiesBean.properties.containsKey(constants.failOnIssues)}">
          <props:checkboxProperty name="${constants.failOnIssues}" uncheckedValue="false"/>
        </c:when>
        <c:otherwise>
          <props:checkboxProperty name="${constants.failOnIssues}" uncheckedValue="false" checked="true"/>
        </c:otherwise>
      </c:choose>
      <span class="smallNote">Fail the build when issues are found.</span>
    </td>
  </tr>
  <tr>
    <th><label>Monitor project on build:</label></th>
    <td>
      <props:checkboxProperty name="${constants.monitorProjectOnBuild}"/>
      <span class="smallNote">Take a current application dependencies snapshot for continuous monitoring by Snyk.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label for="${constants.file}.text">File:</label></th>
    <td>
      <props:textProperty name="${constants.file}" className="longField" id="${constants.file}.text">
        <jsp:attribute name="afterTextField"><bs:vcsTree fieldId="${constants.file}.text"/></jsp:attribute>
      </props:textProperty>
      <span class="smallNote">The path to the manifest file to be used by Snyk.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label for="${constants.organisation}.text">Organisation:</label></th>
    <td>
      <props:textProperty name="${constants.organisation}" className="longField" id="${constants.organisation}.text"/>
      <span class="smallNote">The Snyk organisation in which this project should be tested and monitored.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label for="${constants.projectName}.text">Project name:</label></th>
    <td>
      <props:textProperty name="${constants.projectName}" className="longField" id="${constants.projectName}.text"/>
      <span class="smallNote">A custom name for the Snyk project created for this TeamCity project on every build.</span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label for="${constants.additionalParameters}.text">Additional parameters:</label></th>
    <td>
      <props:textProperty name="${constants.additionalParameters}" className="longField" expandable="true" id="${constants.additionalParameters}.text"/>
      <span class="smallNote">Refer to the <a href="https://snyk.io/docs/using-snyk">Snyk CLI help page</a> for information on additional arguments.</span>
    </td>
  </tr>
</l:settingsGroup>

<l:settingsGroup title="Snyk Tool Settings">
  <tr>
    <th><label>Snyk API token:</label><l:star/></th>
    <td>
      <props:passwordProperty name="${constants.apiToken}" className="longField"/>
      <span class="smallNote">The API token to be used to authenticate to Snyk.</span>
      <span class="error" id="error_${constants.apiToken}"></span>
    </td>
  </tr>
  <tr>
    <th><label for="${constants.version}.select">Snyk version:</label><l:star/></th>
    <td>
      <props:selectProperty name="${constants.version}" className="mediumField" enableFilter="true" id="${constants.version}.select">
        <c:forEach items="${runners.versions}" var="snykVersion">
          <props:option value="${snykVersion}">${snykVersion}</props:option>
        </c:forEach>
      </props:selectProperty>
      <span class="error" id="error_${constants.version}"></span>
    </td>
  </tr>
  <tr class="advancedSetting">
    <th><label>Use custom build tool path:</label></th>
    <td>
      <props:checkboxProperty name="${constants.useCustomBuildToolPath}"/>
      <span class="smallNote">If this checkbox is selected, auto-discovery mode will be deactivated and the path to the project's build tool must be specified.</span>
    </td>
  </tr>
  <tr class="advancedSetting" id="${constants.customBuildToolPath}.tr">
    <th><label for="${constants.customBuildToolPath}.text">Custom build tool path:</label></th>
    <td>
      <props:textProperty name="${constants.customBuildToolPath}" className="longField" expandable="true" id="${constants.customBuildToolPath}.text"/>
      <span class="smallNote">Specify here the path to the build tool used for the project (i.e. <kbd>mvn</kbd> or <kbd>gradle</kbd>).</span>
      <span class="smallNote">During builds this directory will be added to environment <kbd>PATH</kbd>.</span>
      <span class="error" id="error_${constants.customBuildToolPath}"></span>
    </td>
  </tr>
</l:settingsGroup>

<script type="text/javascript">
  var updateCustomBuildToolPathVisibility = function () {
    var useCustomBuildToolPath = $('${constants.useCustomBuildToolPath}').checked;
    var customBuildToolPath = $('${constants.customBuildToolPath}.text');
    if (useCustomBuildToolPath) {
      BS.Util.show('${constants.customBuildToolPath}.tr');
      customBuildToolPath.disabled = false;
    } else {
      BS.Util.hide('${constants.customBuildToolPath}.tr');
      customBuildToolPath.disabled = true;
      customBuildToolPath.value = "";
    }
    BS.VisibilityHandlers.updateVisibility(customBuildToolPath);
  };

  $j(BS.Util.escapeId("${constants.useCustomBuildToolPath}")).click(updateCustomBuildToolPathVisibility);

  updateCustomBuildToolPathVisibility();
</script>
