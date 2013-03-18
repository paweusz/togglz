package org.togglz.core.activation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.togglz.core.logging.Log;
import org.togglz.core.logging.LogFactory;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.ActivationStrategy;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.util.Strings;

/**
 * Activation strategy that allows to activate features only for certain ip's.
 * 
 * @author Eli Abramovitch
 */
public class IPActivationStrategy implements ActivationStrategy {

    private final Log log = LogFactory.getLog(IPActivationStrategy.class);

    public static final String ID = "ip";

    public static final String PARAM_IPS = "ips";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "IP";
    }

    @Override
    public boolean isActive(FeatureState featureState, FeatureUser user) {
        String ipsAsString = featureState.getParameter(PARAM_IPS);

        if (Strings.isNotBlank(ipsAsString)) {

            List<String> ips = Strings.splitAndTrim(ipsAsString, ",");
            String currentMachineIp = null;
            try {
                currentMachineIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.info("Could not resolve current IP: " + e.getMessage());
            }
            if (currentMachineIp != null) {
                for (String ip : ips) {
                    if (currentMachineIp.equals(ip)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] {
                ParameterBuilder.create(PARAM_IPS).label("IPs").largeText()
                    .description("A list of IPs for which the feature should be active.")
        };
    }

}
