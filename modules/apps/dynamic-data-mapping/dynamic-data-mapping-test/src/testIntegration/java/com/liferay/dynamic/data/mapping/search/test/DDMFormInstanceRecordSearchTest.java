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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.helper.DDMFormInstanceRecordTestHelper;
import com.liferay.dynamic.data.mapping.helper.DDMFormInstanceTestHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lino Alves
 * @author Marcela Cunha
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceRecordSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCompanyId(TestPropsValues.getCompanyId());

		setUpDDMFormInstanceFixture();

		DDMFormInstance ddmFormInstance = ddmFormFixture.addDDMFormInstance();

		DDMFormInstanceRecordTestHelper ddmFormInstanceRecordTestHelper =
			new DDMFormInstanceRecordTestHelper(_group, ddmFormInstance);

		ddmFormFixture.setDDMFormInstanceRecordTestHelper(
			ddmFormInstanceRecordTestHelper);

		_searchContext = getSearchContext(_group, _user, ddmFormInstance);
	}

	@After
	public void tearDown() throws PortalException {
		ddmFormFixture.tearDown();
	}

	@Test
	public void testBasicSearchWithDefaultUser() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		User user = UserLocalServiceUtil.getDefaultUser(companyId);

		ddmFormFixture.addDDMFormInstanceRecord(
			"Joe Bloggs", "Simple description");

		_searchContext.setKeywords("Simple description");

		_searchContext.setUserId(user.getUserId());

		assertSearch("description", 1);
	}

	@Test
	public void testBasicSearchWithJustOneTerm() throws Exception {
		ddmFormFixture.addDDMFormInstanceRecord(
			"Joe Bloggs", "Simple description");
		ddmFormFixture.addDDMFormInstanceRecord(
			"Bloggs", "Another description example");
		ddmFormFixture.addDDMFormInstanceRecord(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		assertSearch("example", 1);
		assertSearch("description", 2);
	}

	@Test
	public void testExactPhrase() throws Exception {
		ddmFormFixture.addDDMFormInstanceRecord(
			"Joe Bloggs", "Simple description");
		ddmFormFixture.addDDMFormInstanceRecord(
			"Bloggs", "Another description example");
		ddmFormFixture.addDDMFormInstanceRecord(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		assertSearch("\"Joe Bloggs\"", 1);
		assertSearch("Bloggs", 2);
	}

	@Test
	public void testNonindexableField() throws Exception {
		Locale[] locales = {LocaleUtil.US};

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(locales), locales[0]);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", true, false, false);

		nameDDMFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormField notIndexableDDMFormField =
			DDMFormTestUtil.createTextDDMFormField(
				"notIndexable", true, false, false);

		notIndexableDDMFormField.setIndexType("");

		DDMFormInstanceTestHelper ddmFormInstanceTestHelper =
			new DDMFormInstanceTestHelper(_group);

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDMFormInstance.class), _group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.JSON.toString());

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceTestHelper.addDDMFormInstance(ddmStructure);

		DDMFormInstanceRecordTestHelper instanceRecordHelper =
			new DDMFormInstanceRecordTestHelper(_group, ddmFormInstance);

		ddmFormFixture.setDDMFormInstanceRecordTestHelper(instanceRecordHelper);

		_searchContext = getSearchContext(_group, _user, ddmFormInstance);

		ddmFormFixture.addDDMFormInstanceRecord(
			"Liferay", "Not indexable name");

		assertSearch("Liferay", 1);
		assertSearch("Not indexable name", 0);
	}

	@Test
	public void testStopwords() throws Exception {
		ddmFormFixture.addDDMFormInstanceRecord(
			RandomTestUtil.randomString(), "Simple text");
		ddmFormFixture.addDDMFormInstanceRecord(
			RandomTestUtil.randomString(), "Another description example");

		assertSearch("Another The Example", 1);
	}

	protected static SearchContext getSearchContext(
			Group group, User user, DDMFormInstance ddmFormInstance)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		searchContext.setAttribute(
			"formInstanceId", ddmFormInstance.getFormInstanceId());
		searchContext.setAttribute("status", WorkflowConstants.STATUS_ANY);
		searchContext.setUserId(user.getUserId());

		return searchContext;
	}

	protected void assertSearch(String keywords, int length) throws Exception {
		_searchContext.setKeywords(keywords);

		BaseModelSearchResult<DDMFormInstanceRecord> result =
			DDMFormInstanceRecordLocalServiceUtil.searchFormInstanceRecords(
				_searchContext);

		Assert.assertEquals(length, result.getLength());
	}

	protected void setUpDDMFormInstanceFixture() throws PortalException {
		ddmFormFixture = new DDMFormFixture(_group);
	}

	protected DDMFormFixture ddmFormFixture;

	@DeleteAfterTestRun
	private Group _group;

	private SearchContext _searchContext;

	@DeleteAfterTestRun
	private User _user;

}