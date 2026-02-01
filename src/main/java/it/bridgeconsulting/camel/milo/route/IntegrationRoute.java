package it.bridgeconsulting.camel.milo.route;

import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.springframework.stereotype.Component;

@Component
public class IntegrationRoute extends EndpointRouteBuilder {

	public static final String OPCUA_CONSUMER_ROUTE = "opcUaConsumerRoute";
	private static final String NODE_ID = "nsu=http://microsoft.com/Opc/OpcPlc/;s=RandomSignedInt32";
	
	@Override
	public void configure() throws Exception {
		from(miloClient("opc.tcp://localhost:50000")
				.overrideHost(true)
				.allowedSecurityPolicies(SecurityPolicy.Basic256Sha256.name())
				.keyStoreUrl("classpath:user.jks")
				.keyStorePassword("12345678")
				.keyPassword("12345678")
				.node(NODE_ID))
			.routeId(OPCUA_CONSUMER_ROUTE)
			.log(">> Message from OPCUA ${body}");
	}
}
