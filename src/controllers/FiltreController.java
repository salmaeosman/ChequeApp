package controllers;

import entities.Cheque;
import repositories.ChequeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FiltreController {

    private final ChequeRepository chequeRepository;
    private final ChequeController chequeController;

    public FiltreController(ChequeRepository chequeRepository) {
        this.chequeRepository = chequeRepository;
        this.chequeController = new ChequeController(chequeRepository);
    }

    public List<Cheque> filtrerCheques(String cheque, String beneficiaire, LocalDate date, Double montant) {
        List<Cheque> cheques = chequeRepository.findAll(); // à implémenter si ce n'est pas encore fait
        List<Cheque> resultats = new ArrayList<>();

        for (Cheque c : cheques) {
            boolean match = true;

            if (cheque != null && !cheque.isBlank()) {
                String identifiant = (c.getNomCheque() != null ? c.getNomCheque() : "") +
                                     (c.getNomSerie() != null ? c.getNomSerie() : "") +
                                     (c.getNumeroSerie() != null ? c.getNumeroSerie().toString() : "");
                if (!identifiant.equalsIgnoreCase(cheque)) {
                    match = false;
                }
            }

            if (match && beneficiaire != null && !beneficiaire.isBlank()) {
                if (c.getBeneficiaire() == null || !c.getBeneficiaire().equalsIgnoreCase(beneficiaire)) {
                    match = false;
                }
            }

            if (match && date != null) {
                if (c.getDate() == null || !c.getDate().equals(date)) {
                    match = false;
                }
            }

            if (match && montant != null) {
                if (Math.abs(c.getMontant() - montant) > 0.01) {
                    match = false;
                }
            }

            if (match) {
                resultats.add(c);
            }
        }

        return resultats;
    }

    public ChequeController getChequeController() {
        return chequeController;
    }
}
