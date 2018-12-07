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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Marques de Paula
 */
@RunWith(Arquillian.class)
public final class JournalArticleDDMStructureIndexerTest
	extends BaseJournalArticleDDMStructureIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testDisableJournalArticleIndexer()
		throws Exception, PortalException {

		Group group = addGroup();

		DDMStructure ddmStructure = addDDMStructure(
			group, JournalArticle.class);

		DDMTemplate template = addDDMTemplate(group, ddmStructure);

		String keywords = "私はプログラミングが大好きです";

		for (int i = 1; i <= NUMBER_OF_SAMPLES; i++) {
			String title = String.format("%s - %02d", keywords, i);

			addJournalArticle(group, ddmStructure, template, title);
		}

		Hits hits = indexerFixture.search(keywords);

		assertHitsLength(NUMBER_OF_SAMPLES, hits);

		indexerFixture.deleteDocuments(hits.getDocs());

		indexerFixture.setIsEnable(false);

		runBackgroundTaskReindex(ddmStructure);

		assertHitsLength(0, indexerFixture.search(keywords));
	}

	@Test
	public void testReindexJournalArticle() throws Exception, PortalException {
		Group group = addGroup();

		DDMStructure ddmStructure = addDDMStructure(
			group, JournalArticle.class);

		DDMTemplate template = addDDMTemplate(group, ddmStructure);

		String keywords = "私はプログラミングが大好きです";

		for (int i = 1; i <= NUMBER_OF_SAMPLES; i++) {
			String title = String.format("%s - %02d", keywords, i);

			addJournalArticle(group, ddmStructure, template, title);
		}

		Hits hits = indexerFixture.search(keywords);

		assertHitsLength(NUMBER_OF_SAMPLES, hits);

		indexerFixture.deleteDocuments(hits.getDocs());

		assertHitsLength(0, indexerFixture.search(keywords));

		runBackgroundTaskReindex(ddmStructure);

		assertHitsLength(NUMBER_OF_SAMPLES, indexerFixture.search(keywords));
	}

	protected void assertHitsLength(int expected, Hits hits) {
		int actual = hits.getLength();

		String message = String.format(
			"Hits length expected:<%d> but was:<%d>", expected, actual);

		Assert.assertEquals(message, expected, actual);
	}

}