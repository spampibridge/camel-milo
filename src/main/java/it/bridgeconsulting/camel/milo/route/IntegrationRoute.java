package it.bridgeconsulting.camel.milo.route;

import java.util.Arrays;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.milo.MiloConstants;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.springframework.stereotype.Component;

@Component
public class IntegrationRoute extends EndpointRouteBuilder {

	public static final String TIMER_POLLER = "timerPoller";
	private static final String NODE_ID = "nsu=http://microsoft.com/Opc/OpcPlc/;s=RandomSignedInt32";
	
	@Override
	public void configure() throws Exception {
		from(timer(TIMER_POLLER)
			.period("1000"))
			.routeId(TIMER_POLLER)
			.setHeader(MiloConstants.HEADER_NODE_IDS, constant(Arrays.asList(NODE_ID)))
			.setHeader(MiloConstants.HEADER_AWAIT, constant(true))
			.enrich(miloClient("opc.tcp://localhost:50000")
				.overrideHost(true)
				.allowedSecurityPolicies(SecurityPolicy.Basic256Sha256.name())
				.keyStoreUrl("classpath:user.jks")
				.keyStorePassword("12345678")
				.keyPassword("12345678")
				.applicationUri("urn:myorg:opcua:my-app"))
			.log(">> Message from OPCUA ${body}");
	}
}
