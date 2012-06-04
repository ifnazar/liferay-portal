/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portalweb.portal.controlpanel.users.user.edituserorgrole;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class EditUserOrgRoleTest extends BaseTestCase {
	public void testEditUserOrgRole() throws Exception {
		selenium.open("/web/guest/home/");
		loadRequiredJavaScriptModules();
		assertEquals(RuntimeVariables.replace("Go to"),
			selenium.getText("//li[@id='_145_mySites']/a/span"));
		selenium.mouseOver("//li[@id='_145_mySites']/a/span");

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible("link=Control Panel")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=Control Panel",
			RuntimeVariables.replace("Control Panel"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		selenium.clickAt("link=Users and Organizations",
			RuntimeVariables.replace("Users and Organizations"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		selenium.clickAt("link=Search All Users",
			RuntimeVariables.replace("Search All Users"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		selenium.type("//input[@name='_125_keywords']",
			RuntimeVariables.replace("usersn"));
		selenium.clickAt("//input[@value='Search']",
			RuntimeVariables.replace("Search"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		assertEquals(RuntimeVariables.replace("userfn"),
			selenium.getText("//tr[contains(.,'usersn')]/td[2]"));
		assertEquals(RuntimeVariables.replace("userln"),
			selenium.getText("//tr[contains(.,'usersn')]/td[3]"));
		assertEquals(RuntimeVariables.replace("usersn"),
			selenium.getText("//tr[contains(.,'usersn')]/td[4]"));
		assertEquals(RuntimeVariables.replace("Organization Name"),
			selenium.getText("//tr[contains(.,'usersn')]/td[6]"));
		assertEquals(RuntimeVariables.replace("Actions"),
			selenium.getText(
				"//tr[contains(.,'usersn')]/td/span[@title='Actions']/ul/li/strong/a"));
		selenium.clickAt("//tr[contains(.,'usersn')]/td/span[@title='Actions']/ul/li/strong/a",
			RuntimeVariables.replace("Actions"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible(
							"//div[@class='lfr-component lfr-menu-list']/ul/li/a[contains(.,'Edit')]")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Edit"),
			selenium.getText(
				"//div[@class='lfr-component lfr-menu-list']/ul/li/a[contains(.,'Edit')]"));
		selenium.click(RuntimeVariables.replace(
				"//div[@class='lfr-component lfr-menu-list']/ul/li/a[contains(.,'Edit')]"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		assertTrue(selenium.isPartialText("//a[@id='_125_rolesLink']", "Roles"));
		selenium.clickAt("//a[@id='_125_rolesLink']",
			RuntimeVariables.replace("Roles"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible(
							"//span[contains(.,'Select')]/a[contains(@href,'openOrganizationRoleSelector')]")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Select"),
			selenium.getText(
				"//span[contains(.,'Select')]/a[contains(@href,'openOrganizationRoleSelector')]"));
		selenium.clickAt("//span[contains(.,'Select')]/a[contains(@href,'openOrganizationRoleSelector')]",
			RuntimeVariables.replace("Select"));
		Thread.sleep(5000);
		selenium.waitForPopUp("role", RuntimeVariables.replace("30000"));
		selenium.selectWindow("title=Users and Organizations");

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible(
							"//tr[contains(.,'Roles Orgrole Name')]/td[1]/a")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Roles Orgrole Name"),
			selenium.getText("//tr[contains(.,'Roles Orgrole Name')]/td[1]/a"));
		selenium.clickAt("//tr[contains(.,'Roles Orgrole Name')]/td[1]/a",
			RuntimeVariables.replace("Roles Orgrole Name"));
		selenium.selectWindow("null");

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isPartialText(
							"//div[@id='_125_organizationRolesSearchContainer']",
							"Roles Orgrole Name")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertTrue(selenium.isPartialText(
				"//div[@id='_125_organizationRolesSearchContainer']",
				"Roles Orgrole Name"));
		selenium.clickAt("//input[@value='Save']",
			RuntimeVariables.replace("Save"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		assertEquals(RuntimeVariables.replace(
				"Your request completed successfully."),
			selenium.getText("//div[@class='portlet-msg-success']"));
		assertEquals(RuntimeVariables.replace("Roles Orgrole Name"),
			selenium.getText(
				"//div[@id='_125_organizationRolesSearchContainerSearchContainer']//tr[contains(.,'Roles Orgrole Name')]/td[1]"));
		assertEquals(RuntimeVariables.replace("Organization Name"),
			selenium.getText(
				"//div[@id='_125_organizationRolesSearchContainerSearchContainer']//tr[contains(.,'Roles Orgrole Name')]/td[2]"));
		assertEquals(RuntimeVariables.replace("Remove"),
			selenium.getText(
				"//div[@id='_125_organizationRolesSearchContainerSearchContainer']//tr[contains(.,'Roles Orgrole Name')]/td[3]/a"));
	}
}