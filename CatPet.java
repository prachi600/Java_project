public class CatPet extends Pet {
    private boolean isIndoorOnly;

    public CatPet(int id, String name, String breed, String age, String gender, String size, String image, String description, String status, boolean isIndoorOnly) {
        super(id, name, "Cat", breed, age, gender, size, image, description, status);
        this.isIndoorOnly = isIndoorOnly;
    }

    @Override
    public String getCareInstructions() {
        return "Requires clean litter box, scratch pads, interactive play, and periodic coat brushing.";
    }
}