import java.math.BigDecimal;
import java.sql.SQLException;

public class Note {
    private int id;
    private String idEtudiant;
    private String idEncadrant;
    private String idProjet; // Corrected field name
    private BigDecimal noteTravail;
    private BigDecimal noteRapport;
    private BigDecimal notePresentation;
    private String appreciation;

    // Constructor
    public Note() {}

    public Note(int id, String idEncadrant, String idEtudiant, String idProjet, BigDecimal noteTravail, BigDecimal noteRapport, BigDecimal notePresentation, String appreciation) {
        this.id = id;
        this.idEncadrant = idEncadrant;
        this.idEtudiant = idEtudiant;
        this.idProjet = idProjet; // Initialize corrected field
        this.noteTravail = noteTravail;
        this.noteRapport = noteRapport;
        this.notePresentation = notePresentation;
        this.appreciation = appreciation;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(String idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public String getIdEncadrant() {
        return idEncadrant;
    }

    public void setIdEncadrant(String idEncadrant) {
        this.idEncadrant = idEncadrant;
    }

    public String getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(String idProjet) {
        this.idProjet = idProjet;
    }

    public BigDecimal getNoteTravail() {
        return noteTravail;
    }

    public void setNoteTravail(BigDecimal noteTravail) {
        this.noteTravail = noteTravail;
    }

    public BigDecimal getNoteRapport() {
        return noteRapport;
    }

    public void setNoteRapport(BigDecimal noteRapport) {
        this.noteRapport = noteRapport;
    }

    public BigDecimal getNotePresentation() {
        return notePresentation;
    }

    public void setNotePresentation(BigDecimal notePresentation) {
        this.notePresentation = notePresentation;
    }

    public String getAppreciation() {
        return appreciation;
    }

    public void setAppreciation(String appreciation) {
        this.appreciation = appreciation;
    }

    // Calcul de la note finale (avec des poids ajustables)
    public BigDecimal calculerNoteFinale() {
        BigDecimal travail = noteTravail.multiply(new BigDecimal("0.4"));
        BigDecimal rapport = noteRapport.multiply(new BigDecimal("0.3"));
        BigDecimal presentation = notePresentation.multiply(new BigDecimal("0.3"));
        return travail.add(rapport).add(presentation);
    }

    public void saveToDatabase() throws SQLException {
        boolean success = NoteDAO.ajouterNote(this);
        if (success) {
            System.out.println("Note ajoutee avec succes !");
        } else {
            System.out.println("echec de l'ajout de la note.");
        }
    }

    // New method to get the Etudiant object
    public Etudiant getEtudiant() throws SQLException {
        return EtudiantDAO.getEtudiantById(this.idEtudiant);
    }

    @Override
    public String toString() {
        return "Note {" +
               "ID=" + id +
               ", Encadrant ID='" + idEncadrant + '\'' +
               ", Etudiant ID='" + idEtudiant + '\'' +
               ", Project ID='" + idProjet + '\'' +
               ", Travail=" + noteTravail +
               ", Rapport=" + noteRapport +
               ", Presentation=" + notePresentation +
               ", Appreciation='" + appreciation + '\'' +
               '}';
    }
}
