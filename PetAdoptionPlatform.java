import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

// ============================================================================
// 1. OOP IMPLEMENTATION (Custom Exceptions, Interfaces & Abstract Base)
// ============================================================================

class AdoptionException extends Exception {
    public AdoptionException(String message) {
        super(message);
    }
}

interface Adoptable {
    String getAdoptionDetails();
    boolean isAvailable();
    void setAvailable(boolean available);
}

abstract class Pet implements Adoptable {
    private int id;
    private String name;
    private int age;
    private boolean available;

    public Pet(int id, String name, int age, boolean available) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.available = available;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public void setAvailable(boolean available) { this.available = available; }

    public abstract String getSpecies();
}

class Dog extends Pet {
    private String breed;

    public Dog(int id, String name, int age, String breed, boolean available) {
        super(id, name, age, available);
        this.breed = breed;
    }

    public String getBreed() { return breed; }

    @Override
    public String getSpecies() { return "Dog"; }

    @Override
    public String getAdoptionDetails() {
        return "Dog [Name=" + getName() + ", Breed=" + breed + ", Age=" + getAge() + "]";
    }
}

class Cat extends Pet {
    private boolean indoor;

    public Cat(int id, String name, int age, boolean indoor, boolean available) {
        super(id, name, age, available);
        this.indoor = indoor;
    }

    @Override
    public String getSpecies() { return "Cat"; }

    @Override
    public String getAdoptionDetails() {
        return "Cat [Name=" + getName() + ", Indoor=" + indoor + ", Age=" + getAge() + "]";
    }
}

// ============================================================================
// 2. COLLECTIONS & GENERICS (Generic Repository + Stream API Filtering)
// ============================================================================

class Repository<T extends Pet> {
    private final List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public List<T> filterAvailable() {
        return items.stream()
                    .filter(Pet::isAvailable)
                    .collect(Collectors.toList());
    }

    public Optional<T> findById(int id) {
        return items.stream().filter(p -> p.getId() == id).findFirst();
    }
}

// ============================================================================
// 3. DATABASE CONNECTIVITY (JDBC) & OPERATIONS (DAO Pattern)
// ============================================================================

class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:pet_adoption.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createPetsTable = "CREATE TABLE IF NOT EXISTS pets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "species TEXT NOT NULL, " +
                "age INTEGER, " +
                "available BOOLEAN);";

        String createAppsTable = "CREATE TABLE IF NOT EXISTS applications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pet_id INTEGER, " +
                "applicant_name TEXT, " +
                "status TEXT);";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createPetsTable);
            stmt.execute(createAppsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class PetDAO {
    public void addPet(String name, String species, int age) throws SQLException {
        String sql = "INSERT INTO pets(name, species, age, available) VALUES(?, ?, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, species);
            pstmt.setInt(3, age);
            pstmt.executeUpdate();
        }
    }

    public List<Pet> getAllPets() throws SQLException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM pets";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String species = rs.getString("species");
                int age = rs.getInt("age");
                boolean available = rs.getBoolean("available");

                if ("Dog".equalsIgnoreCase(species)) {
                    list.add(new Dog(id, name, age, "Unknown Breed", available));
                } else {
                    list.add(new Cat(id, name, age, true, available));
                }
            }
        }
        return list;
    }

    public void updateAvailability(int petId, boolean available) throws SQLException {
        String sql = "UPDATE pets SET available = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, available);
            pstmt.setInt(2, petId);
            pstmt.executeUpdate();
        }
    }
}

class ApplicationDAO {
    public void createApplication(int petId, String applicantName) throws SQLException {
        String sql = "INSERT INTO applications(pet_id, applicant_name, status) VALUES(?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, petId);
            pstmt.setString(2, applicantName);
            pstmt.executeUpdate();
        }
    }
}

// ============================================================================
// 4. MULTITHREADING & SYNCHRONIZATION (ReentrantLock + Thread Processing)
// ============================================================================

class AdoptionProcessor {
    private final ReentrantLock lock = new ReentrantLock();
    private final PetDAO petDAO = new PetDAO();
    private final ApplicationDAO appDAO = new ApplicationDAO();

