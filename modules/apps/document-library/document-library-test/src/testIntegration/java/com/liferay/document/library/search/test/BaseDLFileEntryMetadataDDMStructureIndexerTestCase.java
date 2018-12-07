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

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.background.task.BaseDDMStructureIndexerTestCase;
import com.liferay.dynamic.data.mapping.test.util.background.task.DDMStructureIndexerFixture;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestDataConstants;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Lucas Marques de Paula
 */
public abstract class BaseDLFileEntryMetadataDDMStructureIndexerTestCase
	extends BaseDDMStructureIndexerTestCase {

	public void setUp() {
		LocaleThreadLocal.setSiteDefaultLocale(Locale.JAPAN);

		indexerFixture = new DDMStructureIndexerFixture(DLFileEntry.class);

		indexerFixture.setIsEnable(true);
	}

	protected DLFileEntry addDLFileEntry(
			Group group, DLFolder dlFolder, DLFileEntryType dlFileEntryType,
			String title)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			TestPropsValues.getUserId(), dlFolder.getRepositoryId(),
			dlFolder.getFolderId(), RandomTestUtil.randomString(),
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			inputStream, bytes.length, serviceContext);

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.getFileEntry(
			fileEntry.getFileEntryId());

		_dlFileEntries.add(dlFileEntry);

		return dlFileEntry;
	}

	protected DLFileEntryType addDLFileEntryType(
			Group group, DDMStructure ddmStructure)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeLocalServiceUtil.addFileEntryType(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				new long[] {ddmStructure.getStructureId()}, serviceContext);

		_dlFileEntryTypes .add(dlFileEntryType);

		return dlFileEntryType;
	}

	protected DLFolder addDLFolder(Group group) throws PortalException {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLFolder dlFolder = DLFolderLocalServiceUtil.addFolder(
			TestPropsValues.getUserId(), group.getGroupId(), group.getGroupId(),
			false, 0L, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, serviceContext);

		_dlFolders.add(dlFolder);

		return dlFolder;
	}

	protected static final int NUMBER_OF_SAMPLES = 100;

	protected DDMStructureIndexerFixture indexerFixture;

	@DeleteAfterTestRun
	private final List<DDMTemplate> _ddmTemplates = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<DLFileEntry> _dlFileEntries = new ArrayList<>(
		NUMBER_OF_SAMPLES);

	@DeleteAfterTestRun
	private final List<DLFileEntryType> _dlFileEntryTypes = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<DLFolder> _dlFolders = new ArrayList<>(1);

}