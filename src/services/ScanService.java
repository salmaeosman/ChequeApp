package services;

import entities.Cheque;
import entities.Scan;
import repositories.ChequeRepository;
import repositories.ScanRepository;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

public class ScanService {

    private final ScanRepository scanRepository;
    private final ChequeRepository chequeRepository;

    private final String naps2Path = "C:\\Program Files\\NAPS2\\naps2.console.exe"; // ou votre chemin
    private final String outputFolder = "scans";

    public ScanService(ScanRepository scanRepository, ChequeRepository chequeRepository) {
        this.scanRepository = scanRepository;
        this.chequeRepository = chequeRepository;
    }

    public Scan launchRealScan(Long chequeId, String profileName) throws Exception {
        Cheque cheque = chequeRepository.findById(chequeId)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        // 🔒 Vérification si le chèque a déjà été scanné
        Optional<Scan> existing = scanRepository.findByCheque(cheque);
        if (existing.isPresent()) {
            throw new RuntimeException("Ce chèque a déjà été scanné.");
        }

        File dir = new File(outputFolder);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "scan_" + UUID.randomUUID() + ".png";
        File outputFile = new File(dir, fileName);

        ProcessBuilder builder = new ProcessBuilder(
            naps2Path,
            "scan",
            "--profile", profileName,
            "--output", outputFile.getAbsolutePath()
        );

        Process process = builder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0 || !outputFile.exists()) {
            throw new RuntimeException("Erreur lors du scan (code=" + exitCode + ")");
        }

        byte[] imageBytes = Files.readAllBytes(outputFile.toPath());

        Scan scan = new Scan();
        scan.setCheque(cheque);
        scan.setFileName(fileName);
        scan.setFileType("image/png");
        scan.setImage(imageBytes);

        scanRepository.save(scan);
        return scan;
    }
 // ScanService.java
    public Scan getScanByChequeId(Long chequeId) {
        return scanRepository.findByChequeId(chequeId);
    }

}
