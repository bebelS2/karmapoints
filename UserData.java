public class UserData {
    private static UserData instance;
    private int idFinanciadorLogado;
    private int idVolLogado;
    private int idOngLogado;

    private UserData() {
        // Construtor privado para evitar instanciação direta
    }

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public int getIdFinanciadorLogado() {
        return idFinanciadorLogado;
    }

    public void setIdFinanciadorLogado(int idFinanciadorLogado) {
        this.idFinanciadorLogado = idFinanciadorLogado;
    }

    public int getIdVolLogado() {
        return idVolLogado;
    }

    public void setIdVolLogado(int idVolLogado) {
        this.idVolLogado = idVolLogado;
    }

    public int getIdOngLogado() {
        return idOngLogado;
    }

    public void setIdOngLogado(int idOngLogado) {
        this.idOngLogado = idOngLogado;
    }
}
