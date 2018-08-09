package com.liferay.users.admin.indexer.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

/**
 * @author Lucas Marques
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class OrganizationIndexerIndexedFieldsExpandoTest extends BaseOrganizationIndexerTestCase {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		setGroup(organizationFixture.addGroup());
		setIndexerClass(Organization.class);
	}

	@Test
	public void testIndexedFields() throws Exception {
		organizationFixture.updateDisplaySettings(LocaleUtil.JAPAN);

		String organizationName = "新規作成";
		String countryName = "united-states";
		String regionName = "Alabama";
		String expandoColumnObs = "expandoColumnObs";
		String expandoColumnValue = "Software Engineer";
		String expandoColumnName = "expandoColumnName";
		String expandoColumnValue2 = "Software Developer";
		
		List<String> expandoColumns  = new ArrayList<String>();
		expandoColumns.add(expandoColumnObs);
		expandoColumns.add(expandoColumnName);
				
		addExpandoColumn(
				Organization.class, expandoColumns,
				ExpandoColumnConstants.INDEX_TYPE_KEYWORD);

		Map<String, Serializable> expandoValues = new HashMap<>();
		expandoValues.put(expandoColumnObs, expandoColumnValue);
		expandoValues.put(expandoColumnName, expandoColumnValue2);
		
		Organization organization = organizationFixture.createAnOrganization(
			organizationName, countryName, regionName, expandoValues);

		String searchTerm = "Developer";

		Document document = organizationSearchFixture.searchOnlyOne(
			searchTerm, LocaleUtil.JAPAN);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = expectedFieldValues(organization);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}



	protected Map<String, String> expectedFieldValues(Organization organization)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		String portletUID =
			Organization.class.getName() + "_PORTLET_" +
				organization.getOrganizationId();
		String countryName = getCountryNameForAllAvailableLocales(organization);

		Region region = regionService.getRegion(organization.getRegionId());

		String regionName = StringUtil.toLowerCase(region.getName());

		map.put("country", countryName);
		map.put("region", regionName);
		map.put(
			Field.NAME + "_sortable",
			StringUtil.toLowerCase(organization.getName()));
		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(organization.getOrganizationId()));
		map.put(
			"nameTreePath_String_sortable",
			StringUtil.toLowerCase(organization.getName()));
		map.put(Field.ENTRY_CLASS_NAME, Organization.class.getName());
		map.put(
			Field.USER_NAME,
			StringUtil.toLowerCase(organization.getUserName()));
		map.put(
			"parentOrganizationId",
			String.valueOf(organization.getParentOrganizationId()));
		map.put(
			Field.ORGANIZATION_ID,
			String.valueOf(organization.getOrganizationId()));
		map.put(Field.USER_ID, String.valueOf(organization.getUserId()));
		map.put(Field.COMPANY_ID, String.valueOf(organization.getCompanyId()));
		map.put("nameTreePath", organization.getName());
		map.put(Field.NAME, organization.getName());
		map.put(Field.TREE_PATH, organization.getTreePath());
		map.put(Field.UID, portletUID);
		map.put(Field.TYPE, organization.getType());

		map.put(
			"expando__keyword__custom_fields__expandoColumnObs",
			"Software Engineer");
		map.put(
				"expando__keyword__custom_fields__expandoColumnName",
				"Software Developer");

		_populateDates(organization, map);
		// _populateRoles(organization, map);

		return map;
	}

	private void _populateDates(
		Organization organization, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, organization.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, organization.getModifiedDate(), map);
	}

	private void _populateRoles(
			Organization organization, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			organization.getCompanyId(), Organization.class.getName(),
			organization.getOrganizationId(), organization.getGroupId(), null,
			map);
	}

}
