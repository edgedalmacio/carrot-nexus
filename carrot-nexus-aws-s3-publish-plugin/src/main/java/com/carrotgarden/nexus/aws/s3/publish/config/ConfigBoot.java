/**
 * Copyright (C) 2010-2012 Andrei Pozolotin <Andrei.Pozolotin@gmail.com>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.carrotgarden.nexus.aws.s3.publish.config;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.plugins.capabilities.CapabilityReference;
import org.sonatype.nexus.plugins.capabilities.CapabilityRegistry;
import org.sonatype.nexus.plugins.capabilities.CapabilityRegistryEvent;
import org.sonatype.nexus.plugins.capabilities.CapabilityType;
import org.sonatype.nexus.plugins.capabilities.support.CapabilityReferenceFilterBuilder;
import org.sonatype.sisu.goodies.eventbus.EventBus;

import com.google.common.eventbus.Subscribe;

/**
 * provide default configuration
 */
@Named
@Singleton
public class ConfigBoot implements EventBus.LoadOnStart {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Subscribe
	public void handle(final CapabilityRegistryEvent.AfterLoad event) {

		final CapabilityRegistry registry = event.getEventSender();

		try {

			final CapabilityType type = ConfigBean.TYPE;

			if (hasNoReference(registry, type)) {

				log.info("provide default capability type={}", type);

				registry.add(type, false, "default", ConfigBean.defaultProps());

			}

		} catch (final Exception e) {
			throw new RuntimeException("default capability failure", e);
		}

	}

	public static boolean hasNoReference(final CapabilityRegistry registry,
			final CapabilityType type) {

		final CapabilityReference reference = referenceFrom(registry, type);

		return reference == null;

	}

	public static CapabilityReference referenceFrom(
			final CapabilityRegistry registry, final CapabilityType type) {

		final CapabilityReferenceFilterBuilder.CapabilityReferenceFilter filter = //
		CapabilityReferenceFilterBuilder.capabilities().withType(type);

		@SuppressWarnings("unchecked")
		final Collection<CapabilityReference> capabilities = //
		(Collection<CapabilityReference>) registry.get(filter);

		if (capabilities == null || capabilities.isEmpty()) {
			return null;
		} else {
			return capabilities.iterator().next();
		}

	}

}