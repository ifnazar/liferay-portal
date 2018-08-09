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

package com.liferay.users.admin.indexer.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portlet.expando.util.test.ExpandoTestUtil;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public abstract class BaseOrganizationIndexerTestCase {

	public void setUp() throws Exception {
		organizationFixture = createOrganizationFixture();

		organizationFixture.setUp();

		organizationSearchFixture = createOrganizationSearchFixture();
		indexedFieldsFixture = createIndexedFieldsFixture();
	}

	protected IndexedFieldsFixture createIndexedFieldsFixture() {
		return new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected OrganizationFixture createOrganizationFixture() {
		return new OrganizationFixture(
			organizationService, countryService, regionService, _groups,
			_organizations);
	}

	protected OrganizationSearchFixture createOrganizationSearchFixture() {
		return new OrganizationSearchFixture(indexerRegistry);
	}

	protected String getCountryNameForAllAvailableLocales(
		Organization organization) {

		Country country = countryService.fetchCountry(
			organization.getCountryId());

		Set<String> countryNames = new HashSet<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String countryName = country.getName(locale);

			countryName = StringUtil.toLowerCase(countryName);

			countryNames.add(countryName);
		}

		return joinCountryNames(countryNames);
	}

	protected String joinCountryNames(Set<String> countryNames) {
		String separator = ", ";
		StringBuffer sb = new StringBuffer();

		for (String string : countryNames) {
			if (sb.length() != 0) {
				sb.append(separator);
			}

			sb.append(string);
		}

		return "[" + sb.toString() + "]";
	}

	protected void setGroup(Group group) {
		organizationFixture.setGroup(group);
	}

	protected void setIndexerClass(Class<?> clazz) {
		organizationSearchFixture.setIndexerClass(clazz);
	}	

	@Inject
	protected ClassNameLocalService classNameLocalService;

	@Inject
	protected CountryService countryService;

	@Inject
	protected ExpandoColumnLocalService expandoColumnLocalService;

	@DeleteAfterTestRun
	protected final List<ExpandoColumn> expandoColumns = new ArrayList<>();

	@Inject
	protected ExpandoTableLocalService expandoTableLocalService;

	@DeleteAfterTestRun
	protected final List<ExpandoTable> expandoTables = new ArrayList<>();

	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected IndexerRegistry indexerRegistry;

	protected OrganizationFixture organizationFixture;
	protected OrganizationSearchFixture organizationSearchFixture;

	@Inject
	protected OrganizationService organizationService;

	@Inject
	protected RegionService regionService;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>(1);

}