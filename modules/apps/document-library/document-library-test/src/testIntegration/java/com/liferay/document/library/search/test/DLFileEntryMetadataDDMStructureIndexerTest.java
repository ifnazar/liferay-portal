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

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.background.task.DDMStructureBackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Lucas Marques de Paula
 */
@RunWith(Arquillian.class)
public final class DLFileEntryMetadataDDMStructureIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	public void runBackgroundTaskReindex(DDMStructure structure)
		throws Exception {

		DDMStructureBackgroundTask backgroundTask =
			new DDMStructureBackgroundTask(structure.getStructureId());

		backgroundTaskExecutor.execute(backgroundTask);
	}

	@Before
	public void setUp() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(Locale.JAPAN);

		setUpUserSearchFixture();
		setUpDLFileEntryIndexerFixture();
		setUpFileEntryMetadataFixture();
	}

	@After
	public void tearDown() throws Exception {
		setIndexerEnable(true);
		fileEntryMetadataFixture.tearDown();
	}

	@Test
	public void testDisableDLFileEntryIndexer()
		throws Exception, PortalException {

		Locale locale = Locale.JAPAN;
		String title = "新規作成";
		String searchTerm = "新規";

		DDMStructure ddmStructure =
			fileEntryMetadataFixture.createStructureWithdDLFileEntry(
				title, locale);

		Document document = indexerFixture.searchOnlyOne(searchTerm, locale);

		indexerFixture.deleteDocument(document);

		setIndexerEnable(false);

		runBackgroundTaskReindex(ddmStructure);

		indexerFixture.searchNoOne(searchTerm, locale);
	}

	@Test
	public void testReindexDLFileEntry() throws Exception, PortalException {
		Locale locale = Locale.JAPAN;
		String title = "新規作成";
		String searchTerm = "新規";

		DDMStructure ddmStructure =
			fileEntryMetadataFixture.createStructureWithdDLFileEntry(
				title, locale);

		Document document = indexerFixture.searchOnlyOne(searchTerm, locale);

		indexerFixture.deleteDocument(document);

		runBackgroundTaskReindex(ddmStructure);

		indexerFixture.searchOnlyOne(searchTerm, locale);
	}

	protected void setIndexerEnable(boolean indexerEnabled) {
		Indexer<?> indexer = IndexerRegistryUtil.getIndexer(DLFileEntry.class);

		indexer.setIndexerEnabled(indexerEnabled);
	}

	protected void setUpDLFileEntryIndexerFixture() {
		indexerFixture = new IndexerFixture<>(DLFileEntry.class);
	}

	protected void setUpFileEntryMetadataFixture() {
		fileEntryMetadataFixture = new DLFileEntryMetadataDDMStructureFixture(
			group);
	}

	protected void setUpUserSearchFixture() throws Exception {
		UserSearchFixture userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		groups = userSearchFixture.getGroups();
		users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	@Inject(
		filter = "background.task.executor.class.name=com.liferay.dynamic.data.mapping.background.task.DDMStructureIndexerBackgroundTaskExecutor"
	)
	protected BackgroundTaskExecutor backgroundTaskExecutor;

	protected DLFileEntryMetadataDDMStructureFixture fileEntryMetadataFixture;
	protected Group group;

	@DeleteAfterTestRun
	protected List<Group> groups;

	protected IndexerFixture<DLFileEntry> indexerFixture;

	@DeleteAfterTestRun
	protected List<User> users;

}