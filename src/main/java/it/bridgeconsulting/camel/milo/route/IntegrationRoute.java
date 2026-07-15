package it.bridgeconsulting.camel.milo.route;

import java.util.Arrays;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.milo.MiloConstants;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IntegrationRoute extends EndpointRouteBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationRoute.class);

	public static final String OPCUA_CONSUMER_ROUTE = "opcUaConsumerRoute";
	private static final String NODE_ID = "nsu=http://microsoft.com/Opc/OpcPlc/;s=RandomSignedInt32";
	
	@Override
	public void configure() throws Exception {
		
		from(miloClient("opc.tcp://user1:password@localhost:50000")
				.overrideHost(true)
				.allowedSecurityPolicies(SecurityPolicy.Basic256Sha256.name())
				.keyStoreUrl("classpath:user.jks")
				.keyStorePassword("12345678")
				.keyPassword("12345678")
				.node(NODE_ID)
				.requestedPublishingInterval("5000"))
			.autoStartup(false)
			.routeId(OPCUA_CONSUMER_ROUTE)
			.log(LoggingLevel.INFO, LOG, ">> Message from OPCUA ${body}");
		
		from(timer("opcPoller").period(5000))
		    .setHeader(MiloConstants.HEADER_NODE_IDS, constant(Arrays.asList(
		    		"ns=3;i=1002",
		    		"ns=3;i=1005")))
		    .setHeader(MiloConstants.HEADER_AWAIT, constant(true))
	        .enrich(miloClient("{{opc.server.2}}")
	        		.allowedSecurityPolicies(SecurityPolicy.None.name()))
	        .log(LoggingLevel.INFO, LOG, ">> Message from OPCUA ${body}");
	}
}
