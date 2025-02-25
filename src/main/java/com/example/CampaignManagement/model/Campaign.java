package com.example.CampaignManagement.model;

import jakarta.persistence.*;
import lombok.Data;


import java.util.Date;

@Entity
@Table(name = "campaigns")
@Data
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long id;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(nullable = false)
    private String campaign;

    @Column(nullable = false)
    private String offer;

    @Column(nullable = false)
    private String treatment;

    @Column(name = "campaign_type", nullable = false)
    private String campaignType;

    @Column(name = "campaign_subtype", nullable = false)
    private String campaignSubtype;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;


}
