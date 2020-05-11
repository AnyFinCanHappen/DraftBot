package stan.draftbot;

public class Player
{
    private boolean isDrafted;
    private String name;
    public Player(String name) {
        this.isDrafted = false;
        this.name = name;
    }
    public boolean isDrafted() {
        return isDrafted;
    }
    public String getName() {
        return name;
    }

    public void setDrafted(boolean drafted) {
        isDrafted = drafted;
    }
}
