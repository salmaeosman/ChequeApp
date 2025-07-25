package entities;

import java.time.LocalDate;

public class Cheque {
    private Long id;
    private String nomCheque;
    private String nomSerie;
    private Double montant;
    private LocalDate date;
    private String ville;
    private Long numeroSerie;
    private String beneficiaire;
    private String langue;

    // ✅ Constructeur vide (requis par JDBC, H2, etc.)
    public Cheque() {
    }

    // ✅ Constructeur complet (optionnel, utile pour création rapide)
    public Cheque(Long id, String nomCheque, String nomSerie, Double montant,
                  LocalDate date, String ville, Long numeroSerie,
                  String beneficiaire, String langue) {
        this.id = id;
        this.nomCheque = nomCheque;
        this.nomSerie = nomSerie;
        this.montant = montant;
        this.date = date;
        this.ville = ville;
        this.numeroSerie = numeroSerie;
        this.beneficiaire = beneficiaire;
        this.langue = langue;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomCheque() { return nomCheque; }
    public void setNomCheque(String nomCheque) { this.nomCheque = nomCheque; }

    public String getNomSerie() { return nomSerie; }
    public void setNomSerie(String nomSerie) { this.nomSerie = nomSerie; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Long getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Long numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(String beneficiaire) { this.beneficiaire = beneficiaire; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
}
