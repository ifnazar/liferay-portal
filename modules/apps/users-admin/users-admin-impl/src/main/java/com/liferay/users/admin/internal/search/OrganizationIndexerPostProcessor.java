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

package com.liferay.users.admin.internal.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.model.Organization",
	service = IndexerPostProcessor.class
)
public class OrganizationIndexerPostProcessor implements IndexerPostProcessor {

	@Override
	@SuppressWarnings("unchecked")
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params == null) {
			return;
		}

		List<Long> excludedOrganizationIds = (List<Long>)params.get(
			"excludedOrganizationIds");

		if (ListUtil.isNotEmpty(excludedOrganizationIds)) {
			TermsFilter termsFilter = new TermsFilter("organizationId");

			termsFilter.addValues(
				ArrayUtil.toStringArray(
					excludedOrganizationIds.toArray(
						new Long[excludedOrganizationIds.size()])));

			contextBooleanFilter.add(termsFilter, BooleanClauseOccur.MUST_NOT);
		}

		List<Organization> organizationsTree = (List<Organization>)params.get(
			"organizationsTree");

		if (organizationsTree != null) {
			BooleanFilter booleanFilter = new BooleanFilter();

			if (organizationsTree.isEmpty()) {
				TermQuery termQuery = new TermQueryImpl(
					Field.TREE_PATH, StringPool.BLANK);

				booleanFilter.add(new QueryFilter(termQuery));
			}

			for (Organization organization : organizationsTree) {
				String treePath = organization.buildTreePath();

				WildcardQuery wildcardQuery = new WildcardQueryImpl(
					Field.TREE_PATH, treePath);

				booleanFilter.add(new QueryFilter(wildcardQuery));
			}

			contextBooleanFilter.add(booleanFilter, BooleanClauseOccur.MUST);
		}
		else {
			long parentOrganizationId = GetterUtil.getLong(
				searchContext.getAttribute("parentOrganizationId"));

			if (parentOrganizationId !=
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID) {

				contextBooleanFilter.addRequiredTerm(
					"parentOrganizationId", parentOrganizationId);
			}
		}
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		System.out.println("postProcessContextQuery");
	}

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {

		System.out.println("postProcessDocument");
	}

	@Override
	public void postProcessFullQuery(
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

		System.out.println("postProcessFullQuery");
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter booleanFilter,
			SearchContext searchContext)
		throws Exception {

		System.out.println("postProcessSearchQuery");
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		System.out.println("postProcessSearchQuery");
	}

	@Override
	public void postProcessSummary(
		Summary summary, Document document, Locale locale, String snippet) {

		System.out.println("postProcessSummary");
	}

}