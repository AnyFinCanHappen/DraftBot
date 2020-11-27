package stan.draftbot;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter
{
    private final String prefix = "!";
    private PlayerList playerList;
    private boolean isDeveloperMode;

    public Commands(String developerMode){
        this.playerList = new PlayerList();
        this.isDeveloperMode = developerMode.equals("-d");
    }
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        User author= event.getMessage().getAuthor();
        if(author.isBot())
            return;
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if(args[0].charAt(0) != '!')
            return;
        if(args[0].equals(prefix + "add")) {
            this.playerList.addPlayers(args[1], event);
        }
        else if(args[0].equals(prefix + "display")) {
            this.playerList.displayPlayers(event);
        }
        else if(args[0].equals(prefix + "nominate")) {
            this.playerList.nominate(args[1], event);
        }
        else if(args[0].equals(prefix + "draft")) {
            this.playerList.startDraft(event);
        }
        else if(args[0].equals(prefix + "pick")){
            this.playerList.pickPlayer(args[1], event);
        }
        else if(args[0].equals(prefix + "test")) {
            if(this.isDeveloperMode) {
                this.playerList.runTest(event);
            }
            else {
                this.playerList.sendMessage("This commands can only be used in developer mode.", event);
            }
        }
        else if( args[0].equals(prefix + "remove")){
            this.playerList.removePlayer(args[1], event);
        }
        else if(args[0].equals(prefix + "reset")){
            this.playerList.resetCommand(event);
        }
        else if(args[0].equals(prefix + "addAll")){
            this.playerList.addAll(event);
        }
        else if(args[0].equals(prefix + "empty")){
            this.playerList.emptyCommand(event);
        }
        else if(args[0].equals(prefix + "commands")){
            this.playerList.listCommands(event);
        }
        else if(args[0].equals(prefix + "instructions")){
            this.playerList.instructionsCommand(event);
        }
        else{
            this.playerList.sendMessage("Invalid Command.\n Use !commands to list all commands.", event);
        }
    }
}
