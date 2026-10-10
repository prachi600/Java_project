public class DogPet extends Pet {
    private boolean isLeashTrained;

    public DogPet(int id, String name, String breed, String age, String gender, String size, String image, String description, String status, boolean isLeashTrained) {
        super(id, name, "Dog", breed, age, gender, size, image, description, status);
        this.isLeashTrained = isLeashTrained;
    }

    @Override
    public String getCareInstructions() {
        return "Requires 2 daily outdoor walks, routine vaccinations, and high-protein canine diet.";
    }
}