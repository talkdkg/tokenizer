package org.tokenizer.executor.model.configuration;

public abstract class AbstractFetcherTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;

    protected String host;
    protected String agentName = "Tokenizer";
    protected String emailAddress = "info@tokenizer.ca";
    protected String webAddress = "http://www.tokenizer.ca";

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(final String agentName) {
        this.agentName = agentName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(final String webAddress) {
        this.webAddress = webAddress;
    }

}
