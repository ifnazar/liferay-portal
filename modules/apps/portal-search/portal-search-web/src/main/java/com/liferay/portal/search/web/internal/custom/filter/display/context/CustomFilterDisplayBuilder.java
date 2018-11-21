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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Wade Cao
 */
public class CustomFilterDisplayBuilder {

	public CustomFilterDisplayContext build() {
		boolean nothingSelected = isNothingSelected();

		List<TermCollector> termCollectors = getTermsCollectors();

		boolean renderNothing = false;

		if (nothingSelected && termCollectors.isEmpty()) {
			renderNothing = true;
		}

		CustomFilterDisplayContext customFilterDisplayContext =
			new CustomFilterDisplayContext();

		customFilterDisplayContext.setDisplayCaption(getDisplayCaption());
		customFilterDisplayContext.setNothingSelected(nothingSelected);
		customFilterDisplayContext.setParameterName(_parameterName);
		customFilterDisplayContext.setParameterValue(getFirstParameterValue());
		customFilterDisplayContext.setParameterValues(_parameterValues);
		customFilterDisplayContext.setRenderNothing(renderNothing);
		customFilterDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts(termCollectors));

		return customFilterDisplayContext;
	}

	public void setCustomDisplayCaption(String customDisplayCaption) {
		_customDisplayCaption = customDisplayCaption;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFieldToAggregate(String fieldToAggregate) {
		_fieldToAggregate = fieldToAggregate;
	}

	public void setFrequenciesVisible(boolean frequenciesVisible) {
		_frequenciesVisible = frequenciesVisible;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		parameterValue = StringUtil.trim(
			Objects.requireNonNull(parameterValue));

		if (parameterValue.isEmpty()) {
			return;
		}

		_parameterValues = Collections.singletonList(parameterValue);
	}

	public void setParameterValues(List<String> parameterValues) {
		_parameterValues = parameterValues;
	}

	protected CustomFilterTermDisplayContext buildTermDisplayContext(
		TermCollector termCollector) {

		String term = GetterUtil.getString(termCollector.getTerm());

		CustomFilterTermDisplayContext customFilterTermDisplayContext =
			new CustomFilterTermDisplayContext();

		customFilterTermDisplayContext.setFrequency(
			termCollector.getFrequency());
		customFilterTermDisplayContext.setFrequencyVisible(_frequenciesVisible);
		customFilterTermDisplayContext.setSelected(isSelected(term));
		customFilterTermDisplayContext.setFieldName(term);

		return customFilterTermDisplayContext;
	}

	protected List<CustomFilterTermDisplayContext> buildTermDisplayContexts(
		List<TermCollector> termCollectors) {

		if (termCollectors.isEmpty()) {
			return getEmptyTermDisplayContexts();
		}

		List<CustomFilterTermDisplayContext> customFilterTermDisplayContexts =
			new ArrayList<>(termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			if (((_maxTerms > 0) && (i >= _maxTerms)) ||
				((_frequencyThreshold > 0) &&
				 (_frequencyThreshold > termCollector.getFrequency()))) {

				break;
			}

			customFilterTermDisplayContexts.add(
				buildTermDisplayContext(termCollector));
		}

		return customFilterTermDisplayContexts;
	}

	protected String getDisplayCaption() {
		Optional<String> optional1 = SearchStringUtil.maybe(
			_customDisplayCaption);

		Optional<String> optional2 = SearchStringUtil.maybe(
			optional1.orElse(_fieldToAggregate));

		return optional2.orElse("custom");
	}

	protected List<CustomFilterTermDisplayContext>
		getEmptyTermDisplayContexts() {

		if (_parameterValues.isEmpty()) {
			return Collections.emptyList();
		}

		CustomFilterTermDisplayContext customFilterTermDisplayContext =
			new CustomFilterTermDisplayContext();

		customFilterTermDisplayContext.setFrequency(0);
		customFilterTermDisplayContext.setFrequencyVisible(_frequenciesVisible);
		customFilterTermDisplayContext.setSelected(true);
		customFilterTermDisplayContext.setFieldName(_parameterValues.get(0));

		return Collections.singletonList(customFilterTermDisplayContext);
	}

	protected String getFirstParameterValue() {
		if (_parameterValues.isEmpty()) {
			return StringPool.BLANK;
		}

		return _parameterValues.get(0);
	}

	protected List<TermCollector> getTermsCollectors() {
		if (_facet != null) {
			FacetCollector facetCollector = _facet.getFacetCollector();

			if (facetCollector != null) {
				return facetCollector.getTermCollectors();
			}
		}

		return Collections.<TermCollector>emptyList();
	}

	protected boolean isNothingSelected() {
		if (_parameterValues.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isSelected(String value) {
		if (_parameterValues.contains(value)) {
			return true;
		}

		return false;
	}

	private String _customDisplayCaption;
	private Facet _facet;
	private String _fieldToAggregate;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private int _maxTerms;
	private String _parameterName;
	private List<String> _parameterValues = Collections.emptyList();

}