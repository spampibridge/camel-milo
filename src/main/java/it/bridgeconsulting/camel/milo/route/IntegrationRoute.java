package it.bridgeconsulting.camel.milo.route;

import java.util.Arrays;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.milo.MiloConstants;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.springframework.stereotype.Component;

@Component
public class IntegrationRoute extends EndpointRouteBuilder {

	public static final String TIMER_POLLER = "timerPoller";
	
	@Override
	public void configure() throws Exception {
		from(timer(TIMER_POLLER)
			.period("1000"))
			.routeId(TIMER_POLLER)
			.setHeader(MiloConstants.HEADER_NODE_IDS, constant(Arrays.asList("ns=3;i=1002")))
			.setHeader(MiloConstants.HEADER_AWAIT, constant(true))
			.enrich(miloClient("opc.tcp://localhost:53530/OPCUA/SimulationServer")
				.allowedSecurityPolicies(SecurityPolicy.Basic256Sha256.name())
				.keyStoreUrl("classpath:keystore.jks")
				.keyStorePassword("12345678")
				.keyAlias("client1")
				.keyPassword("12345678")
				.applicationUri("urn:myorg:opcua:my-app"))
			.log(">> Message from OPCUA ${body}");
	}
}
