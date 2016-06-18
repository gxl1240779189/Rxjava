package Model;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class Pet {
    private String Petname;

    public Pet(String petname) {
        Petname = petname;
    }

    public String getPetname() {
        return Petname;
    }

    public void setPetname(String petname) {
        Petname = petname;
    }
}
