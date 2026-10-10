public interface AdoptionProcessor {
    boolean processApplication(int petId, String applicantName) throws PetAdoptionException;
    String getStatus(int petId);
}