package com.example.telecom.common.topology;

import java.util.Objects;

public class TopologyLink {

    private final String linkId;
    private final String sourceNodeId;
    private final String targetNodeId;
    private final String linkType;
    private final int bandwidth;
    private final int latency;
    private final String status;

    public TopologyLink(String linkId, String sourceNodeId, String targetNodeId,
                        String linkType, int bandwidth, int latency, String status) {
        this.linkId = linkId;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.linkType = linkType;
        this.bandwidth = bandwidth;
        this.latency = latency;
        this.status = status;
    }

    public String getLinkId() { return linkId; }
    public String getSourceNodeId() { return sourceNodeId; }
    public String getTargetNodeId() { return targetNodeId; }
    public String getLinkType() { return linkType; }
    public int getBandwidth() { return bandwidth; }
    public int getLatency() { return latency; }
    public String getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopologyLink that = (TopologyLink) o;
        return Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkId);
    }

    @Override
    public String toString() {
        return "TopologyLink{" +
                "linkId='" + linkId + '\'' +
                ", sourceNodeId='" + sourceNodeId + '\'' +
                ", targetNodeId='" + targetNodeId + '\'' +
                ", linkType='" + linkType + '\'' +
                ", bandwidth=" + bandwidth +
                ", latency=" + latency +
                ", status='" + status + '\'' +
                '}';
    }
}
