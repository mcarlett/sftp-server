package org.jboss.fuse.qe.sftp.command;

import org.apache.sshd.server.SshServer;

import org.jboss.fuse.qe.sftp.config.MicroprofileConfigDefaultValueProvider;
import org.jboss.fuse.qe.sftp.service.SftpService;

import java.io.IOException;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.Quarkus;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(name = "start", description = "Start the server", defaultValueProvider = MicroprofileConfigDefaultValueProvider.class)
public class StartServerCommand implements Runnable {

	@CommandLine.Option(names = {"-h", "--host"}, description = "the listening host", defaultValue = "sftp.server.host")
	String host;

	@CommandLine.Option(names = {"-p", "--port"}, description = "the listening port number", defaultValue = "sftp.server.port")
	int port;

	@CommandLine.Option(names = {"-u", "--user"}, description = "the username for the authentication", defaultValue = "sftp.server.username")
	String username;

	@CommandLine.Option(names = {"-pass", "--password"}, description = "the password for the authentication", defaultValue = "sftp.server.password")
	String password;

	@CommandLine.Option(names = {"-home"}, description = "the home directory to use, will be created if not exists", defaultValue = "sftp.server.home")
	String home;

	private final SftpService sftpService;

	public StartServerCommand(SftpService sftpService) {
		this.sftpService = sftpService;
	}

	@Override
	public void run() {
		try {
			final SshServer sshd = sftpService.startServer(host, port, username, password, home);
			Quarkus.waitForExit();
			sftpService.stopServer(sshd);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
