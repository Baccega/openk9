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

package com.openk9.ingestion.rabbitmq.factory;

import com.openk9.ingestion.api.OutboundMessage;
import com.openk9.ingestion.api.OutboundMessageFactory;
import com.openk9.ingestion.rabbitmq.wrapper.OutboundMessageWrapper;
import org.osgi.service.component.annotations.Component;

import java.util.Objects;
import java.util.function.Function;

@Component(immediate = true, service = OutboundMessageFactory.class)
public class OutboundMessageFactoryImpl implements OutboundMessageFactory {
	@Override
	public OutboundMessage createOutboundMessage(
		Function<OutboundMessage.Builder, OutboundMessage.Builder> function) {

		Objects.requireNonNull(function, "function is null");

		return function.apply(
			new OutboundMessageWrapper.BuilderWrapper()).build();
	}

	@Override
	public OutboundMessage createOutboundMessage(
		String exchange, String routingKey, byte[] body) {

		return new OutboundMessageWrapper(
			new reactor.rabbitmq.OutboundMessage(exchange, routingKey, body));
	}
}
