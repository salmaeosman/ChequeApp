package controllers;

import entities.Scan;
import services.ScanService;

import java.util.Base64;

public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    public void scannerCheque(Long chequeId, String profileName) {
        try {
            Scan scan = scanService.launchRealScan(chequeId, profileName);
            System.out.println("✅ Scan réussi : " + scan.getFileName());

            String base64 = Base64.getEncoder().encodeToString(scan.getImage());
            System.out.println("Image en base64 : ");
            System.out.println(base64.substring(0, 100) + "...");

        } catch (Exception e) {
            System.err.println("❌ Erreur de scan : " + e.getMessage());
        }
    }
 // ScanController.java
    public Scan getScanByChequeId(Long chequeId) {
        return scanService.getScanByChequeId(chequeId);
    }

}