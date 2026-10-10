import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PetAdoptionGUI extends JFrame {
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionService adoptionService = new AdoptionService();
    private JTable petTable;
    private DefaultTableModel tableModel;

    public PetAdoptionGUI() {
        setTitle("Online Pet Adoption Platform - Admin GUI");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        loadPetData();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Pet Adoption Management Panel", JLabel.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 22));
        titleLabel.setForeground(new Color(79, 70, 229));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Species", "Breed", "Age", "Gender", "Size", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        petTable = new JTable(tableModel);
        petTable.setRowHeight(28);

        mainPanel.add(new JScrollPane(petTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton refreshBtn = new JButton("Refresh List");
        JButton approveBtn = new JButton("Approve Application");

        refreshBtn.addActionListener(e -> loadPetData());
        approveBtn.addActionListener(e -> approveSelectedPet());

        btnPanel.add(refreshBtn);
        btnPanel.add(approveBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPetData() {
        try {
            tableModel.setRowCount(0);
            List<Pet> pets = petDAO.findAll();
            for (Pet pet : pets) {
                tableModel.addRow(new Object[]{
                    pet.getId(), pet.getName(), pet.getSpecies(), pet.getBreed(),
                    pet.getAge(), pet.getGender(), pet.getSize(), pet.getStatus()
                });
            }
        } catch (PetAdoptionException e) {
            JOptionPane.showMessageDialog(this, "Failed to load pets: " + e.getMessage());
        }
    }

    private void approveSelectedPet() {
        int selectedRow = petTable.getSelectedRow();
        if (selectedRow != -1) {
            int petId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                adoptionService.processApplication(petId, "Admin Approval");
                JOptionPane.showMessageDialog(this, "Application processing started!");
                loadPetData();
            } catch (PetAdoptionException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a pet first.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PetAdoptionGUI().setVisible(true));
    }
}