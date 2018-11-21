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

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterTermDisplayContext" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<style>
	.facet-checkbox-label {
		display: block;
	}

	.facet-term-selected {
		font-weight: 600;
	}

	.facet-term-unselected {
		font-weight: 400;
	}
</style>

<%
CustomFilterDisplayContext customFilterDisplayContext = (CustomFilterDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>

<c:choose>
	<c:when test="<%= customFilterDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getParameterName()) %>" type="hidden" value="<%= customFilterDisplayContext.getParameterValue() %>" />
	</c:when>
	<c:otherwise>
		<liferay-ui:panel-container
			extended="<%= true %>"
			id='<%= renderResponse.getNamespace() + "facetCustomPanelContainer" %>'
			markupView="lexicon"
			persistState="<%= true %>"
		>
			<liferay-ui:panel
				collapsible="<%= true %>"
				cssClass="search-facet"
				id='<%= renderResponse.getNamespace() + "facetCustomPanel" %>'
				markupView="lexicon"
				persistState="<%= true %>"
				title="<%= customFilterDisplayContext.getDisplayCaption() %>"
			>
				<aui:form method="post" name="customFacetForm">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getParameterName()) %>" type="hidden" value="<%= customFilterDisplayContext.getParameterValue() %>" />
					<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%= customFilterDisplayContext.getParameterName() %>" />

					<aui:fieldset>
						<ul class="list-unstyled">

							<%
							int i = 0;

							for (CustomFilterTermDisplayContext customFilterTermDisplayContext : customFilterDisplayContext.getTermDisplayContexts()) {
								i++;
							%>

								<li class="facet-value">
									<label class="facet-checkbox-label" for="<portlet:namespace />term_<%= i %>">
										<input class="facet-term" data-term-id="<%= customFilterTermDisplayContext.getFieldName() %>" id="<portlet:namespace />term_<%= i %>" name="<portlet:namespace />term_<%= i %>" onChange="Liferay.Search.FacetUtil.changeSelection(event);" type="checkbox" <%= customFilterTermDisplayContext.isSelected() ? "checked" : StringPool.BLANK %> />

										<span class="term-name <%= customFilterTermDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>">
											<%= HtmlUtil.escape(customFilterTermDisplayContext.getFieldName()) %>
										</span>

										<c:if test="<%= customFilterTermDisplayContext.isFrequencyVisible() %>">
											<small class="term-count">
												(<%= customFilterTermDisplayContext.getFrequency() %>)
											</small>
										</c:if>
									</label>
								</li>

							<%
							}
							%>

						</ul>
					</aui:fieldset>

					<c:if test="<%= !customFilterDisplayContext.isNothingSelected() %>">
						<a class="text-default" href="javascript:;" onClick="Liferay.Search.FacetUtil.clearSelections(event);"><small><liferay-ui:message key="clear" /></small></a>
					</c:if>
				</aui:form>
			</liferay-ui:panel>
		</liferay-ui:panel-container>
	</c:otherwise>
</c:choose>

<aui:script use="liferay-search-facet-util"></aui:script>