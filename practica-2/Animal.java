public class Animal {
    private String name;
    private int age;

    public void presentar() {
        System.out.println( name + " is " + age + " years old.");
    }

    public static void main(String[] args) {
        Animal perro = new Animal(); 
        perro.name = "Toby";
        perro.age = 3;
        perro.presentar();
    }
}