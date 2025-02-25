package com.example.CampaignManagement.service;

import com.example.CampaignManagement.model.Campaign;
import com.example.CampaignManagement.model.CampaignStatus;
import com.example.CampaignManagement.model.ServiceType;
import com.example.CampaignManagement.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.EnumSet;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class CampaignService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .sorted(Comparator.comparing(Campaign::getPriority).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    public Campaign createCampaign(Campaign campaign) {
        logger.info("Creating new campaign: {}", campaign);
        try {
            validateCampaign(campaign);

            if (ServiceType.UNICA_ARCHIVE.equals(campaign.getServiceType())) {
                campaign.setStatus(CampaignStatus.PASSIVE);
            } else if (campaign.getStatus() == null) {
                campaign.setStatus(CampaignStatus.ACTIVE);
            }

            Campaign savedCampaign = campaignRepository.save(campaign);
            logger.info("Campaign created successfully with id: {}", savedCampaign.getId());
            return savedCampaign;
        } catch (Exception e) {
            logger.error("Error creating campaign: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Campaign updateCampaign(Long id) {
        return campaignRepository.findById(id)
                .map(existingCampaign -> {
                    CampaignStatus newStatus = (existingCampaign.getStatus() == CampaignStatus.ACTIVE)
                            ? CampaignStatus.PASSIVE
                            : CampaignStatus.ACTIVE;

                    if (ServiceType.UNICA_ARCHIVE.equals(existingCampaign.getServiceType())
                            && newStatus == CampaignStatus.ACTIVE) {
                        throw new RuntimeException("UNICA_ARCHIVE campaigns must be PASSIVE");
                    }

                    existingCampaign.setStatus(newStatus);
                    return campaignRepository.save(existingCampaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    public Campaign activateCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));

        if (ServiceType.UNICA_ARCHIVE.equals(campaign.getServiceType())) {
            throw new RuntimeException("Cannot activate UNICA_ARCHIVE campaigns");
        }

        campaign.setStatus(CampaignStatus.ACTIVE);
        return campaignRepository.save(campaign);
    }

    public Campaign deactivateCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));

        campaign.setStatus(CampaignStatus.PASSIVE);
        return campaignRepository.save(campaign);
    }

    private void validateCampaign(Campaign campaign) {
        List<String> validationErrors = Stream.of(
                validateServiceType(campaign),
                validateCampaignType(campaign),
                validateDates(campaign),
                validatePriority(campaign),
                validateStatus(campaign))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (!validationErrors.isEmpty()) {
            throw new RuntimeException(String.join(", ", validationErrors));
        }
    }

    private Optional<String> validateServiceType(Campaign campaign) {
        if (campaign.getServiceType() == null) {
            return Optional.of("Service type is required");
        }
        if (!EnumSet.of(ServiceType.UNICA, ServiceType.UNICA_ARCHIVE).contains(campaign.getServiceType())) {
            return Optional.of("Invalid service type. Must be either UNICA or UNICA_ARCHIVE");
        }
        return Optional.empty();
    }

    private Optional<String> validateCampaignType(Campaign campaign) {
        if (campaign.getCampaignType() == null) {
            return Optional.of("Campaign type is required");
        }
        if (!EnumSet.of(CampaignType.SEASONAL, CampaignType.STANDARD).contains(campaign.getCampaignType())) {
            return Optional.of("Invalid campaign type. Must be either SEASONAL or STANDARD");
        }
        return Optional.empty();
    }

    private Optional<String> validateDates(Campaign campaign) {
        if (campaign.getStartDate() == null || campaign.getEndDate() == null) {
            return Optional.of("Campaign start and end dates are required");
        }
        if (campaign.getStartDate().after(campaign.getEndDate())) {
            return Optional.of("Campaign start date cannot be after end date");
        }
        return Optional.empty();
    }

    private Optional<String> validatePriority(Campaign campaign) {
        if (campaign.getPriority() == null || campaign.getPriority() < 1) {
            return Optional.of("Campaign priority must be a positive number");
        }
        return Optional.empty();
    }

    private Optional<String> validateStatus(Campaign campaign) {
        if (ServiceType.UNICA_ARCHIVE.equals(campaign.getServiceType())
                && CampaignStatus.ACTIVE.equals(campaign.getStatus())) {
            return Optional.of("UNICA_ARCHIVE campaigns must be PASSIVE");
        }
        return Optional.empty();
    }

    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE);
    }

    public List<Campaign> getPassiveCampaigns() {
        return campaignRepository.findByStatus(CampaignStatus.PASSIVE);
    }

    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }

    public List<Campaign> getCampaignsByServiceType(ServiceType serviceType) {
        return campaignRepository.findByServiceType(serviceType);
    }

    public List<Campaign> getCampaignsByType(CampaignType campaignType) {
        return campaignRepository.findByCampaignType(campaignType);
    }
}
