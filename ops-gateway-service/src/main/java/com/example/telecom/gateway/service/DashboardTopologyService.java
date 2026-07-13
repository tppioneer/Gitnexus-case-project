package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.common.device.DeviceInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardTopologyService {

    private final DeviceClient deviceClient;

    public DashboardTopologyService(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    public Map<String, Object> getTopology() {
        List<DeviceInfo> devices = deviceClient.getAllDevices();
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (DeviceInfo device : devices) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", device.getDeviceId());
            node.put("name", device.getDeviceName());
            node.put("type", device.getDeviceType() != null ? device.getDeviceType().name() : "UNKNOWN");
            node.put("regionCode", device.getMaintenanceRegionCode());
            nodes.add(node);
        }
        return buildGraph(nodes, Collections.emptyList());
    }

    public Map<String, Object> getNodeTopology(String nodeId) {
        return getTopology();
    }

    public Map<String, Object> getRegionTopology(String regionCode) {
        List<DeviceInfo> devices = deviceClient.getAllDevices().stream()
                .filter(d -> regionCode.equals(d.getMaintenanceRegionCode()))
                .collect(Collectors.toList());
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (DeviceInfo device : devices) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", device.getDeviceId());
            node.put("name", device.getDeviceName());
            node.put("regionCode", device.getMaintenanceRegionCode());
            nodes.add(node);
        }
        return buildGraph(nodes, Collections.emptyList());
    }

    private Map<String, Object> buildGraph(List<Map<String, Object>> nodes, List<Map<String, Object>> edges) {
        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", nodes);
        graph.put("edges", edges);
        graph.put("nodeCount", nodes.size());
        return graph;
    }
}
