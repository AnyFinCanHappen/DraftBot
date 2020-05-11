package stan.draftbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;

public class DraftBot
{
    private final JDA jda;
    public DraftBot() throws LoginException
    {
        this.jda = new JDABuilder(AccountType.BOT).setToken("NzAyNzA0MDEzNDkzMjcyNjE3.XqD7RQ.QP1iBZbrk8moD7n_RV0WHWA9GEk").build();
        this.jda.getPresence().setStatus(OnlineStatus.ONLINE);
        this.jda.addEventListener(new Commands());
    }

}
