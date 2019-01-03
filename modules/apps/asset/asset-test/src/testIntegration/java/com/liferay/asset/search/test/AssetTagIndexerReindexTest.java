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

package com.liferay.asset.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luan Maoski
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class AssetTagIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();
		setUpAssetTagIndexerFixture();
		setUpAssetTagFixture();
	}

	@Test
	public void testReindexing() throws Exception {
		AssetTag assetTag = assetTagFixture.createAssetTag();

		String searchTerm = assetTag.getUserName();

		assetTagIndexerFixture.searchOnlyOne(searchTerm);

		Document document = assetTagIndexerFixture.searchOnlyOne(searchTerm);

		assetTagIndexerFixture.deleteDocument(document);

		assetTagIndexerFixture.searchNoOne(searchTerm);

		assetTagIndexerFixture.reindex(assetTag.getCompanyId());

		assetTagIndexerFixture.searchOnlyOne(searchTerm);
	}

	protected void setUpAssetTagFixture() throws Exception {
		assetTagFixture = new AssetTagFixture(group, user);

		_assetTags = assetTagFixture.getAssetTags();
	}

	protected void setUpAssetTagIndexerFixture() {
		assetTagIndexerFixture = new IndexerFixture<>(AssetTag.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		group = userSearchFixture.addGroup();

		user = userSearchFixture.addUser(RandomTestUtil.randomString(), group);

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();
	}

	protected AssetTagFixture assetTagFixture;
	protected IndexerFixture<AssetTag> assetTagIndexerFixture;
	protected Group group;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected User user;
	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private List<AssetTag> _assetTags;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}