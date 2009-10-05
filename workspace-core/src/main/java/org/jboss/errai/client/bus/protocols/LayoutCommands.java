package org.jboss.errai.client.bus.protocols;

import org.jboss.errai.client.bus.MessageBusClient;

import java.util.Map;

public enum LayoutCommands {
    OpenNewTab,
    CloseTab,
    RegisterWorkspaceEnvironment,
    RegisterToolSet,
    GetWidget,
    DisposeWidget,
    PublishTool,
    ActivateTool,
    GetActiveWidgets,
    SizeHints,
    Initialize,
    GetInstances;

    public String getSubject() {
        return "org.jboss.errai.WorkspaceLayout";
    }

    public void send(Map<String, Object> message) {
        message.put(LayoutParts.CommandType.name(), this.name());
        MessageBusClient.send(getSubject(), message);
    }
}