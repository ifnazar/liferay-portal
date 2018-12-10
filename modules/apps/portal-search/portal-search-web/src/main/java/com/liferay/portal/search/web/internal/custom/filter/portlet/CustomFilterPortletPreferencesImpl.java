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

package com.liferay.portal.search.web.internal.custom.filter.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.custom.filter.builder.CustomDTO;
import com.liferay.portal.search.web.internal.custom.filter.portlet.shared.search.CustomFilterPortletSharedSearchContributor;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Igor Nazar
 * @author Luan Maoski
 */
public class CustomFilterPortletPreferencesImpl
	implements CustomFilterPortletPreferences {

	public CustomFilterPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional,
		JSONFactory jsonFactory) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
		_jsonFactory = jsonFactory;
	}

	@Override
	public List<CustomDTO> getComboValues() {
		return loadFields();
	}

	@Override
	public String getDateValue() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_DATE_VALUE,
			StringPool.BLANK);
	}

	@Override
	public String getFilterField() {
		Optional<String> optional = getFilterFieldOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getFilterFieldOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_FIELD);
	}

	@Override
	public String getFilterType() {
		String selectedString = getSelectedValue();
		List<CustomDTO> customDTOs = loadFields();

		for (CustomDTO customDTO : customDTOs) {
			String fieldType = customDTO.getField();

			if (fieldType.equals(selectedString)) {
				return customDTO.getType();
			}
		}

		return "";
	}

	@Override
	public String getFilterValue() {
		Optional<String> optional = getFilterValueOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getFilterValueOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_VALUE);
	}

	@Override
	public String getSelectedValue() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_SELECTED_VALUE,
			StringPool.BLANK);
	}

	@Override
	public boolean isDisplay(String value) {
		String selectedValue = getSelectedValue();

		if (value.equals(selectedValue)) {
			return true;
		}

		return false;
	}

	public List<CustomDTO> loadFields() {
		String mappings = _getResourceAsString(
			CustomFilterPortletSharedSearchContributor.class,
			"/META-INF/mappings/liferay-type-mappings.json");

		try {
			JSONObject root = _jsonFactory.createJSONObject(mappings);

			JSONObject liferayDocumentType = (JSONObject)root.get(
				"LiferayDocumentType");

			JSONObject properties = (JSONObject)liferayDocumentType.get(
				"properties");

			List<CustomDTO> result = new ArrayList<>();

			for (Iterator<?> i = properties.keys(); i.hasNext();) {
				String key = (String)i.next();

				JSONObject property = (JSONObject)properties.get(key);

				String type = (String)(property.get("type"));

				CustomDTO dto = new CustomDTO(key, type);

				result.add(dto);
			}

			orderList(result);

			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String _getResourceAsString(Class<?> clazz, String resourceName) {
		try (InputStream inputStream = clazz.getResourceAsStream(
				resourceName)) {

			return StringUtil.read(inputStream);
		}
		catch (IOException ioe) {
			throw new RuntimeException(
				"Unable to load resource: " + resourceName, ioe);
		}
	}

	private void orderList(List<CustomDTO> result) {
		Collections.sort(result, new Comparator<CustomDTO>() {
			@Override
			public int compare(CustomDTO o1, CustomDTO o2) {
				return o1.getField().compareTo(o2.getField());
			}
		});
	}

	private final JSONFactory _jsonFactory;
	private final PortletPreferencesHelper _portletPreferencesHelper;
	
	

	@Override
	public String getCustomParameterName() {
		Optional<String> optional = getCustomParameterNameOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getCustomParameterNameOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_CUSTOM_PARAMETER_NAME);
	}

	@Override
	public boolean isInvisible() {
		return _portletPreferencesHelper.getBoolean(
			CustomFilterPortletPreferences.PREFERENCE_KEY_IS_INVISIBLE, false);			
	}
	
	@Override
	public String getCustomHeading() {
		Optional<String> optional = getCustomHeadingOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getCustomHeadingOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING);
	}	

}