import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO implements DatabaseOperations<Pet> {

    @Override
    public void save(Pet pet) throws PetAdoptionException {
        String sql = "INSERT INTO pets (name, species, breed, age, gender, size, image, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pet.getName());
            stmt.setString(2, pet.getSpecies());
            stmt.setString(3, pet.getBreed());
            stmt.setString(4, pet.getAge());
            stmt.setString(5, pet.getGender());
            stmt.setString(6, pet.getSize());
            stmt.setString(7, pet.getImage());
            stmt.setString(8, pet.getDescription());
            stmt.setString(9, pet.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new PetAdoptionException("Error inserting pet into database", e);
        }
    }

    @Override
    public Pet findById(int id) throws PetAdoptionException {
        String sql = "SELECT * FROM pets WHERE id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractPetFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new PetAdoptionException("Error fetching pet with ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<Pet> findAll() throws PetAdoptionException {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM pets";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pets.add(extractPetFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new PetAdoptionException("Error retrieving pets", e);
        }
        return pets;
    }

    @Override
    public boolean updateStatus(int id, String status) throws PetAdoptionException {
        String sql = "UPDATE pets SET status = ? WHERE id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PetAdoptionException("Error updating pet status", e);
        }
    }

    private Pet extractPetFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String species = rs.getString("species");
        String breed = rs.getString("breed");
        String age = rs.getString("age");
        String gender = rs.getString("gender");
        String size = rs.getString("size");
        String image = rs.getString("image");
        String description = rs.getString("description");
        String status = rs.getString("status");

        if ("Dog".equalsIgnoreCase(species)) {
            return new DogPet(id, name, breed, age, gender, size, image, description, status, true);
        } else {
            return new CatPet(id, name, breed, age, gender, size, image, description, status, true);
        }
    }
}
