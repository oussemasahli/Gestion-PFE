import java.sql.*;
import java.util.Date;

public class Reclamation {
    private String id;
    private Etudiant etudiant; // L'etudiant qui fait la reclamation
    private String description; // Le contenu de la reclamation
    private String type; // Type de reclamation (ex. "extension", "clarification")
    private String status; // Statut de la reclamation (ex. "en attente", "resolue")
    private Date date; // Date de la reclamation

    // Constructeur
    public Reclamation(String id, Etudiant etudiant, String description, String type, String status, Date date) {
        this.id = id;
        this.etudiant = etudiant;
        this.description = description;
        this.type = type;
        this.status = status;
        this.date = date;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    // Methode pour enregistrer la reclamation dans la base de donnees
    public void saveToDatabase() throws SQLException {
        ReclamationDAO.addReclamation(this);
    }

    // Methode pour afficher les details de la reclamation
    public void afficherDetails() {
        System.out.println("ID : " + id);
        System.out.println("etudiant : " + etudiant.getNom());
        System.out.println("Description : " + description);
        System.out.println("Type : " + type);
        System.out.println("Statut : " + status);
        System.out.println("Date : " + date);
    }
}
