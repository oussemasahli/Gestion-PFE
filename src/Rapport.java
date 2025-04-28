import java.sql.*;

public class Rapport {
    private String id;
    private Etudiant etudiant;
    private Projet projet;
    private boolean soumis; // Indique si le rapport est soumis ou non

    // Constructeur
    public Rapport(String id, Etudiant etudiant, Projet projet) {
        this.id = id;
        this.etudiant = etudiant;
        this.projet = projet;
        this.soumis = false;
    }

    // Soumettre le rapport
    public void soumettre() throws SQLException {
        this.soumis = true;
        RapportDAO.saveRapport(this); // Sauvegarder le rapport dans la base de donnees
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public Projet getProjet() {
        return projet;
    }

    public boolean isSoumis() {
        return soumis;
    }
}
