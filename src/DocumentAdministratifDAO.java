import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentAdministratifDAO {

    // Methode pour ajouter un document administratif dans la base de donnees
    public static void addDocumentAdministratif(DocumentAdministratif document) throws SQLException {
        // Verifier si le document existe deja dans la base de donnees
        String checkSql = "SELECT * FROM document_administratif WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, document.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Si le document avec cet ID existe deja, afficher un message et sortir
                System.out.println("Document avec l'ID " + document.getId() + " existe deja.");
                return;  // Quitter la methode sans inserer
            }
        }

        // Si le document n'existe pas, inserer le nouveau document dans la table
        String sql = "INSERT INTO document_administratif (id, type, content, project_id, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, document.getId());
            pstmt.setString(2, document.getType());
            pstmt.setString(3, document.getContent());
            pstmt.setString(4, document.getProjet() != null ? document.getProjet().getId() : null);
            pstmt.setTimestamp(5, new Timestamp(document.getDate().getTime()));
            pstmt.executeUpdate();
            System.out.println("Document ajoute avec succes : " + document.getId());
        }
    }

    // Methode pour recuperer un document administratif par ID dans la base de donnees
    public static DocumentAdministratif getDocumentById(String documentId) throws SQLException {
        String sql = "SELECT * FROM document_administratif WHERE id = ?";
        DocumentAdministratif document = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                String content = rs.getString("content");
                Timestamp date = rs.getTimestamp("date");
                String projetId = rs.getString("project_id");

                Projet projet = null;
                if (projetId != null) {
                    projet = ProjetDAO.getProjetById(projetId);
                }

                document = new DocumentAdministratif(documentId, type, content, projet, date);
            }
        }
        return document;
    }

    // Methode pour recuperer tous les documents administratifs dans la base de donnees
    public static List<DocumentAdministratif> getAllDocuments() throws SQLException {
        List<DocumentAdministratif> documents = new ArrayList<>();
        String sql = "SELECT * FROM document_administratif";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String documentId = rs.getString("id");
                String type = rs.getString("type");
                String content = rs.getString("content");
                Timestamp date = rs.getTimestamp("date");
                String projetId = rs.getString("project_id");

                Projet projet = null;
                if (projetId != null) {
                    projet = ProjetDAO.getProjetById(projetId);
                }

                DocumentAdministratif document = new DocumentAdministratif(documentId, type, content, projet, date);
                documents.add(document);
            }
        }
        return documents;
    }
}