    public void processAdoptionRequest(int petId, String applicantName) {
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " processing adoption for Pet ID: " + petId);
                appDAO.createApplication(petId, applicantName);
                petDAO.updateAvailability(petId, false);
                Thread.sleep(1000);
                System.out.println("Adoption successfully processed for " + applicantName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
    }
}

// ============================================================================
// 5. JAVA GUI DASHBOARD (Swing Interface)
// ============================================================================

class PetAdoptionGUI extends JFrame {
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionProcessor processor = new AdoptionProcessor();
    private JTable petTable;
    private DefaultTableModel tableModel;

    public PetAdoptionGUI() {
        setTitle("Online Pet Adoption Platform (Admin & Adopter GUI)");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JTextField txtName = new JTextField(8);
        JTextField txtSpecies = new JTextField(8);
        JTextField txtAge = new JTextField(4);
        JButton btnAdd = new JButton("Add Pet");

        topPanel.add(new JLabel("Name:"));
        topPanel.add(txtName);
        topPanel.add(new JLabel("Species:"));
        topPanel.add(txtSpecies);
        topPanel.add(new JLabel("Age:"));
        topPanel.add(txtAge);
        topPanel.add(btnAdd);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Species", "Age", "Available"}, 0);
        petTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(petTable);

        JPanel bottomPanel = new JPanel();
        JTextField txtApplicant = new JTextField(10);
        JButton btnAdopt = new JButton("Submit Adoption Request");

        bottomPanel.add(new JLabel("Applicant Name:"));
        bottomPanel.add(txtApplicant);
        bottomPanel.add(btnAdopt);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText();
                String species = txtSpecies.getText();
                int age = Integer.parseInt(txtAge.getText());
                petDAO.addPet(name, species, age);
                loadPetData();
                JOptionPane.showMessageDialog(this, "Pet added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding pet: " + ex.getMessage());
            }
        });

        btnAdopt.addActionListener(e -> {
            int selectedRow = petTable.getSelectedRow();
            if (selectedRow != -1) {
                int petId = (int) tableModel.getValueAt(selectedRow, 0);
                String applicant = txtApplicant.getText();
                if (!applicant.isEmpty()) {
                    processor.processAdoptionRequest(petId, applicant);
                    JOptionPane.showMessageDialog(this, "Adoption request submitted in background thread.");
                    SwingUtilities.invokeLater(() -> {
                        try { Thread.sleep(1200); loadPetData(); } catch (Exception ignored) {}
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter applicant name.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a pet from the table first.");
            }
        });

        loadPetData();
    }

    private void loadPetData() {
        try {
            tableModel.setRowCount(0);
            List<Pet> pets = petDAO.getAllPets();
            for (Pet p : pets) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getSpecies(), p.getAge(), p.isAvailable()});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// ============================================================================
// 6. SERVLET / WEB INTEGRATION (HTTP Endpoint Handler)
// ============================================================================

class PetAdoptionServlet implements HttpHandler {
    private final PetDAO petDAO = new PetDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder jsonResponse = new StringBuilder("[");
        try {
            List<Pet> pets = petDAO.getAllPets();
            for (int i = 0; i < pets.size(); i++) {
                Pet p = pets.get(i);
                jsonResponse.append(String.format("{\"id\":%d, \"name\":\"%s\", \"species\":\"%s\", \"available\":%b}",
                        p.getId(), p.getName(), p.getSpecies(), p.isAvailable()));
                if (i < pets.size() - 1) jsonResponse.append(",");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jsonResponse.append("]");

        byte[] responseBytes = jsonResponse.toString().getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}

// ============================================================================
// 7. MAIN ENTRY POINT
// ============================================================================

public class PetAdoptionPlatform {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/api/pets", new PetAdoptionServlet());
            server.setExecutor(null);
            server.start();
            System.out.println("Web Servlet Endpoint running at: http://localhost:8080/api/pets");
        } catch (IOException e) {
            System.out.println("Failed to launch HTTP Servlet: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            PetAdoptionGUI gui = new PetAdoptionGUI();
            gui.setVisible(true);
        });
    }
}