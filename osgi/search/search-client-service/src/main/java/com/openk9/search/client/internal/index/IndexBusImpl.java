/*
 * Copyright (c) 2020-present SMC Treviso s.r.l. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.openk9.search.client.internal.index;

import com.openk9.search.client.api.DocWriteRequestFactory;
import com.openk9.search.client.api.IndexBus;
import com.openk9.search.client.internal.ElasticSearchIndexer;
import org.elasticsearch.action.DocWriteRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.function.Function;

@Component(service = IndexBus.class)
public class IndexBusImpl implements IndexBus {

	@Override
	public void sendRequest(DocWriteRequest<?> request) {
		_elasticSearchIndexer.sendDocWriteRequest(request);
	}

	@Override
	public void sendRequest(
		Function<DocWriteRequestFactory, DocWriteRequest<?>> requestFunction) {

		sendRequest(requestFunction.apply(_docWriteRequestFactory));
	}

	@Reference
	private ElasticSearchIndexer _elasticSearchIndexer;

	@Reference
	private DocWriteRequestFactory _docWriteRequestFactory;

}
