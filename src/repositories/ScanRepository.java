package repositories;

import db.H2Database;
import entities.Cheque;
import entities.Scan;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Optional;

public class ScanRepository {

    private final Connection conn;

    public ScanRepository(H2Database db) {
        this.conn = db.getConnection();
    }

    public void save(Scan scan) {
        try (PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO scans (fileName, fileType, image, cheque_id)
            VALUES (?, ?, ?, ?)
        """, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, scan.getFileName());
            ps.setString(2, scan.getFileType());
            ps.setBinaryStream(3, new ByteArrayInputStream(scan.getImage()));
            ps.setLong(4, scan.getCheque().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    scan.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("unique_cheque_scan")) {
                throw new RuntimeException("Le chèque a déjà un scan associé.");
            }
            throw new RuntimeException("Erreur insertion scan : " + e.getMessage(), e);
        }
    }

    public Optional<Scan> findByCheque(Cheque cheque) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM scans WHERE cheque_id = ?")) {
            ps.setLong(1, cheque.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Scan scan = new Scan();
                    scan.setId(rs.getLong("id"));
                    scan.setFileName(rs.getString("fileName"));
                    scan.setFileType(rs.getString("fileType"));
                    scan.setImage(rs.getBytes("image"));
                    scan.setCheque(cheque);
                    return Optional.of(scan);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCheque : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Scan findByChequeId(Long chequeId) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM scans WHERE cheque_id = ?")) {

            stmt.setLong(1, chequeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Scan scan = new Scan();
                scan.setId(rs.getLong("id"));
                scan.setFileName(rs.getString("fileName"));
                scan.setFileType(rs.getString("fileType"));
                scan.setImage(rs.getBytes("image"));

                Cheque cheque = new Cheque();
                cheque.setId(chequeId);
                scan.setCheque(cheque);

                return scan;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
