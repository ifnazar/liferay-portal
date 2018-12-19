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

package com.liferay.dynamic.data.mapping.search.test;

import com.liferay.dynamic.data.mapping.helper.DDMFormInstanceRecordTestHelper;
import com.liferay.dynamic.data.mapping.helper.DDMFormInstanceTestHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Igor Fabiano Nazar
 */
public class DDMFormFixture {

	public DDMFormFixture(Group group) {
		_group = group;
	}

	public DDMFormInstance addDDMFormInstance() throws Exception {
		DDMStructure ddmStructure = _addDDMStructure();

		DDMFormInstanceTestHelper ddmFormInstanceTestHelper =
			new DDMFormInstanceTestHelper(_group);

		DDMFormInstance formInstance =
			ddmFormInstanceTestHelper.addDDMFormInstance(ddmStructure);

		_ddmFormInstances.add(formInstance);

		return formInstance;
	}

	public DDMFormInstanceRecord addDDMFormInstanceRecord(
			String name, String description)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.US, name);

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.US, description);

		return _addDDMFormInstanceRecord(nameMap, descriptionMap);
	}

	public void setDDMFormInstanceRecordTestHelper(
		DDMFormInstanceRecordTestHelper ddmFormInstanceRecordTestHelper) {

		_ddmFormInstanceRecordTestHelper = ddmFormInstanceRecordTestHelper;
	}

	public void tearDown() throws PortalException {
		_deleteAllDDMFormInstanceRecords();
		_deleteALLDDMStructures();
		_deleteDDMFormInstances();
	}

	private DDMFormInstanceRecord _addDDMFormInstanceRecord(
			Map<Locale, String> name, Map<Locale, String> description)
		throws Exception {

		Locale[] locales = new Locale[name.size()];

		name.keySet().toArray(locales);

		DDMFormValues ddmFormValues = _createDDMFormValues(locales);

		DDMFormFieldValue nameDDMFormFieldValue =
			_createLocalizedDDMFormFieldValue("name", name);

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormFieldValue descriptionDDMFormFieldValue =
			_createLocalizedDDMFormFieldValue("description", description);

		ddmFormValues.addDDMFormFieldValue(descriptionDDMFormFieldValue);

		DDMFormInstanceRecord instanceRecord =
			_ddmFormInstanceRecordTestHelper.addDDMFormInstanceRecord(
				ddmFormValues);

		_ddmFormInstanceRecords.add(instanceRecord);

		return instanceRecord;
	}

	private DDMStructure _addDDMStructure() throws Exception {
		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDMFormInstance.class), _group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			_createDDMForm(LocaleUtil.US), StorageType.JSON.toString());

		_ddmStructures.add(ddmStructure);

		return ddmStructure;
	}

	private DDMForm _createDDMForm(Locale... locales) {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(locales), locales[0]);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", true, false, false);

		nameDDMFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormField descriptionDDMFormField =
			DDMFormTestUtil.createTextDDMFormField(
				"description", true, false, false);

		descriptionDDMFormField.setIndexType("text");

		ddmForm.addDDMFormField(descriptionDDMFormField);

		return ddmForm;
	}

	private DDMFormValues _createDDMFormValues(Locale... locales)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceRecordTestHelper.getDDMFormInstance();

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return DDMFormValuesTestUtil.createDDMFormValues(
			ddmStructure.getDDMForm(),
			DDMFormValuesTestUtil.createAvailableLocales(locales), locales[0]);
	}

	private DDMFormFieldValue _createLocalizedDDMFormFieldValue(
		String name, Map<Locale, String> values) {

		Value localizedValue = new LocalizedValue(LocaleUtil.US);

		for (Map.Entry<Locale, String> value : values.entrySet()) {
			localizedValue.addString(value.getKey(), value.getValue());
		}

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			name, localizedValue);
	}

	private void _deleteAllDDMFormInstanceRecords() throws PortalException {
		for (DDMFormInstanceRecord ddmFormInstanceRecord :
				_ddmFormInstanceRecords) {

			DDMFormInstanceRecordLocalServiceUtil.deleteFormInstanceRecord(
				ddmFormInstanceRecord);
		}

		_ddmFormInstanceRecords.clear();
	}

	private void _deleteALLDDMStructures() {
		for (DDMStructure ddmStructure : _ddmStructures) {
			DDMStructureLocalServiceUtil.deleteDDMStructure(ddmStructure);
		}

		_ddmStructures.clear();
	}

	private void _deleteDDMFormInstances() {
		for (DDMFormInstance ddmFormInstance : _ddmFormInstances) {
			DDMFormInstanceLocalServiceUtil.deleteDDMFormInstance(
				ddmFormInstance);
		}

		_ddmFormInstances.clear();
	}

	private final List<DDMFormInstanceRecord> _ddmFormInstanceRecords =
		new ArrayList<>();
	private DDMFormInstanceRecordTestHelper _ddmFormInstanceRecordTestHelper;
	private final List<DDMFormInstance> _ddmFormInstances = new ArrayList<>();
	private final List<DDMStructure> _ddmStructures = new ArrayList<>();
	private final Group _group;

}