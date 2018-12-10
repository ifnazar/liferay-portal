AUI.add(
	'liferay-search-filter-util',
	function(A) {
		var FacetUtil = {
			addURLParameter: function(key, value, parameterArray) {
				console.log(">>>> addURLParameter");
				key = encodeURIComponent(key);
				value = encodeURIComponent(value);

				parameterArray[parameterArray.length] = [key, value].join('=');

				return parameterArray;
			},

			changeSelection: function(event) {
				console.log(">>>> changeSelection");
				var form = event.currentTarget.form;
				console.log('------------ 1');

				if (!form) {
					return;
				}
				console.log('------------ 2');

				var selections = [];

				console.log('$$$$');
				console.log('#' + form.id + ' input.filter-keyword');
				console.log('$$$$');
				
				var formInput = $('#' + form.id + ' input.filter-keyword');
				console.log('------------ 3');
				console.log(formInput);
				console.log(formInput.val());
				selections.push(formInput.val());
				
				

//				formInput.each(
//					function(index, value) {
							console.log('------------ formInput.each');
//							console.log(value);
//							console.log((value.getAttribute('value'));
//							console.log('------------');
//							selections.push(value.value);
//					}
//				);
				
				console.log('------------ 4');
				console.log(selections);
				

				FacetUtil.selectTerms(form, selections);
			},

			clearSelections: function(event) {
				console.log(">>>> clearSelections");
				var form = $(event.currentTarget).closest('form')[0];

				if (!form) {
					return;
				}

				var selections = [];

				FacetUtil.selectTerms(form, selections);
			},

			removeURLParameters: function(key, parameterArray) {
				console.log(">>>> removeURLParameters");
				key = encodeURIComponent(key);

				var newParameters = parameterArray.filter(
					function(item) {
						var itemSplit = item.split('=');

						if (itemSplit && (itemSplit[0] === key)) {
							return false;
						}

						return true;
					}
				);

				return newParameters;
			},

			selectTerms: function(form, selections) {
				console.log(">>>> selectTerms");
				var formParameterName = $('#' + form.id + ' input.filter-parameter-name');
				console.log(":: formParameterName: " + formParameterName);
				console.log(":: selections: " + selections);
				
				var key = formParameterName[0].value;

				
				var newXXX = FacetUtil.updateQueryString(key, selections, document.location.search)
				console.log("@@@@@@@@@@@@@@@@@");
				console.log(newXXX);
				console.log("@@@@@@@@@@@@@@@@@");
				
				document.location.search = FacetUtil.updateQueryString(key, selections, document.location.search);
			},

			setURLParameter: function(url, name, value) {
				console.log(">>>> setURLParameter");
				
				var parts = url.split('?');

				var address = parts[0];

				var queryString = parts[1];

				if (!queryString) {
					queryString = '';
				}

				queryString = Liferay.Search.FacetUtil.updateQueryString(name, [value], queryString);

				return address + '?' + queryString;
			},

			setURLParameters: function(key, values, parameterArray) {
				console.log(">>>> setURLParameters");
				var newParameters = FacetUtil.removeURLParameters(key, parameterArray);

				values.forEach(
					function(item) {
						newParameters = FacetUtil.addURLParameter(key, item, newParameters);
					}
				);

				return newParameters;
			},

			updateQueryString: function(key, selections, queryString) {
				console.log(">>>> updateQueryString");
				var search = queryString;

				var hasQuestionMark = false;

				if (search[0] === '?') {
					hasQuestionMark = true;
				}

				if (hasQuestionMark) {
					search = search.substr(1);
				}

				var parameterArray = search.split('&').filter(
					function(item) {
						return item.trim() !== '';
					}
				);

				var newParameters = FacetUtil.setURLParameters(key, selections, parameterArray);

				search = newParameters.join('&');

				if (hasQuestionMark) {
					search = '?' + search;
				}

				return search;
			}
		};

		Liferay.namespace('Search').FacetUtil = FacetUtil;
	},
	'',
	{
		requires: []
	}
);