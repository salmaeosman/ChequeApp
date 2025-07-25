package repositories;

import db.H2Database;
import entities.Cheque;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChequeRepository {

    private final Connection conn;

    // Nouveau constructeur qui accepte un objet H2Database
    public ChequeRepository(H2Database db) {
        this.conn = db.getConnection();
    }

    public void save(Cheque cheque) {
        String sql = """
            INSERT INTO cheques (nomCheque, nomSerie, montant, date, ville, numeroSerie, beneficiaire, langue)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cheque.getNomCheque());
            ps.setString(2, cheque.getNomSerie());
            ps.setDouble(3, cheque.getMontant());
            ps.setDate(4, Date.valueOf(cheque.getDate()));
            ps.setString(5, cheque.getVille());
            ps.setLong(6, cheque.getNumeroSerie());
            ps.setString(7, cheque.getBeneficiaire());
            ps.setString(8, cheque.getLangue());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cheque.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du chèque : " + e.getMessage(), e);
        }
    }

    public void update(Cheque cheque) {
        String sql = """
            UPDATE cheques
            SET nomCheque = ?, nomSerie = ?, montant = ?, date = ?, ville = ?,
                numeroSerie = ?, beneficiaire = ?, langue = ?
            WHERE id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cheque.getNomCheque());
            ps.setString(2, cheque.getNomSerie());
            ps.setDouble(3, cheque.getMontant());
            ps.setDate(4, Date.valueOf(cheque.getDate()));
            ps.setString(5, cheque.getVille());
            ps.setLong(6, cheque.getNumeroSerie());
            ps.setString(7, cheque.getBeneficiaire());
            ps.setString(8, cheque.getLangue());
            ps.setLong(9, cheque.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du chèque : " + e.getMessage(), e);
        }
    }
    
    public List<Cheque> findAll() {
        List<Cheque> cheques = new ArrayList<>();
        String sql = "SELECT * FROM cheques";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cheques.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des chèques : " + e.getMessage(), e);
        }
        return cheques;
    }

    public Optional<Cheque> findById(Long id) {
        String sql = "SELECT * FROM cheques WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Cheque> findByNomChequeAndNomSerieAndNumeroSerie(String nomCheque, String nomSerie, Long numeroSerie) {
        String sql = "SELECT * FROM cheques WHERE nomCheque = ? AND nomSerie = ? AND numeroSerie = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomCheque);
            ps.setString(2, nomSerie);
            ps.setLong(3, numeroSerie);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByNomChequeAndNomSerieAndNumeroSerie : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Cheque getLastInserted() {
        String sql = "SELECT * FROM cheques ORDER BY id DESC LIMIT 1";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur getLastInserted : " + e.getMessage(), e);
        }
        throw new RuntimeException("Aucun chèque trouvé.");
    }

    private Cheque map(ResultSet rs) throws SQLException {
        Cheque cheque = new Cheque(); // doit avoir un constructeur vide
        cheque.setId(rs.getLong("id"));
        cheque.setNomCheque(rs.getString("nomCheque"));
        cheque.setNomSerie(rs.getString("nomSerie"));
        cheque.setMontant(rs.getDouble("montant"));
        cheque.setDate(rs.getDate("date").toLocalDate());
        cheque.setVille(rs.getString("ville"));
        cheque.setNumeroSerie(rs.getLong("numeroSerie"));
        cheque.setBeneficiaire(rs.getString("beneficiaire"));
        cheque.setLangue(rs.getString("langue"));
        return cheque;
    }
}
