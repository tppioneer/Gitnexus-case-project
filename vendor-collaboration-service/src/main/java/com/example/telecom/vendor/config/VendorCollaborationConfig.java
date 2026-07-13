package com.example.telecom.vendor.config;

import com.example.telecom.vendor.evaluation.QualityScoreVendorEvaluator;
import com.example.telecom.vendor.evaluation.ResolutionRateVendorEvaluator;
import com.example.telecom.vendor.evaluation.ResponseTimeVendorEvaluator;
import com.example.telecom.vendor.evaluation.VendorEvaluatorRegistry;
import com.example.telecom.vendor.event.VendorTicketEventConsumer;
import com.example.telecom.vendor.event.VendorTicketEventPublisher;
import com.example.telecom.vendor.mapper.VendorTicketMapper;
import com.example.telecom.vendor.repository.VendorFeedbackRepository;
import com.example.telecom.vendor.repository.VendorPerformanceRepository;
import com.example.telecom.vendor.repository.VendorSlaRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.vendor.service.VendorNotificationService;
import com.example.telecom.vendor.service.VendorSlaTrackingService;
import com.example.telecom.vendor.workflow.VendorCollaborationStateMachine;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VendorCollaborationConfig {

    @Bean
    public VendorTicketRepository vendorTicketRepository() {
        return new VendorTicketRepository();
    }

    @Bean
    public VendorSlaRepository vendorSlaRepository() {
        return new VendorSlaRepository();
    }

    @Bean
    public VendorFeedbackRepository vendorFeedbackRepository() {
        return new VendorFeedbackRepository();
    }

    @Bean
    public VendorPerformanceRepository vendorPerformanceRepository() {
        return new VendorPerformanceRepository();
    }

    @Bean
    public VendorCollaborationStateMachine vendorCollaborationStateMachine() {
        return new VendorCollaborationStateMachine();
    }

    @Bean
    public VendorTicketEventPublisher vendorTicketEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        return new VendorTicketEventPublisher(applicationEventPublisher);
    }

    @Bean
    public VendorTicketEventConsumer vendorTicketEventConsumer(
            VendorTicketRepository ticketRepository,
            VendorSlaTrackingService slaTrackingService,
            VendorNotificationService notificationService) {
        return new VendorTicketEventConsumer(ticketRepository, slaTrackingService, notificationService);
    }

    @Bean
    public ResponseTimeVendorEvaluator responseTimeVendorEvaluator(
            VendorTicketRepository ticketRepository) {
        return new ResponseTimeVendorEvaluator(ticketRepository);
    }

    @Bean
    public ResolutionRateVendorEvaluator resolutionRateVendorEvaluator(
            VendorTicketRepository ticketRepository) {
        return new ResolutionRateVendorEvaluator(ticketRepository);
    }

    @Bean
    public QualityScoreVendorEvaluator qualityScoreVendorEvaluator(
            VendorFeedbackRepository feedbackRepository) {
        return new QualityScoreVendorEvaluator(feedbackRepository);
    }

    @Bean
    public VendorEvaluatorRegistry vendorEvaluatorRegistry(
            ResponseTimeVendorEvaluator responseTimeEvaluator,
            ResolutionRateVendorEvaluator resolutionRateEvaluator,
            QualityScoreVendorEvaluator qualityScoreEvaluator) {
        VendorEvaluatorRegistry registry = new VendorEvaluatorRegistry();
        registry.register(responseTimeEvaluator);
        registry.register(resolutionRateEvaluator);
        registry.register(qualityScoreEvaluator);
        return registry;
    }
}
