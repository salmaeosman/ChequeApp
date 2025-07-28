package controllers;

import entities.Cheque;
import repositories.ChequeRepository;
import services.MontantEnLettresService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ChequeController {

    private final ChequeRepository chequeRepository;

    public ChequeController(ChequeRepository chequeRepository) {
        this.chequeRepository = chequeRepository;
    }

    /**
     * Enregistre un nouveau chèque avec validation.
     * @return code : "success", "existedeja", "beneficiaire", ou "erreur"
     */
    public String enregistrerCheque(String beneficiaire, double montant, String ville,
                                    String langue, String nomCheque, String nomSerie,
                                    Long numeroSerie, String dateStr) {

        try {
            String nomChequeUpper = nomCheque.toUpperCase();
            String nomSerieUpper = nomSerie.toUpperCase();

            Optional<Cheque> existant = chequeRepository
                    .findByNomChequeAndNomSerieAndNumeroSerie(nomChequeUpper, nomSerieUpper, numeroSerie);

            if (existant.isPresent()) return "existedeja";
            if (beneficiaire == null || beneficiaire.isBlank()) return "beneficiaire";

            Cheque cheque = new Cheque();
            cheque.setMontant(montant);
            cheque.setVille(ville);
            cheque.setDate(LocalDate.parse(dateStr));
            cheque.setNomCheque(nomChequeUpper);
            cheque.setNomSerie(nomSerieUpper);
            cheque.setNumeroSerie(numeroSerie);
            cheque.setBeneficiaire(beneficiaire.toUpperCase());
            cheque.setLangue(langue);

            chequeRepository.save(cheque);
            return "success";

        } catch (Exception e) {
            return "erreur";
        }
    }

    /**
     * Met à jour un chèque existant.
     */
    public boolean modifierCheque(Long id, Cheque chequeForm) {
        Optional<Cheque> opt = chequeRepository.findById(id);
        if (opt.isEmpty()) return false;

        Cheque cheque = opt.get();
        cheque.setMontant(chequeForm.getMontant());
        cheque.setVille(chequeForm.getVille());
        cheque.setDate(chequeForm.getDate());
        cheque.setBeneficiaire(chequeForm.getBeneficiaire());
        cheque.setNomCheque(chequeForm.getNomCheque());
        cheque.setNomSerie(chequeForm.getNomSerie());
        cheque.setNumeroSerie(chequeForm.getNumeroSerie());

        chequeRepository.update(cheque);
        return true;
    }

    /**
     * Récupère un chèque par ID.
     */
    public Optional<Cheque> getChequeById(Long id) {
        return chequeRepository.findById(id);
    }

    /**
     * Renvoie le montant en lettres selon la langue.
     */
    public String getMontantLettre(double montant, String langue) {
        return MontantEnLettresService.convertirMontant(montant, langue);
    }

    // ✅ Méthode ajoutée pour corriger l'erreur
    public List<Cheque> getAllCheques() {
        return chequeRepository.findAll();
    }
    public String convertirMontantEnLettre(Double montant, String langue) {
        return MontantEnLettresService.convertirMontant(montant, langue);
    }
}
