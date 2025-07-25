package services;

public class MontantEnLettresService {
    public static String convertirMontant(double montant, String langue) {
        if ("ar".equalsIgnoreCase(langue)) {
            return NombreArabe.convert(montant);
        } else if ("fr".equalsIgnoreCase(langue)) {
            return NombreEnLettre.convertir(montant);
        } else {
            return "Langue non supportée.";
        }
    }
}