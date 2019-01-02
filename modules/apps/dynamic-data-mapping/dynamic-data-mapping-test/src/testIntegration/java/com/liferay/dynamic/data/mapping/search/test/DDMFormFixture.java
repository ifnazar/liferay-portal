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
import com.liferay.portal.kernel.test.util.TestPropsValues;
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

	public DDMFormFixture(Group group) throws PortalException {
		_group = group;
	}

	public void setDDMFormInstanceRecordTestHelper(
		DDMFormInstanceRecordTestHelper ddmFormInstanceRecordTestHelper) {

		_ddmFormInstanceRecordTestHelper = ddmFormInstanceRecordTestHelper;
	}

	public void tearDown() throws PortalException {
		deleteAllDDMFormInstanceRecords();
		deleteALLDDMStructures();
		deleteDDMFormInstances();
	}

	protected DDMFormInstance addDDMFormInstance() throws Exception {
		return addDDMFormInstance(TestPropsValues.getUserId());
	}

	protected DDMFormInstance addDDMFormInstance(long userId) throws Exception {
		DDMStructure ddmStructure = addDDMStructure();

		DDMFormInstanceTestHelper ddmFormInstanceTestHelper =
			new DDMFormInstanceTestHelper(_group);

		DDMFormInstance formInstance =
			ddmFormInstanceTestHelper.addDDMFormInstance(userId, ddmStructure);

		_ddmFormInstances.add(formInstance);

		return formInstance;
	}

	protected DDMFormInstanceRecord addDDMFormInstanceRecord(
			Map<Locale, String> name, Map<Locale, String> description)
		throws Exception {

		Locale[] locales = new Locale[name.size()];

		name.keySet().toArray(locales);

		DDMFormValues ddmFormValues = createDDMFormValues(locales);

		DDMFormFieldValue nameDDMFormFieldValue =
			createLocalizedDDMFormFieldValue("name", name);

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormFieldValue descriptionDDMFormFieldValue =
			createLocalizedDDMFormFieldValue("description", description);

		ddmFormValues.addDDMFormFieldValue(descriptionDDMFormFieldValue);

		DDMFormInstanceRecord instanceRecord =
			_ddmFormInstanceRecordTestHelper.addDDMFormInstanceRecord(
				ddmFormValues);

		_ddmFormInstanceRecords.add(instanceRecord);

		return instanceRecord;
	}

	protected DDMFormInstanceRecord addDDMFormInstanceRecord(
			String name, String description)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.US, name);

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.US, description);

		return addDDMFormInstanceRecord(nameMap, descriptionMap);
	}

	protected DDMStructure addDDMStructure() throws Exception {
		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDMFormInstance.class), _group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			createDDMForm(LocaleUtil.US), StorageType.JSON.toString());

		_ddmStructures.add(ddmStructure);

		return ddmStructure;
	}

	protected DDMForm createDDMForm(Locale... locales) {
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

	protected DDMFormValues createDDMFormValues(Locale... locales)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceRecordTestHelper.getDDMFormInstance();

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return DDMFormValuesTestUtil.createDDMFormValues(
			ddmStructure.getDDMForm(),
			DDMFormValuesTestUtil.createAvailableLocales(locales), locales[0]);
	}

	protected DDMFormFieldValue createLocalizedDDMFormFieldValue(
		String name, Map<Locale, String> values) {

		Value localizedValue = new LocalizedValue(LocaleUtil.US);

		for (Map.Entry<Locale, String> value : values.entrySet()) {
			localizedValue.addString(value.getKey(), value.getValue());
		}

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			name, localizedValue);
	}

	protected void deleteAllDDMFormInstanceRecords() throws PortalException {
		for (DDMFormInstanceRecord ddmFormInstanceRecord :
				_ddmFormInstanceRecords) {

			DDMFormInstanceRecordLocalServiceUtil.deleteFormInstanceRecord(
				ddmFormInstanceRecord);
		}

		_ddmFormInstanceRecords.clear();
	}

	protected void deleteALLDDMStructures() {
		for (DDMStructure ddmStructure : _ddmStructures) {
			DDMStructureLocalServiceUtil.deleteDDMStructure(ddmStructure);
		}

		_ddmFormInstanceRecords.clear();
	}

	protected void deleteDDMFormInstances() {
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