package org.jboss.fuse.qe.sftp.command;

import org.jboss.fuse.qe.sftp.service.SftpService;

import java.io.IOException;

import io.quarkus.runtime.Quarkus;
import picocli.CommandLine;

@CommandLine.Command(name = "start", description = "Start the server")
public class StartServerCommand implements Runnable {

	@CommandLine.Option(names = {"-h", "--host"}, description = "the listening host", defaultValue = "0.0.0.0")
	String host;

	@CommandLine.Option(names = {"-p", "--port"}, description = "the listening port number", defaultValue = "22")
	int port;

	@CommandLine.Option(names = {"-u", "--user"}, description = "the username for the authentication", defaultValue = "test")
	String username;

	@CommandLine.Option(names = {"-pass", "--password"}, description = "the password for the authentication", defaultValue = "test")
	String password;

	private final SftpService sftpService;

	public StartServerCommand(SftpService sftpService) {
		this.sftpService = sftpService;
	}

	@Override
	public void run() {
		try {
			sftpService.startServer(host, port, username, password);
			Quarkus.waitForExit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
