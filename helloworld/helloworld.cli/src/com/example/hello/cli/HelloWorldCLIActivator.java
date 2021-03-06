package com.example.hello.cli;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

public class HelloWorldCLIActivator implements BundleActivator {

	private HelloWorldCLI cli;
	private ServiceRegistration reg;

	public void start(BundleContext context) throws Exception {
		cli = new HelloWorldCLI();
		cli.start(context);

		Properties props = new Properties();
		props.put("osgi.command.scope", HelloWorldCLI.SCOPE);
		props.put("osgi.command.function", HelloWorldCLI.FUNCTIONS);
		props.put("service.pid", "com.example.hello.cli");

		reg = context.registerService(new String[] {
				HelloWorldCLI.class.getName(),
				ManagedService.class.getName()
		}, cli, props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		reg.unregister();
		cli.stop();
	}

}
