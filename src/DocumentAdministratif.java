import java.sql.*;
import java.util.Date;

public class DocumentAdministratif {
    private String id;
    private String type; // Type du document (ex : "attestation", "PV")
    private String content; // Contenu du document
    private Projet projet; // Projet associe (peut être null)
    private Date date; // Date de creation

    // Constructeur
    public DocumentAdministratif(String id, String type, String content, Projet projet, Date date) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.projet = projet;
        this.date = date;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Projet getProjet() {
        return projet;
    }

    public Date getDate() {
        return date;
    }

    // Methode pour sauvegarder le document dans la base de donnees
    public void saveToDatabase() throws SQLException {
        DocumentAdministratifDAO.addDocumentAdministratif(this);
    }

    // Methode pour afficher les details du document
    public void afficherDetails() {
        System.out.println("ID : " + id);
        System.out.println("Type : " + type);
        System.out.println("Contenu : " + content);
        System.out.println("Date : " + date);
        System.out.println("Projet associe : " + (projet != null ? projet.getTitre() : "Aucun projet"));
    }
}
