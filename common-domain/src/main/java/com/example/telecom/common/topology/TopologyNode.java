package com.example.telecom.common.topology;

import java.util.Objects;

public class TopologyNode {

    private final String nodeId;
    private final String nodeName;
    private final String nodeType;
    private final String regionCode;
    private final String ipAddress;
    private final String status;
    private final String parentNodeId;

    public TopologyNode(String nodeId, String nodeName, String nodeType,
                        String regionCode, String ipAddress, String status,
                        String parentNodeId) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.regionCode = regionCode;
        this.ipAddress = ipAddress;
        this.status = status;
        this.parentNodeId = parentNodeId;
    }

    public String getNodeId() { return nodeId; }
    public String getNodeName() { return nodeName; }
    public String getNodeType() { return nodeType; }
    public String getRegionCode() { return regionCode; }
    public String getIpAddress() { return ipAddress; }
    public String getStatus() { return status; }
    public String getParentNodeId() { return parentNodeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopologyNode that = (TopologyNode) o;
        return Objects.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }

    @Override
    public String toString() {
        return "TopologyNode{" +
                "nodeId='" + nodeId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", status='" + status + '\'' +
                ", parentNodeId='" + parentNodeId + '\'' +
                '}';
    }
}
