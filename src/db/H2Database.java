package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Database {

    // ✅ URL JDBC persistante, multi-processus et durable
    private static final String JDBC_URL = "jdbc:h2:file:C:/Users/pc/eclipse-workspace/GestionChequeV2/cheque-data;AUTO_SERVER=TRUE;";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private Connection conn;

    public H2Database() {
        try {
            this.conn = init();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données : " + e.getMessage(), e);
        }
    }

    private Connection init() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cheques (
                    id IDENTITY PRIMARY KEY,
                    nomCheque VARCHAR(255),
                    nomSerie VARCHAR(255),
                    montant DOUBLE,
                    date DATE,
                    ville VARCHAR(255),
                    numeroSerie BIGINT,
                    beneficiaire VARCHAR(255),
                    langue VARCHAR(5)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scans (
                    id IDENTITY PRIMARY KEY,
                    cheque_id BIGINT,
                    image BLOB,
                    fileName VARCHAR(255),
                    fileType VARCHAR(100),
                    FOREIGN KEY (cheque_id) REFERENCES cheques(id) ON DELETE CASCADE
                )
            """);
        }
        return conn;
    }

    public Connection getConnection() {
        return this.conn;
    }
}
