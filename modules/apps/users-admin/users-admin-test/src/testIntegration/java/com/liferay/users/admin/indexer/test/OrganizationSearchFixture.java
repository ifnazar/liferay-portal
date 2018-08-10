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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.test.util.HitsAssert;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public class OrganizationSearchFixture {

	public OrganizationSearchFixture(IndexerRegistry indexerRegistry) {
		_indexerRegistry = indexerRegistry;
	}

	public SearchContext getSearchContext(String keywords, Locale locale)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setKeywords(keywords);
		searchContext.setLocale(Objects.requireNonNull(locale));

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	public Document searchOnlyOne(String keywords, Locale locale)
		throws Exception {

		Hits search = search(getSearchContext(keywords, locale));

		return HitsAssert.assertOnlyOne(search);
	}
	
	protected void reindex(Organization organization) throws Exception {
		_indexer.reindex(new String[] {String.valueOf(organization.getCompanyId())});
	}

	public void setIndexerClass(Class<?> clazz) {
		_indexer = _indexerRegistry.getIndexer(clazz);
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		return _indexer.search(searchContext);
	}

	private Indexer<?> _indexer;
	private final IndexerRegistry _indexerRegistry;

}