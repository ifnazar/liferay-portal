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

package com.liferay.bulk.selection.test.util;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	service = {BulkSelectionFactory.class, TestBulkSelectionFactory.class}
)
public class TestBulkSelectionFactory implements BulkSelectionFactory<Integer> {

	@Override
	public BulkSelection<Integer> create(Map<String, String[]> parameterMap) {
		String[] integers = parameterMap.get("integers");

		return new BulkSelection<Integer>() {

			@Override
			public String describe(Locale locale) {
				return "Test Integer Bulk Selection";
			}

			@Override
			public Class<? extends BulkSelectionFactory>
				getBulkSelectionFactoryClass() {

				return TestBulkSelectionFactory.class;
			}

			@Override
			public Map<String, String[]> getParameterMap() {
				return parameterMap;
			}

			@Override
			public boolean isMultiple() {
				return true;
			}

			@Override
			public Serializable serialize() {
				return StringUtil.merge(integers, StringPool.COMMA);
			}

			@Override
			public Stream<Integer> stream() {
				return Arrays.stream(
					integers
				).map(
					Integer::new
				);
			}

		};
	}

}