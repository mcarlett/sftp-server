package org.jboss.fuse.qe.sftp.service;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;

@Dependent
public class SftpService {
	private static final Logger LOG = Logger.getLogger(SftpService.class);
	public void startServer(String host, int port, String username, String password) throws IOException {
		final SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setHost(host);
		sshd.setPort(port);
		final Path keyFile = Files.createTempFile("", ".key");
		keyFile.toFile().deleteOnExit();
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(keyFile.toAbsolutePath()));
		sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
		sshd.setPasswordAuthenticator((u, p, session) -> u.equals(username) && p.equals(password));
		sshd.start();
		LOG.info("Server listening on " + sshd.getBoundAddresses().stream().map(Object::toString).collect(Collectors.toList()));
	}
}
