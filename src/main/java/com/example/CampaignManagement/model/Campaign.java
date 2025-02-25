package com.example.CampaignManagement.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

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

//    @Column(name = "customer_scope")
//    private String CustomerScope;
//
//    @Column(name = "menu",nullable = false)
//    private String menu;
//
//    @Column(name = "platform", nullable = false)
//    private String platform;
//
//    @Column(name = "msisdn",nullable = false)
//    private String msisdn;
//
//    @Column(name = "campaign_short_title",nullable = false)
//    private String CampaignShortTitle;
//
//    @Column(name = "dashboard",nullable = false)
//    private boolean dashboard;
//
//    @Column(name = "popup",nullable = false)
//    private boolean popup;
//
//    @Column(name = "offer_page",nullable = false)
//    private boolean offerPage;
//
//    @Column(name = "confirmation_page",nullable = false)
//    private boolean confirmationPage;
//
//    @Column(name = "result_page",nullable = false)
//    private boolean resultPage;
}
