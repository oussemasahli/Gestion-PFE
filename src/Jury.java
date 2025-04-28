import java.sql.*;
import java.util.List;

public class Jury {
    private String id;
    private String name;  // Name of the jury
    private List<Encadrant> membres; // List of jury members

    // Constructor
    public Jury(String id, String name, List<Encadrant> membres) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide.");
        }
        this.id = id;
        this.name = name;
        this.membres = membres;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // Add this setter
        this.name = name;
    }

    public List<Encadrant> getMembres() {
        return membres;
    }

    // Save the jury to the database
    public void saveToDatabase() throws SQLException {
        JuryDAO.addJury(this);
    }

    // Display jury members
    public void afficherMembres() {
        for (Encadrant encadrant : membres) {
            System.out.println("Membre du jury : " + encadrant.getNom());
        }
    }
}
