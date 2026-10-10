public abstract class Pet {
    private int id;
    private String name;
    private String species;
    private String breed;
    private String age;
    private String gender;
    private String size;
    private String image;
    private String description;
    private String status;

    public Pet(int id, String name, String species, String breed, String age, String gender, String size, String image, String description, String status) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.size = size;
        this.image = image;
        this.description = description;
        this.status = status;
    }

    public abstract String getCareInstructions();

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public String getAge() { return age; }
    public String getGender() { return gender; }
    public String getSize() { return size; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}