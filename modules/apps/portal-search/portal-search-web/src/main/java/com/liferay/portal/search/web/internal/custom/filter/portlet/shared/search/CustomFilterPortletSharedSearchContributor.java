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

package com.liferay.portal.search.web.internal.custom.filter.portlet.shared.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.filter.DateRangeFilterBuilder;
import com.liferay.portal.search.web.internal.custom.filter.constants.CustomFilterPortletKeys;
import com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletPreferences;
import com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Nazar
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + CustomFilterPortletKeys.CUSTOM_FILTER,
	service = PortletSharedSearchContributor.class
)
public class CustomFilterPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFilterPortletPreferences customFilterPortletPreferences =
			new CustomFilterPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences(),
				_jsonFactory);

		Optional<String> fieldFieldOptional =
			customFilterPortletPreferences.getFilterFieldOptional();

		String fieldValueOptional =
			customFilterPortletPreferences.getFilterValue();

		String selectedValue =
			customFilterPortletPreferences.getSelectedValue();
				
		String dateValue = customFilterPortletPreferences.getDateValue();
				
		fieldFieldOptional.ifPresent(
			fieldField -> {
				buildFilter(
					fieldField, fieldValueOptional, selectedValue, dateValue,
					customFilterPortletPreferences,
					portletSharedSearchSettings);
			});
	}

	protected void buildFilter(
		String filterField, String filterValue, String selectedValue,
		String dateValue,
		CustomFilterPortletPreferences customFilterPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		filterField = selectedValue;

		@SuppressWarnings("unchecked")
		HashMap<String, String> customFilterMap =
			(HashMap<String, String>)searchContext.getAttribute(
				"customFilterWidget");

		if (customFilterMap == null) {
			customFilterMap = new HashMap<>();
		}

		String formattedDateStart = null;
		String formattedDateEnd = null;
		
		if(dateValue != null && !dateValue.trim().equals("")) {			
			formattedDateStart = _formatDate(dateValue, true);
			formattedDateEnd = _formatDate(dateValue, false);	
			customFilterMap.put(filterField, 
				formattedDateStart + "," + formattedDateEnd);						
		}else if (filterValue != null && filterValue.trim().equals("") ) {
			customFilterMap.put(filterField, filterValue);
		}
									
		searchContext.setAttribute("customFilterWidget", customFilterMap);
	}

	protected <T> void copy(Supplier<Optional<T>> from, Consumer<T> to) {
		Optional<T> optional = from.get();

		optional.ifPresent(to);
	}

	protected String getFilterField(
		CustomFilterPortletPreferences customFilterPortletPreferences,
		String portletId) {

		return customFilterPortletPreferences.getFilterField() +
			StringPool.PERIOD + portletId;
	}

	protected String getParameterName(
		CustomFilterPortletPreferences customFilterPortletPreferences) {

		Optional<String> optional = Stream.of(
			customFilterPortletPreferences.getFilterFieldOptional()
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).findFirst();

		return optional.orElse("customfield");
	}
	
	private String _formatDate(String date, boolean period) {
		try {
			DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
			
			if(period) {
				DateFormat dfStart = new SimpleDateFormat("yyyyMMdd000000");
				return dfStart.format(df2.parse(date));
			}else {
				DateFormat dfEnd = new SimpleDateFormat("yyyyMMdd235959");
				return dfEnd.format(df2.parse(date));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	@Reference
	private JSONFactory _jsonFactory;

}