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

package com.liferay.layout.search.test;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Vagner B.C
 */
public class LayoutFixture {

	public static final String LAYOUT_TYPE_CONTENT = "content";

	public LayoutFixture(Group group) {
		_group = group;
	}

	public Layout createLayout() {
		return createLayout(RandomTestUtil.randomString());
	}

	public Layout createLayout(String name) {
		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			String friendlyURL =
				StringPool.SLASH +
					FriendlyURLNormalizerUtil.normalize(
						RandomTestUtil.randomString());

			Layout layout = LayoutLocalServiceUtil.addLayout(
				TestPropsValues.getUserId(), _group.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, null,
				RandomTestUtil.randomString(), LayoutConstants.TYPE_PORTLET,
				false, friendlyURL, serviceContext);

			layout.setType(LAYOUT_TYPE_CONTENT);

			LayoutLocalServiceUtil.updateLayout(layout);

			_layouts.add(layout);

			return layout;
		}
		catch (PortalException e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<Layout> getLayouts() {
		return _layouts;
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	private final Group _group;
	private final List<Layout> _layouts = new ArrayList<>();

}