import java.util.*;

public class PetRepository<T extends Pet> {
    private final List<T> petList = new ArrayList<>();
    private final Map<Integer, T> petMap = new HashMap<>();

    public synchronized void addPet(T pet) {
        petList.add(pet);
        petMap.put(pet.getId(), pet);
    }

    public synchronized T getPetById(int id) {
        return petMap.get(id);
    }

    public synchronized List<T> getAllPets() {
        return Collections.unmodifiableList(new ArrayList<>(petList));
    }

    public synchronized List<T> filterBySpecies(String species) {
        List<T> filtered = new ArrayList<>();
        for (T pet : petList) {
            if (pet.getSpecies().equalsIgnoreCase(species)) {
                filtered.add(pet);
            }
        }
        return filtered;
    }
}