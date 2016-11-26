package org.yamcs.studio.core.ui.connections;

import java.util.Objects;

import org.yamcs.api.YamcsConnectionProperties;
import org.yamcs.studio.core.ConnectionInfo;
import org.yamcs.studio.core.security.YamcsCredentials;

/**
 * UI class. Used to maintain state of a server in the connection manager dialog
 */
public class YamcsConfiguration {
    private String name;
    private String instance;
    private String user;
    private String password;

    private String primaryHost;
    private Integer primaryPort;

    private String failoverHost;
    private Integer failoverPort;

    private boolean savePassword;

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public boolean isAnonymous() {
        return getUser() == null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getPrimaryHost() {
        return primaryHost;
    }

    public void setPrimaryHost(String primaryHost) {
        this.primaryHost = primaryHost;
    }

    public Integer getPrimaryPort() {
        return primaryPort;
    }

    public void setPrimaryPort(Integer primaryPort) {
        this.primaryPort = primaryPort;
    }

    public void setFailoverHost(String failoverHost) {
        this.failoverHost = failoverHost;
    }

    public String getFailoverHost() {
        return failoverHost;
    }

    public void setFailoverPort(Integer failoverPort) {
        this.failoverPort = failoverPort;
    }

    public Integer getFailoverPort() {
        return failoverPort;
    }

    public boolean isFailoverConfigured() {
        return failoverHost != null;
    }

    public boolean isSavePassword() {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    public String getPrimaryConnectionString() {
        if (instance == null || "".equals(instance)) {
            return "yamcs://" + primaryHost + ":" + primaryPort;
        } else {
            return "yamcs://" + primaryHost + ":" + primaryPort + "/" + instance;
        }
    }

    public String getFailoverConnectionString() {
        if (isFailoverConfigured()) {
            if (instance == null || "".equals(instance)) {
                return "yamcs://" + failoverHost + ":" + failoverPort;
            } else {
                return "yamcs://" + failoverHost + ":" + failoverPort + "/" + instance;
            }
        } else {
            return null;
        }
    }

    public ConnectionInfo toConnectionInfo() {
        YamcsConnectionProperties primaryProps = new YamcsConnectionProperties(primaryHost, primaryPort, instance);
        YamcsConnectionProperties failoverProps = null;
        if (failoverHost != null) {
            failoverProps = new YamcsConnectionProperties(failoverHost, failoverPort, instance);
        }
        return new ConnectionInfo(primaryProps, failoverProps);
    }

    public YamcsCredentials toYamcsCredentials() {
        return (isAnonymous()) ? null : new YamcsCredentials(user, password);
    }

    @Override
    public boolean equals(Object obj) {
        // TODO This is not exactly true, since we don't currently check for
        // duplicates.
        // We do need an equals-method though, as it is used to compare the
        // last-used configuration
        // with the list of all configurations.
        if (obj == null)
            return false;
        YamcsConfiguration other = (YamcsConfiguration) obj;
        return Objects.equals(name, other.name);
    }
}