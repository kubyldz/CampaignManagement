package com.example.CampaignManagement.repository;

import com.example.CampaignManagement.model.Campaign;
import com.example.CampaignManagement.model.CampaignStatus;
import com.example.CampaignManagement.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus(CampaignStatus status);

    List<Campaign> findByServiceType(ServiceType serviceType);

    List<Campaign> findByCampaignType(CampaignType campaignType);

}
