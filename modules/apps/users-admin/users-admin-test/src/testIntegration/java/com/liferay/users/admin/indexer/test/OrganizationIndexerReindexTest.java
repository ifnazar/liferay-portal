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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Marques de Paula
 */
@RunWith(Arquillian.class)
public class OrganizationIndexerReindexTest extends BaseOrganizationIndexerTestCase {
	
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setIndexerClass(Organization.class);
		super.setGroup(organizationFixture.addGroup());		
	}

	@Test
	public void testIndexedFields() throws Exception {
		organizationFixture.updateDisplaySettings(LocaleUtil.JAPAN);
				
		String organizationName = "新規作成";
		String countryName = "united-states";
		String regionName = "Alabama";		

		Organization organization = organizationFixture.createAnOrganization(
			organizationName, countryName, regionName, null);

		String searchTerm = "新規";

		organizationSearchFixture.searchOnlyOne(searchTerm, LocaleUtil.JAPAN);

		organizationSearchFixture.reindex(organization);

		organizationSearchFixture.searchOnlyOne(searchTerm, LocaleUtil.JAPAN);
	}

}
