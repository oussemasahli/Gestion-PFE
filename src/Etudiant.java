
public class Etudiant {
    private String id;
    private String nom;
    private String numeroTel;
    private String email;
    private String formation;
    private String sexe;

    public Etudiant() {
        // Empty constructor
    }

    public Etudiant(String id, String nom, String numeroTel, String email, String formation, String sexe) {
        this.id = id;
        this.nom = nom;
        this.numeroTel = numeroTel;
        this.email = email;
        this.formation = formation;
        this.sexe = sexe;
    }

    // Getters and Setters
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
        this.nom = nom;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    // Display student details
    public void afficherDetails() {
        System.out.println("ID : " + id);
        System.out.println("Nom : " + nom);
        System.out.println("Numero de telephone : " + numeroTel);
        System.out.println("Email : " + email);
        System.out.println("Formation : " + formation);
    }
}
