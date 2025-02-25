package com.example.CampaignManagement.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "scheduled_tasks")
@Data
public class ScheduledTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduled_task_id")
    private Long id;

    @Column(name = "customer_scope")
    private String CustomerScope;

    @Column(name = "menu",nullable = false)
    private String menu;

    @Column(name = "platform", nullable = false)
    private String platform;

    @Column(name = "msisdn",nullable = false)
    private String msisdn;

    @Column(name = "campaign_short_title",nullable = false)
    private String CampaignShortTitle;

    @Column(name = "dashboard",nullable = false)
    private boolean dashboard;

    @Column(name = "popup",nullable = false)
    private boolean popup;

    @Column(name = "offer_page",nullable = false)
    private boolean offerPage;

    @Column(name = "confirmation_page",nullable = false)
    private boolean confirmationPage;

    @Column(name = "result_page",nullable = false)
    private boolean resultPage;


}
