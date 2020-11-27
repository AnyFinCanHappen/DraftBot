package stan.draftbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlayerList
{
    private final int teamA_int = 0;
    private final int teamB_int = 1;

    private ArrayList<Player> listOfPlayers;
    private ArrayList<Player> teamA;
    private ArrayList<Player> teamB;

    private int nominateTracker;
    private int pickTracker_Team;
    private int pickTracker_Num;
    private boolean draftMode;



    public PlayerList(){
        this.listOfPlayers = new ArrayList<>(10);
        this.teamA = new ArrayList<>(5);
        this.teamB = new ArrayList<>(5);
        this.nominateTracker = 0;
        this.pickTracker_Num = 0;
        this.draftMode = false;
    }

    public void addPlayers(String name, GuildMessageReceivedEvent event)
    {
        if(!draftMode) {
            listOfPlayers.add(new Player(name));
            sendMessage(" has been added!", event);
            displayPlayers(event);
        }
        else {
            sendMessage("Draft mode already started, cannot add players. \n" +
                    "Use command !restart to turn off draft mode.", event);
        }
    }

    public void addAll(GuildMessageReceivedEvent event)
    {
        if(!draftMode) {
            ArrayList<String> names = new ArrayList<>();
            for (VoiceChannel channel : event.getGuild().getVoiceChannels()) {
                List<Member> members = channel.getMembers();
                for (Member m : members) {
                    names.add(m.getUser().getName());
                }
            }
            if (names.size() == 0) {
                sendMessage("No Players added, voice channels empty :(", event);
            } else {
                StringBuilder allNames = new StringBuilder();
                for (int i = 0; i < names.size(); i++) {
                    if (i == names.size() - 1) {
                        allNames.append(names.get(i)).append(".");
                    } else {
                        allNames.append(names.get(i)).append(", ");
                    }
                    this.listOfPlayers.add(new Player(names.get(i)));
                }
                sendMessage("Players added: " + allNames.toString(), event);
            }
        }
        else{
            sendMessage("Draft mode already started, cannot add players. \n" +
                    "Use command !restart to turn off draft mode.", event);
        }
    }

    public void removePlayer(String message, GuildMessageReceivedEvent event)
    {
        try{
            if(!draftMode)
            {
                int number = Integer.parseInt(message);
                if(this.listOfPlayers.size() == 0){
                    sendMessage("There are no more players.", event);
                }
                else if (number < 1 || number > this.listOfPlayers.size()) {
                    sendMessage(message + " is an invalid input. Must be a value " +
                            1 + "-" + (this.listOfPlayers.size()), event);
                }
                else{
                    String name = listOfPlayers.remove(number - 1).getName();
                    sendMessage("Player " + name + " has been removed.", event);
                    displayPlayers(event);
                }
            }
            else
            {
                sendMessage("Draft mode already started, cannot remove players. \n" +
                        "Use command !restart to turn off draft mode.", event);
            }
        }
        catch (NumberFormatException e)
        {
            sendMessage(message + " is an invalid input. Must be a value " +
                    1 + "-" + (this.listOfPlayers.size()), event);
        }
    }

    public void startDraft(GuildMessageReceivedEvent event)
    {
        if(this.listOfPlayers.size() == 10 && !this.draftMode){
            this.draftMode = true;
            sendMessage("Draft has started.", event);
            Random random = new Random();
            int result = random.nextInt(2);
            if(result == 1){
                this.pickTracker_Team = teamB_int;
            }
            else{
                this.pickTracker_Team = teamA_int;
            }
            displayPlayers(event);
            sendMessage("Choose two team captains.\n" +
                    "For an example !nominate 1 to nominate " + listOfPlayers.get(0).getName() + ".", event);
        }
        else {
            if(this.draftMode) {
                sendMessage("Draft mode already started.", event);
            }
            else if(this.listOfPlayers.size() < 10){
                sendMessage("Not enough players, need " + (10 - this.listOfPlayers.size()) + " more players.", event);
            }
            else{
                sendMessage("Too many players, remove " + (this.listOfPlayers.size() - 10) + " players.", event);
            }
        }
    }

    public void nominate(String message, GuildMessageReceivedEvent event) {
        try {
            if (draftMode) {
                int pick_choice = Integer.parseInt(message);
                if (pick_choice < 1 || pick_choice > this.listOfPlayers.size()) {
                    sendMessage(message + " is an invalid input. Must be a value " +
                            1 + "-" + (this.listOfPlayers.size() - this.nominateTracker), event);
                } else {
                    if (this.nominateTracker == 0) {
                        this.listOfPlayers.get(pick_choice - 1).setDrafted(true);
                        this.teamA.add(listOfPlayers.get(pick_choice - 1));
                        this.listOfPlayers.remove(pick_choice - 1);
                        String name = this.teamA.get(0).getName();
                        sendMessage(name + " is Team A's captain. \n" +
                                "Nominate Team B's captain.", event);
                        this.nominateTracker++;
                        displayPlayers(event);
                    } else if (this.nominateTracker == 1) {
                        this.listOfPlayers.get(pick_choice - 1).setDrafted(true);
                        this.teamB.add(listOfPlayers.get(pick_choice - 1));
                        this.listOfPlayers.remove(pick_choice - 1);
                        String name = this.teamB.get(0).getName();
                        sendMessage(name + " is Team B's captain.", event);
                        this.nominateTracker++;
                        displayPlayers(event);
                        sendMessage("Draft has started.", event);
                        sendMessage("Use command !pick # to pick a player\n" +
                                "e.g. !pick 1 to pick: " + this.listOfPlayers.get(0).getName(),event);
                        if(pickTracker_Team == 0)
                        {
                            sendMessage("Team A has the first pick.", event);
                        }
                        else{
                            sendMessage("Team B has the first pick", event);
                        }
                    } else {
                        sendMessage("Team captains already nominated.", event);
                    }

                }
            }
            else{
            sendMessage("Must initiate draft mode.\n" +
                    "Use !draft command to initiate.", event);
            }
        }
        catch(NumberFormatException e)
        {
             sendMessage(message + " is an invalid input. Must be a value " +
             1 + "-" + this.listOfPlayers.size(), event);
        }
    }
    public void pickPlayer(String message, GuildMessageReceivedEvent event)
    {
        try
        {
            if(draftMode && nominateTracker == 2)
            {
                int pick_choice = Integer.parseInt(message);
                int numOfPicked = this.teamA.size() + this.teamB.size();
                if(pick_choice < 1 || pick_choice > this.listOfPlayers.size())
                {
                    sendMessage(message + " is an invalid input. Must be a value " +
                            1 + "-" + (this.listOfPlayers.size() - numOfPicked), event);
                }
                else
                {
                    if(pickTracker_Num == 0){
                        pick(pick_choice);        //first pick
                        swapPickTracker_Team();
                        pickTracker_Num++;
                        displayPlayers(event);
                        display_which_team_pick(event);
                    }
                    else if(pickTracker_Num == 1 || pickTracker_Num == 2){
                        pick(pick_choice);            //2nd pick (picks twice)
                        if (pickTracker_Num != 1) {
                            swapPickTracker_Team();
                        }
                        pickTracker_Num++;
                        displayPlayers(event);
                        display_which_team_pick(event);
                    }
                    else if(pickTracker_Num == 3 || pickTracker_Num == 4){
                        pick(pick_choice);    //first pick (picks twice)
                        if (pickTracker_Num != 3) {
                            swapPickTracker_Team();
                        }
                        pickTracker_Num++;
                        displayPlayers(event);
                        display_which_team_pick(event);
                    }
                    else if(pickTracker_Num == 5 || pickTracker_Num == 6)
                    {
                        pick(pick_choice);    //2nd pick (picks twice), has 5 players already
                        if (pickTracker_Num != 5) {
                            swapPickTracker_Team();
                        }
                        pickTracker_Num++;
                        displayPlayers(event);
                        display_which_team_pick(event);
                    }
                    else if(pickTracker_Num == 7)
                    {
                        pick(pick_choice);
                        sendMessage("Teams drafted!.", event);
                        displayPlayers(event);
                        resetLocalVar();
                    }
                }
            }
            else
            {
                if(!draftMode){
                    sendMessage("Must initiate draft mode.\n" +
                            "Use !draft command to initiate.", event);
                }
                else{
                    sendMessage("Must nominate captains.\n" +
                            "Need " + (2 - this.nominateTracker) +" captains.\n" +
                            "Use !nominate # to nominate.", event);
                }
            }
        }
        catch (NumberFormatException e)
        {
            sendMessage(message + " is an invalid input. Must be a value " +
                    1 + "-" + this.listOfPlayers.size(), event);
        }
    }

    public void resetCommand(GuildMessageReceivedEvent event)
    {
        resetLocalVar();
        sendMessage("Draft reset.",event);
    }

    public void emptyCommand(GuildMessageReceivedEvent event)
    {
        resetLocalVar();
        this.listOfPlayers.clear();
        sendMessage("All players removed.", event);
    }


    public void resetLocalVar(){
        this.nominateTracker = 0;
        this.pickTracker_Team = 0;
        this.pickTracker_Num = 0;
        draftMode = false;
        int team_size = teamA.size();
        for(int i = team_size - 1; i >= 0; i --)
            listOfPlayers.add(teamA.remove(i));
        for(int i = team_size - 1; i >= 0; i --)
            listOfPlayers.add(teamB.remove(i));
        for(Player p : listOfPlayers)
            p.setDrafted(false);
    }
    private void pick(int player)
    {
        if(pickTracker_Team == 0)
        {
            listOfPlayers.get(player - 1).setDrafted(true);
            teamA.add(listOfPlayers.get(player - 1));
        }
        else if(pickTracker_Team == 1) {
            listOfPlayers.get(player - 1).setDrafted(true);
            teamB.add(listOfPlayers.get(player - 1));
        }
        listOfPlayers.remove(player - 1);
    }

    private void display_which_team_pick(GuildMessageReceivedEvent event)
    {
        if(pickTracker_Team == 0){
            String captain = teamA.get(0).getName();
            sendMessage("Team A's captain: " + captain + " turn to pick.", event);
        }
        else
        {
            String captain = teamB.get(0).getName();
            sendMessage("Team B's captain: " + captain + " turn to pick.", event);
        }
        sendMessage("Use command !pick # to pick a player\n" +
                "e.g. !pick 1 to pick: " + this.listOfPlayers.get(0).getName(),event);
    }

    private void swapPickTracker_Team()
    {
        if(pickTracker_Team == 1){
            pickTracker_Team = 0;
        }
        else if (pickTracker_Team == 0){
            pickTracker_Team = 1;
        }
    }



    public void sendMessage(String message, GuildMessageReceivedEvent event)
    {
        //event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(message).queue();
    }

    public void displayPlayers(GuildMessageReceivedEvent event)
    {
        StringBuilder players = new StringBuilder();
        StringBuilder teamAList = new StringBuilder();
        StringBuilder teamBList = new StringBuilder();
        EmbedBuilder info = new EmbedBuilder();
        int counter = 0;
        for (Player listOfPlayer : listOfPlayers) {
            players.append(counter + 1).append(". ").append(listOfPlayer.getName()).append("\n");
            counter++;
        }
        for(int i = 0; i < teamA.size(); i ++){
            teamAList.append(i + 1).append(". ").append(teamA.get(i).getName()).append("\n");
        }
        for(int i = 0; i < teamB.size(); i ++){
            teamBList.append(i + 1).append(". ").append(teamB.get(i).getName()).append("\n");
        }
        info.addField("Team A    ", teamAList.toString(), true);
        info.addField("Players", players.toString(), true);
        info.addField("Team B", teamBList.toString(), true);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(info.build()).queue();
    }

    public void instructionsCommand(GuildMessageReceivedEvent event)
    {
        String instructions =
                "To use the draftBot, you must have no more or no less than 10 players added in order to start the draft.\n" +
                        "Use command: !add # to add players.\n" +
                        "Also, you can use !display to see players that have been added.\n" +
                        "After adding 10 players, you may start the draft.\n" +
                        "Use command: !draft to start.\n" +
                        "When draft has started, you must nominate two captains.\n" +
                        "Use command !nominate # to choose a captain.\n" +
                        "Once captains have been nominated, captain with first pick is chosen randomly.\n" +
                        "Pick ordering follows this order: \n " +
                        "1st Pick: picks once\n" +
                        "2nd Pick: picks twice\n" +
                        "1st Pick: picks twice\n" +
                        "2nd Pick: picks twice\n" +
                        "1st Pick: picks once (all players have been chosen). \n" +
                        "Use command !pick # to choose players. \n"+
                        "Once all players have been assigned a team, the draft is done.";
        sendMessage(instructions,event);
    }
    public void listCommands(GuildMessageReceivedEvent event)
    {
        String commands = "!instructions:\tInstructions on how to use the draft bot.\n" +
                "\n" +
                "!add #:\tAdd players to the draft.\n" +
                "\n" +
                "!display: Show all players in the draft.\n" +
                "\n" +
                "!remove #: Remove players from the draft.\n" +
                "\n" +
                "!draft: Start draft mode, must have no more or no less than 10 players.\n" +
                "\n" +
                "!reset: Reset draft. This will remove all players from Team A and B.\n" +
                "\tDraft mode will be turned off.\n" +
                "\n" +
                "!addAll: Add all players from all channels, like Kevin's .addAll\n" +
                "\n" +
                "!empty: Completely remove all players from the draft.\n" +
                "\n" +
                "Draft Mode Commands (Must be in draft mode to use, !draft to turn on draft mode)\n" +
                "\n" +
                "!reset: Reset draft. This will remove all players from Team A and B.\n" +
                "\tDraft mode will be turned off.\n" +
                "\n" +
                "!nominate #: Nominate a player for team captain." +
                "\n" +
                "!pick #: Add player to a team.";

        sendMessage(commands,event);
    }
    public void runTest(GuildMessageReceivedEvent event)
    {
        emptyCommand(event);
        String playerName;
        for(int i = 1; i <= 10; i ++)
        {
            playerName = "player" + i;
            //this.listOfPlayers.add(new Player(playerName));
            addPlayers(playerName,event);
        }
        removePlayer("1",event);
        removePlayer("6", event);
        addPlayers("player1", event);
        addPlayers("planer6", event);
        startDraft(event);
        nominate("1",event);
        nominate("1", event);
        Random rand = new Random();
        int nextInt;
        for(int i = 0; i < 8; i ++){
            nextInt = rand.nextInt(listOfPlayers.size()) + 1;
            pickPlayer(Integer.toString(nextInt), event);
        }
        displayPlayers(event);
    }

    /*
    TO DO:
        done: remove command
        done addALl command
        done test restart command

        help command

     */

}
