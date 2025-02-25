package com.example.CampaignManagement.controller;

import com.example.CampaignManagement.model.Campaign;
import com.example.CampaignManagement.model.CampaignStatus;
import com.example.CampaignManagement.model.ServiceType;
import com.example.CampaignManagement.service.CampaignService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private static final Logger logger = LoggerFactory.getLogger(CampaignController.class);

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/createCampaign")
    public ResponseEntity<Campaign> createCampaign(@Valid @RequestBody Campaign campaign) {
        try {
            Campaign createdCampaign = campaignService.createCampaign(campaign);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCampaign);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<Campaign> updateCampaignStatus(@PathVariable Long id) {
        try {
            Campaign updatedCampaign = campaignService.updateCampaign(id);
            return ResponseEntity.ok(updatedCampaign);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Campaign> activateCampaign(@PathVariable Long id) {
        try {
            Campaign campaign = campaignService.activateCampaign(id);
            return ResponseEntity.ok(campaign);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Campaign> deactivateCampaign(@PathVariable Long id) {
        try {
            Campaign campaign = campaignService.deactivateCampaign(id);
            return ResponseEntity.ok(campaign);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/service-type/{serviceType}")
    public ResponseEntity<List<Campaign>> getCampaignsByServiceType(
            @PathVariable ServiceType serviceType) {
        return ResponseEntity.ok(campaignService.getCampaignsByServiceType(serviceType));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Campaign>> getCampaignsByStatus(
            @PathVariable CampaignStatus status) {
        return ResponseEntity.ok(campaignService.getCampaignsByStatus(status));
    }

    @GetMapping("/getAllCampaigns")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping("/getByCampaignType")
    public ResponseEntity<List<Campaign>> getCampaignsByType(@RequestParam String campaignType) {
        try {
            if ("ALL".equalsIgnoreCase(campaignType)) {
                return ResponseEntity.ok(campaignService.getAllCampaigns());
            }
            CampaignType type = CampaignType.valueOf(campaignType.toUpperCase());
            return ResponseEntity.ok(campaignService.getCampaignsByType(type));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportCampaignsToExcel() {
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        logger.info("Exporting {} campaigns to Excel", campaigns.size());
        byte[] bytes = null;

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Campaigns");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Servis Tipi", "Kampanya ID", "Kampanya", "Teklif", "Treatment", "Kampanya Tipi", "Kampanya Alt Tipi", "Başlangıç Tarihi", "Bitiş Tarihi", "Öncelik", "Statü"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Campaign campaign : campaigns) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(campaign.getServiceType() != null ? campaign.getServiceType().toString() : "");
                row.createCell(1).setCellValue(campaign.getId());
                row.createCell(2).setCellValue(campaign.getCampaign() != null ? campaign.getCampaign() : "");
                row.createCell(3).setCellValue(campaign.getOffer() != null ? campaign.getOffer() : "");
                row.createCell(4).setCellValue(campaign.getTreatment() != null ? campaign.getTreatment() : "");
                row.createCell(5).setCellValue(campaign.getCampaignType() != null ? campaign.getCampaignType().toString() : "");
                row.createCell(6).setCellValue(campaign.getCampaignSubtype() != null ? campaign.getCampaignSubtype() : "");
                row.createCell(7).setCellValue(campaign.getStartDate() != null ? campaign.getStartDate().toString() : "");
                row.createCell(8).setCellValue(campaign.getEndDate() != null ? campaign.getEndDate().toString() : "");
                row.createCell(9).setCellValue(campaign.getPriority() != null ? campaign.getPriority().toString() : "");
                row.createCell(10).setCellValue(campaign.getStatus() != null ? campaign.getStatus().toString() : "");

            }

            workbook.write(outputStream);
            bytes = outputStream.toByteArray();

        } catch (IOException e) {
            logger.error("Error while exporting campaigns to Excel", e);
        }

        if (bytes != null) {
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=campaigns.xlsx")
                    .body(bytes);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
