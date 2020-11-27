package stan.draftbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;

public class DraftBot
{
    private final JDA jda;
    public DraftBot(String[] args) throws LoginException
    {
        String devArgument;
        if(args.length > 1){
            devArgument = args[1];
        }
        else{
            devArgument = "";
        }
        this.jda = JDABuilder.createLight(args[0])
                .addEventListeners(new Commands(devArgument))
                .setStatus(OnlineStatus.ONLINE)
                .build();
    }

}
