package com.example.CampaignManagement.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "scheduled_tasks")
@Data
public class ScheduledTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduled_task_id")
    private Long id;

    @Column(name = "campaign_type", nullable = false)
    private String campaignType;

    @Column(nullable = false)
    private String offer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;


}
