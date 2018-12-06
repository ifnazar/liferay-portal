<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ page import="com.liferay.portal.kernel.json.JSONFactory" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.builder.CustomDTO" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<%@ page import="java.util.Calendar" %><%@
page import="java.util.List" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
JSONFactory jsonFactory = (JSONFactory)request.getAttribute("jsonFactory");
CustomFilterPortletPreferences customFilterPortletPreferences = new CustomFilterPortletPreferencesImpl(java.util.Optional.of(portletPreferences), jsonFactory);
String initialFieldType = customFilterPortletPreferences.getFilterType();
Calendar today = Calendar.getInstance();
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset-group>
			<aui:input helpMessage="filter-field-help" label="filter-field" name="<%= PortletPreferencesJspUtil.getInputName(CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_FIELD) %>" value="<%= customFilterPortletPreferences.getFilterField() %>" />

			<aui:input helpMessage="filter-value-help" label="filter-value" name="<%= PortletPreferencesJspUtil.getInputName(CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_VALUE) %>" value="<%= customFilterPortletPreferences.getFilterValue() %>" />

			<aui:select label="Combo" name="<%= PortletPreferencesJspUtil.getInputName(CustomFilterPortletPreferences.PREFERENCE_KEY_SELECTED_VALUE) %>" onChange="entityChanged(this);">

				<%
				List<CustomDTO> lstCustomDTO = customFilterPortletPreferences.getComboValues();

				for (CustomDTO value : lstCustomDTO) {
					%>

						<aui:option data-field="<%= value.getField() %>" data-type="<%= value.getType() %>" label="<%= value.getField() %>" selected="<%= customFilterPortletPreferences.isDisplay(value.getField()) %>" />

					<%
				}
				%>

			</aui:select>

			<div class="checkbox_filterable_string">
				<aui:input helpMessage="filter-value-help" label="filter-value" name="xx1" value="<%= customFilterPortletPreferences.getFilterValue() %>" />
			</div>

			<div class="checkbox_filterable_date hide">
				<aui:field-wrapper helpMessage="filter-value-help" label="filter-value">
					<liferay-ui:input-date
						dayParam="dobDay"
						dayValue="<%= today.get(Calendar.DAY_OF_MONTH) %>"
						monthParam="dobMonth"
						monthValue="<%= today.get(Calendar.MONTH) %>"
						name="<%= PortletPreferencesJspUtil.getInputName(CustomFilterPortletPreferences.PREFERENCE_KEY_DATE_VALUE) %>"
						yearParam="dobYear"
						yearValue="<%= today.get(Calendar.YEAR) %>"
					/>
				</aui:field-wrapper>
			</div>
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	var checkboxFilterableString = $('.checkbox_filterable_string');
	var checkboxFilterableDate = $('.checkbox_filterable_date');
	var hidden = 'hide';

	function changeFieldType(type) {
		checkboxFilterableString.addClass(hidden);
		checkboxFilterableDate.addClass(hidden);

		if (type === 'keyword') {
			checkboxFilterableString.removeClass(hidden);
		}

		if (type === 'date') {
			checkboxFilterableDate.removeClass(hidden);
		}
	}

	function entityChanged(select) {
		var selectedOption = $(select).find(':selected');
		var field = selectedOption.data('field');
		var type = selectedOption.data('type');
		changeFieldType(type);
	}

	function main() {
		changeFieldType('<%= initialFieldType %>');
	}

	main();
</aui:script>