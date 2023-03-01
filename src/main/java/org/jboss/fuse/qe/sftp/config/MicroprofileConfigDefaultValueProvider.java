package org.jboss.fuse.qe.sftp.config;

import org.jboss.logging.Logger;

import org.eclipse.microprofile.config.ConfigProvider;

import java.util.NoSuchElementException;
import java.util.Optional;

import picocli.CommandLine;

public class MicroprofileConfigDefaultValueProvider implements CommandLine.IDefaultValueProvider {

	private static final Logger LOG = Logger.getLogger(MicroprofileConfigDefaultValueProvider.class);

	@Override
	public String defaultValue(CommandLine.Model.ArgSpec argSpec) throws Exception {
		return Optional.ofNullable(argSpec.defaultValue())
				.map(defaultPropKey -> {
					try {
						return ConfigProvider.getConfig().getValue(defaultPropKey, String.class);
					} catch (NoSuchElementException e) {
						LOG.warn(e.getMessage());
						LOG.warn(String.format("the value '%s' will be used instead", defaultPropKey));
						return defaultPropKey;
					}
				})
				.orElse(null);
	}
}
