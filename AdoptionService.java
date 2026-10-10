import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class AdoptionService implements AdoptionProcessor {
    private final PetDAO petDAO = new PetDAO();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final ReentrantLock lock = new ReentrantLock();
    private final ConcurrentHashMap<Integer, String> statusMap = new ConcurrentHashMap<>();

    @Override
    public boolean processApplication(int petId, String applicantName) throws PetAdoptionException {
        threadPool.submit(() -> {
            lock.lock();
            try {
                statusMap.put(petId, "UNDER_REVIEW");
                Thread.sleep(1500);
                petDAO.updateStatus(petId, "APPLICATION_SUBMITTED");
                statusMap.put(petId, "APPROVED");
                System.out.println("[Thread " + Thread.currentThread().getName() + "] Processed pet ID: " + petId);
            } catch (Exception e) {
                statusMap.put(petId, "FAILED");
            } finally {
                lock.unlock();
            }
        });
        return true;
    }

    @Override
    public String getStatus(int petId) {
        return statusMap.getOrDefault(petId, "NO_ACTIVE_APPLICATION");
    }

    public void shutdown() {
        threadPool.shutdown();
    }
}