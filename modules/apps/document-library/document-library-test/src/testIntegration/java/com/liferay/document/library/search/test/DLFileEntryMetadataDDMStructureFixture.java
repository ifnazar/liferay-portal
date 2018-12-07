package com.liferay.document.library.search.test;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestDataConstants;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Igor Fabiano Nazar
 * @author Lucas Marques de Paula
 */
public class DLFileEntryMetadataDDMStructureFixture {

	public DLFileEntryMetadataDDMStructureFixture(Group group) {
		_group = group;
	}

	public void tearDown() throws PortalException {
		deleteAllFileEntries();

		deleteAllFileEntryTypes();

		deleteAllDLFolders();

		deleteAllDDMStructures();
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

		_fileEntries.add(dlFileEntry);

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

		_fileEntryTypes.add(dlFileEntryType);

		return dlFileEntryType;
	}

	protected DLFolder addDLFolder(String name) throws PortalException {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		String description = RandomTestUtil.randomString();

		DLFolder dlFolder = DLFolderLocalServiceUtil.addFolder(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), false, 0L, name, description, false,
			serviceContext);

		_folders.add(dlFolder);

		return dlFolder;
	}

	protected DDMStructure createStructureWithdDLFileEntry(
			String title, Locale locale)
		throws Exception, PortalException {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			new Locale[] {locale}, locale);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName(), ddmForm,
			locale);

		_ddmStructures.add(ddmStructure);

		DLFileEntryType dlFileEntryType = addDLFileEntryType(
			_group, ddmStructure);

		String folderName = RandomTestUtil.randomString();

		DLFolder folder = addDLFolder(folderName);

		addDLFileEntry(_group, folder, dlFileEntryType, title);

		return ddmStructure;
	}

	protected void deleteAllDDMStructures() throws PortalException {
		for (DDMStructure ddmStructure : _ddmStructures) {
			DDMStructureLocalServiceUtil.deleteDDMStructure(
				ddmStructure.getStructureId());
		}
	}

	protected void deleteAllDLFolders() throws PortalException {
		for (DLFolder dlFolder : _folders) {
			DLFolderLocalServiceUtil.deleteDLFolder(dlFolder.getFolderId());
		}
	}

	protected void deleteAllFileEntries() throws PortalException {
		for (DLFileEntry dlFileEntry : _fileEntries) {
			DLFileEntryLocalServiceUtil.deleteDLFileEntry(
				dlFileEntry.getFileEntryId());
		}
	}

	protected void deleteAllFileEntryTypes() throws PortalException {
		for (DLFileEntryType dlFileEntryType : _fileEntryTypes) {
			DLFileEntryTypeLocalServiceUtil.deleteDLFileEntryType(
				dlFileEntryType.getFileEntryTypeId());
		}
	}

	private final List<DDMStructure> _ddmStructures = new ArrayList<>();
	private final List<DLFileEntry> _fileEntries = new ArrayList<>();
	private final List<DLFileEntryType> _fileEntryTypes = new ArrayList<>();
	private final List<DLFolder> _folders = new ArrayList<>();
	private final Group _group;

}