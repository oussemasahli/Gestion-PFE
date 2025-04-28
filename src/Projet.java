import java.sql.*;

public class Projet {
    private String id;
    private String titre;
    private String description;

    // Constructor
    public Projet(String id, String titre, String description) {
        if (id == null || id.isEmpty() || titre == null || titre.isEmpty()) {
            throw new IllegalArgumentException("L'ID et le Titre ne peuvent pas être vides.");
        }
        this.id = id;
        this.titre = titre;
        this.description = description;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setTitre(String titre) {
        if (titre == null || titre.isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Save to database
    public void saveToDatabase() throws SQLException {
        ProjetDAO.addProjet(this);
    }

    // Update in database
    public void updateInDatabase() throws SQLException {
        ProjetDAO.updateProjet(this);
    }

    // Get project by ID from database
    public static Projet getFromDatabase(String projectId) throws SQLException {
        return ProjetDAO.getProjetById(projectId);
    }

    // Display project details
    public void afficherDetails() {
        System.out.println("ID : " + id);
        System.out.println("Titre : " + titre);
        System.out.println("Description : " + description);
    }
}
