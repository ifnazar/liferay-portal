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

package com.liferay.polls.service.permission;

import com.liferay.polls.model.PollsQuestion;
import com.liferay.polls.service.PollsQuestionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of 4.0.0, with no direct replacement
 */
@Component(
	immediate = true,
	property = {"model.class.name=com.liferay.polls.model.PollsQuestion"}
)
@Deprecated
public class PollsQuestionPermissionChecker
	implements BaseModelPermissionChecker {

	public static void check(
			PermissionChecker permissionChecker, long questionId,
			String actionId)
		throws PortalException {

		_pollsQuestionModelResourcePermission.check(
			permissionChecker, questionId, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, PollsQuestion question,
			String actionId)
		throws PortalException {

		_pollsQuestionModelResourcePermission.check(
			permissionChecker, question, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long questionId,
			String actionId)
		throws PortalException {

		return _pollsQuestionModelResourcePermission.contains(
			permissionChecker, questionId, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, PollsQuestion question,
			String actionId)
		throws PortalException {

		return _pollsQuestionModelResourcePermission.contains(
			permissionChecker, question, actionId);
	}

	@Override
	public void checkBaseModel(
			PermissionChecker permissionChecker, long groupId, long primaryKey,
			String actionId)
		throws PortalException {

		_pollsQuestionModelResourcePermission.check(
			permissionChecker, primaryKey, actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.polls.model.PollsQuestion)",
		unbind = "-"
	)
	protected void setModelResourcePermission(
		ModelResourcePermission<PollsQuestion> modelResourcePermission) {

		_pollsQuestionModelResourcePermission = modelResourcePermission;
	}

	protected void setPollsQuestionLocalService(
		PollsQuestionLocalService pollsQuestionLocalService) {
	}

	private static ModelResourcePermission<PollsQuestion>
		_pollsQuestionModelResourcePermission;

}