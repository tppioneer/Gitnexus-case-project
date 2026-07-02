package com.example.telecom.collector.service;

import java.util.List;

import com.example.telecom.collector.adapter.VendorAdapter;
import com.example.telecom.collector.adapter.VendorAdapterRegistry;
import com.example.telecom.collector.event.MetricEventPublisher;
import com.example.telecom.collector.mapper.DeviceMetricMapper;
import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.collector.validator.DeviceMetricValidator;
import com.example.telecom.common.device.*;

/**
 * Core service for accepting and processing device metrics.
 * This is a key node in Case A flow tracing.
 */
public class MetricCollectorService {

    private final DeviceMetricRepository deviceMetricRepository;
    private final DeviceMetricValidator deviceMetricValidator;
    private final DeviceMetricNormalizer deviceMetricNormalizer;
    private final MetricEventPublisher metricEventPublisher;
    private final DeviceMetricMapper deviceMetricMapper;
    private final VendorAdapterRegistry vendorAdapterRegistry;

    public MetricCollectorService(DeviceMetricRepository deviceMetricRepository,
                                   DeviceMetricValidator deviceMetricValidator,
                                   DeviceMetricNormalizer deviceMetricNormalizer,
                                   MetricEventPublisher metricEventPublisher,
                                   DeviceMetricMapper deviceMetricMapper,
                                   VendorAdapterRegistry vendorAdapterRegistry) {
        this.deviceMetricRepository = deviceMetricRepository;
        this.deviceMetricValidator = deviceMetricValidator;
        this.deviceMetricNormalizer = deviceMetricNormalizer;
        this.metricEventPublisher = metricEventPublisher;
        this.deviceMetricMapper = deviceMetricMapper;
        this.vendorAdapterRegistry = vendorAdapterRegistry;
    }

    /**
     * Accept a raw metric, normalize it through vendor adapter, validate, save, and publish event.
     */
    public DeviceMetric acceptMetric(RawDeviceMetric rawMetric, DeviceInfo deviceInfo) {
        VendorAdapter adapter = vendorAdapterRegistry.resolve(rawMetric.getVendor());
        DeviceMetric metric = adapter.normalizeRawMetric(rawMetric, deviceInfo);
        deviceMetricValidator.validate(metric, deviceInfo);
        deviceMetricNormalizer.normalize(metric);
        deviceMetricRepository.save(metric);

        DeviceMetricEvent event = deviceMetricMapper.toEvent(metric, deviceInfo);
        metricEventPublisher.publish(event);

        return metric;
    }

    public List<DeviceMetric> getMetricsByDevice(String deviceId) {
        return deviceMetricRepository.findByDeviceId(deviceId);
    }
}
