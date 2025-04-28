import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Soutenance {
    private String id;
    private Etudiant etudiant; // L'etudiant qui presente le projet
    private Jury jury; // Le jury qui evalue la soutenance
    private Date date; // Date de la soutenance
    private String salle; // Salle où a lieu la soutenance
    private String projectId; // ID du projet
    private Encadrant encadrant; // Encadrant (supervisor)

    // Constructeurs
    public Soutenance() {}

    public Soutenance(String id, Etudiant etudiant, Jury jury, Date date, String salle, String projectId, Encadrant encadrant) {
        this.id = id;
        this.etudiant = etudiant;
        this.jury = jury;
        this.date = date;
        this.salle = salle;
        this.projectId = projectId;
        this.encadrant = encadrant;
    }

    // Overloaded constructor for cases where Encadrant is not provided
    public Soutenance(String id, Etudiant etudiant, Jury jury, Date date, String salle, String projectId) {
        this(id, etudiant, jury, date, salle, projectId, null);
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Jury getJury() {
        return jury;
    }

    public void setJury(Jury jury) {
        this.jury = jury;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Encadrant getEncadrant() {
        return encadrant;
    }

    public void setEncadrant(Encadrant encadrant) {
        this.encadrant = encadrant;
    }

    // Derived property: supervisorName
    public String getSupervisorName() {
        return encadrant != null ? encadrant.getNom() : "N/A";
    }

    // Derived property: formattedDate
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return date != null ? formatter.format(date) : "N/A";
    }

    // Derived property: etudiantName
    public String getEtudiantName() {
        return etudiant != null ? etudiant.getNom() : "N/A";
    }

    // Derived property: juryName
    public String getJuryName() {
        return jury != null ? jury.getName() : "N/A";
    }

    // Derived property: projectName
    public String getProjectName() {
        try {
            Projet project = ProjetDAO.getProjetById(projectId); // Fetch the project by ID
            return project != null ? project.getTitre() : "N/A"; // Return the project title
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur";
        }
    }
}
