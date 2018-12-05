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

package com.liferay.dynamic.data.mapping.test.util.background.task;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest.Builder;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lucas Marques de Paula
 */
public abstract class BaseDDMStructureIndexerTestCase {

	protected final DDMStructure addDDMStructure(Group group, Class<?> clazz)
		throws Exception, PortalException {

		long classNameId = PortalUtil.getClassNameId(clazz);

		Builder builder = Builder.newBuilder(_getStructureDefinition());

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormDeserializer.deserialize(builder.build());

		DDMForm ddmForm = ddmFormDeserializerDeserializeResponse.getDDMForm();

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(classNameId, group);

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			classNameId, null, RandomTestUtil.randomString(), ddmForm,
			StorageType.JSON.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		_ddmStructures.add(ddmStructure);

		return ddmStructure;
	}

	protected final Group addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	protected final String getResourceAsString(String filename, Class<?> clazz)
		throws Exception {

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(filename);

		return StringUtil.read(inputStream);
	}

	protected void runBackgroundTaskReindex(DDMStructure ddmStructure)
		throws Exception {

		DDMStructureBackgroundTask backgroundTask =
			new DDMStructureBackgroundTask();

		backgroundTask.setTaskContextMap(
			Collections.singletonMap(
				"structureId", ddmStructure.getStructureId()));

		backgroundTaskExecutor.execute(backgroundTask);
	}

	@Inject(
		filter = "background.task.executor.class.name=com.liferay.dynamic.data.mapping.background.task.DDMStructureIndexerBackgroundTaskExecutor"
	)
	protected BackgroundTaskExecutor backgroundTaskExecutor;

	@Inject(filter = "ddm.form.deserializer.type=json")
	protected DDMFormDeserializer ddmFormDeserializer;

	private String _getStructureDefinition() throws Exception {
		if (_structureDefinition == null) {
			_structureDefinition = getResourceAsString(
				"com/liferay/dynamic/data/mapping/test/util/background/task" +
					"/dynamic-data-mapping-structure-definition.json",
				BaseDDMStructureIndexerTestCase.class);
		}

		return _structureDefinition;
	}

	@DeleteAfterTestRun
	private final List<DDMStructure> _ddmStructures = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>(1);

	private String _structureDefinition;

}