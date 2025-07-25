package controllers;

import services.MontantEnLettresService;

public class MontantController {

    private MontantEnLettresService montantService;
    public MontantController(MontantEnLettresService montantService) {
        this.montantService = montantService;
    }
    public String convertirMontant(double montant, String langue) {
        return montantService.convertirMontant(montant, langue);
    }
    public static void main(String[] args) {
        MontantEnLettresService service = new MontantEnLettresService();
        MontantController controller = new MontantController(service);
        String resultat = controller.convertirMontant(1234.56, "fr");
        System.out.println("Montant en lettres : " + resultat);
    }
}