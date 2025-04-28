import java.sql.*;

public class Encadrant {
    private String id;
    private String nom;
    private int disponibilite; // Nombre maximum de projets qu'il peut encadrer
    private int chargeEncadrement; // Nombre actuel de projets encadres
    private String role; // Add role field

    // Constructeurs
    public Encadrant() {}

    public Encadrant(String id, String nom, int disponibilite, int chargeEncadrement) {
        if (id == null || id.isEmpty() || nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("ID et nom ne peuvent pas être vides.");
        }
        this.id = id;
        this.nom = nom;
        this.disponibilite = disponibilite;
        this.chargeEncadrement = chargeEncadrement;
    }

    // Nouveau constructeur pour correspondre aux arguments (String id, String nom)
    public Encadrant(String id, String nom) {
        if (id == null || id.isEmpty() || nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("ID et nom ne peuvent pas être vides.");
        }
        this.id = id;
        this.nom = nom;
        this.disponibilite = 3; // Valeur par defaut
        this.chargeEncadrement = 0; // Valeur par defaut
    }

    // Nouveau constructeur pour correspondre aux arguments (String id, String nom, String role)
    public Encadrant(String id, String nom, String role) {
        this.id = id;
        this.nom = nom;
        this.role = role;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("Nom cannot be empty.");
        }
        this.nom = nom;
    }

    public int getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(int disponibilite) {
        this.disponibilite = disponibilite;
    }

    public int getChargeEncadrement() {
        return chargeEncadrement;
    }

    public void setChargeEncadrement(int chargeEncadrement) {
        this.chargeEncadrement = chargeEncadrement;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Methode pour verifier si l'encadrant peut encadrer un projet supplementaire
    public boolean peutEncadrer() {
        return disponibilite > 0; // Encadrant is available if availability > 0
    }

    // Methode pour augmenter la charge d'encadrement
    public void incrementerCharge() {
        if (!peutEncadrer()) {
            throw new IllegalStateException("Cet encadrant n'est pas disponible.");
        }
        chargeEncadrement++;
        disponibilite--; // Decrease availability when a project is assigned
    }

    // Methode pour reduire la charge d'encadrement
    public void decrementerCharge() {
        if (chargeEncadrement > 0) {
            chargeEncadrement--;
            disponibilite++; // Increase availability when a project is removed
        }
    }

    // Methode pour afficher les details de l'encadrant
    public void afficherDetails() {
        System.out.println("ID : " + id);
        System.out.println("Nom : " + nom);
        System.out.println("Disponibilite : " + disponibilite);
        System.out.println("Charge d'encadrement actuelle : " + chargeEncadrement);
        System.out.println("Role : " + role);
    }

    // Methode pour enregistrer un encadrant dans la base de donnees
    public void saveToDatabase() throws SQLException {
        EncadrantDAO.addEncadrant(this);
    }

    // Methode pour mettre a jour un encadrant dans la base de donnees
    public void updateInDatabase() throws SQLException {
        EncadrantDAO.updateEncadrant(this);
    }

    // Methode pour recuperer un encadrant depuis la base de donnees
    public static Encadrant getFromDatabase(String supervisorId) throws SQLException {
        return EncadrantDAO.getEncadrantById(supervisorId);
    }
}
