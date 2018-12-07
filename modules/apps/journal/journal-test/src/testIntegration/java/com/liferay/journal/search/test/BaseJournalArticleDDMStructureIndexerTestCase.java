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

package com.liferay.journal.search.test;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.test.util.background.task.BaseDDMStructureIndexerTestCase;
import com.liferay.dynamic.data.mapping.test.util.background.task.DDMStructureIndexerFixture;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lucas Marques de Paula
 */
public abstract class BaseJournalArticleDDMStructureIndexerTestCase
	extends BaseDDMStructureIndexerTestCase {

	public void setUp() {
		LocaleThreadLocal.setSiteDefaultLocale(Locale.JAPAN);

		indexerFixture = new DDMStructureIndexerFixture(JournalArticle.class);

		indexerFixture.setIsEnable(true);
	}

	protected final DDMTemplate addDDMTemplate(
			Group group, DDMStructure ddmStructure)
		throws Exception, PortalException {

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		_ddmTemplates.add(ddmTemplate);

		return ddmTemplate;
	}

	protected final JournalArticle addJournalArticle(
			Group group, DDMStructure ddmStructure, DDMTemplate template,
			String title)
		throws Exception, PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		Map<Locale, String> titleMap = Collections.singletonMap(
			LocaleThreadLocal.getSiteDefaultLocale(), title);

		JournalArticle journalArticle =
			JournalArticleLocalServiceUtil.addArticle(
				TestPropsValues.getUserId(), group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, titleMap,
				Collections.emptyMap(), _getJournalArticleContent(),
				ddmStructure.getStructureKey(), template.getTemplateKey(),
				serviceContext);

		_journalArticles.add(journalArticle);

		return journalArticle;
	}

	protected static final int NUMBER_OF_SAMPLES = 100;

	@Inject(filter = "ddm.form.deserializer.type=json")
	protected DDMFormDeserializer ddmFormDeserializer;

	protected DDMStructureIndexerFixture indexerFixture;

	private String _getJournalArticleContent() throws Exception {
		if (_journalArticleContent == null) {
			_journalArticleContent = getResourceAsString(
				"com/liferay/journal/content" +
					"/dynamic-data-mapping-journal-article-content.xml",
				getClass());
		}

		return _journalArticleContent;
	}

	@DeleteAfterTestRun
	private final List<DDMTemplate> _ddmTemplates = new ArrayList<>(1);

	private String _journalArticleContent;

	@DeleteAfterTestRun
	private final List<JournalArticle> _journalArticles = new ArrayList<>(
		NUMBER_OF_SAMPLES);

}