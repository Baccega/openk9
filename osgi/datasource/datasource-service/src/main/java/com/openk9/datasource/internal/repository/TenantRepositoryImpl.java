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

package com.openk9.datasource.internal.repository;


import com.openk9.datasource.model.Datasource;
import com.openk9.datasource.model.Tenant;
import com.openk9.datasource.repository.TenantRepository;
import com.openk9.repository.http.api.RepositoryHttpExtender;
import com.openk9.sql.api.client.Criteria;
import com.openk9.sql.api.client.DatabaseClient;
import com.openk9.sql.api.entity.BaseReactiveRepository;
import com.openk9.sql.api.entity.EntityMapper;
import com.openk9.sql.api.entity.ReactiveRepository;
import com.openk9.sql.api.event.EntityEventBus;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Component(
	immediate = true,
	service = {
		TenantRepository.class,
		RepositoryHttpExtender.class
	}
)
public class TenantRepositoryImpl
	extends BaseReactiveRepository<Tenant, Long>
	implements TenantRepository, RepositoryHttpExtender {

	@Override
	public ReactiveRepository getReactiveRepository() {
		return this;
	}

	public Mono<Tenant> removeTenant(Long tenantId) {
		return super.delete(tenantId);
	}
	public Mono<Tenant> findByVirtualHost(String virtualHost) {
		return super.findOneBy(Criteria.where("virtualHost").is(virtualHost));
	}
	public Mono<Tenant> addTenant(Tenant tenant) {
        return super.insert(tenant);
	}
	public Mono<Tenant> addTenant(String name, String virtualHost) {
		return super.insert(
			Tenant
				.builder()
				.name(name)
				.virtualHost(virtualHost)
				.build()
		);
	}
	public Mono<Tenant> updateTenant(Tenant tenant) {
		return super.update(tenant);
	}
	public Mono<Tenant> updateTenant(
		String name, Long tenantId, String virtualHost) {

		return super.update(
			Tenant.of(tenantId, name, virtualHost)
		);
	}

	public Mono<Tenant> removeTenant(Tenant tenant) {
        return super.delete(tenant.getTenantId());
	}

	@Override
	public Long parsePrimaryKey(String primaryKey) {
		return Long.valueOf(primaryKey);
	}

	@Override
	public Class<Tenant> entityClass() {
		return Tenant.class;
	}

	@Override
	public Class<Long> primaryKeyType() {
		return Long.class;
	}

	@Override
	public String primaryKeyName() {
		return "tenantId";
	}

	@Override
	public String tableName() {
		return TABLE_NAME;
	}

	@Override
	public Long getPrimaryKey(Tenant tenant) {
		return tenant.getTenantId();
	}

	@Override
	public BiFunction<Row, RowMetadata, Tenant> entityMapping() {
		return (row, rowMetadata) -> {

			Long tenantId = row.get("tenantId", Long.class);
			String name = row.get("name", String.class);
			String virtualHost = row.get("virtualHost", String.class);

			return Tenant.of(tenantId, name, virtualHost);
		};
	}

	@Reference
	public void setDatabaseClient(DatabaseClient databaseClient) {
		_databaseClient = databaseClient;
	}

	@Reference(
		target = "(|" +
				 "(entity.mapper=com.openk9.datasource.model.Tenant)" +
				 "(entity.mapper=default)" +
				 ")",
		service = EntityMapper.class,
		policyOption = ReferencePolicyOption.GREEDY,
		bind = "setEntityMapper"
	)
	public void setEntityMapper(EntityMapper entityMapper) {
		_updateMapper = entityMapper.toMap(Datasource.class);
		_insertMapper = entityMapper.toMapWithoutPK(Datasource.class);
	}

	@Reference
	public void setEntityEventBus(EntityEventBus entityEventBus) {
		_entityEventBus = entityEventBus;
	}

	public static final String TABLE_NAME = "TENANT";

}

