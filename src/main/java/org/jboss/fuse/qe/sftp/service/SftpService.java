package org.jboss.fuse.qe.sftp.service;

import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Dependent
public class SftpService {
	private static final Logger LOG = Logger.getLogger(SftpService.class);

	public SshServer startServer(String host, int port, String username, String password, String homeDirectory) throws IOException {

		final Path home = Paths.get(homeDirectory);
		LOG.info("Server home directory: " + home.toAbsolutePath());
		final SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setHost(host);
		sshd.setPort(port);
		final Path keyFile = Files.createTempFile("", ".key");
		keyFile.toFile().deleteOnExit();
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(keyFile.toAbsolutePath()));
		sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory.Builder()
				.build()));
		sshd.setPasswordAuthenticator((u, p, session) -> u.equals(username) && p.equals(password));
		final NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory(true);
		fileSystemFactory.setUsersHomeDir(homeDirectory);
		sshd.setFileSystemFactory(fileSystemFactory);
		sshd.start();

		LOG.info("Server listening on " + getListeningAddresses(sshd));
		return sshd;
	}

	public void stopServer(SshServer sshd) throws IOException {
		LOG.info("Stopping server " + getListeningAddresses(sshd));
		sshd.stop();
	}

	private List<String> getListeningAddresses(SshServer sshd) {
		return sshd.getBoundAddresses().stream().map(Object::toString).collect(Collectors.toList());
	}
}
