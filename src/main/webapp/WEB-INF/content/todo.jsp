<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
<html>
<head><title>Manage Your TODOs</title></head>
<body>

<div class="row-fluid">
    <div class="span4">

        <s:fielderror theme="bootstrap"/>

        <s:form id="todoAddForm" action="todo-save" namespace="/" theme="bootstrap" cssClass="form-inline" label="New TODO ...">
	        <s:hidden name="nodoc" value="true" />
            <div class="input-append">
                <s:textfield name="topic" label="TODO" tooltip="TODO Topic" theme="simple"/>
                <sj:submit
		                cssClass="btn btn-primary"
		                cssStyle="height: 30px"
		                value="Add"
		                formIds="todoAddForm"
		                targets="todoListContent"
		                clearForm="true"
		                validate="true"
		                validateFunction="bootstrapValidation"
		        />
            </div>
        </s:form>
    </div>
	<s:url var="listurl" action="todo-list" namespace="/" >
	   <s:param name="nodoc" value="true" />
	</s:url>
	<sj:div id="todoListContent" cssClass="span8" href="%{listurl}">
    </sj:div>
</div>

<sj:dialog id="todoDialog" modal="true" title="TODO Dialog" autoOpen="false" width="800" height="600" closeTopics="/todo/dialog/close" />
</body>
</html>