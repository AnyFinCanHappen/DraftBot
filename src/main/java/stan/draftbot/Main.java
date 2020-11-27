package stan.draftbot;

public class Main
{
    public static void main(String[] args)
    {
        try {
            if (args.length < 1) {
                throw new Exception("Must have Token as first argument.");
            } else {
                DraftBot bot = new DraftBot(args);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
