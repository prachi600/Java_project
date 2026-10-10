import java.util.List;

public interface DatabaseOperations<T> {
    void save(T entity) throws PetAdoptionException;
    T findById(int id) throws PetAdoptionException;
    List<T> findAll() throws PetAdoptionException;
    boolean updateStatus(int id, String status) throws PetAdoptionException;
}