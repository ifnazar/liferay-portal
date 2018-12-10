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

package com.liferay.portal.search.web.internal.custom.filter.display.context;

import java.util.List;

/**
 * @author Wade Cao
 */
public class CustomFilterDisplayContext {

	public String getDisplayCaption() {
		return _displayCaption;
	}

	public String getParameterName() {
		return _parameterName;
	}

	public String getParameterValue() {
		return _parameterValue;
	}

	public List<String> getParameterValues() {
		return _parameterValues;
	}

	public List<CustomFilterTermDisplayContext> getTermDisplayContexts() {
		return _customFilterTermDisplayContexts;
	}

	public boolean isNothingSelected() {
		return _nothingSelected;
	}

	public boolean isInvisible() {
		return _isInvisible;
	}

	public void setDisplayCaption(String displayCaption) {
		_displayCaption = displayCaption;
	}

	public void setNothingSelected(boolean nothingSelected) {
		_nothingSelected = nothingSelected;
	}

	public void setParameterName(String paramName) {
		_parameterName = paramName;
	}

	public void setParameterValue(String paramValue) {
		_parameterValue = paramValue;
	}

	public void setParameterValues(List<String> paramValues) {
		_parameterValues = paramValues;
	}

	public void setIsInvisible(boolean isInvisible) {
		_isInvisible = isInvisible;
	}

	public void setTermDisplayContexts(
		List<CustomFilterTermDisplayContext> customFilterTermDisplayContexts) {

		_customFilterTermDisplayContexts = customFilterTermDisplayContexts;
	}

	private List<CustomFilterTermDisplayContext> _customFilterTermDisplayContexts;
	private String _displayCaption;
	private boolean _nothingSelected;
	private String _parameterName;
	private String _parameterValue;
	private List<String> _parameterValues;
	private boolean _isInvisible;

}