package org.example;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.lang.Math;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

//JDBC.connect();
//event.getChannel().sendMessage("Hi").queue();
// event.getChannel().sendMessage(user.getAsMention()).queue(); // Display message
// event.getChannel().sendMessageEmbeds(embedCharInfo.build()).queue(); // Display embed

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException, SQLException {
        JDABuilder jda = JDABuilder.createDefault("MTE3NDM0NzAzNjIwNDc0ODgzMA.Gma3Lg.aJoRZ567SCe4Y685JjXfD8lUE2ZhFe13uazjy8").enableIntents(GatewayIntent.MESSAGE_CONTENT);
        jda.addEventListeners(new Main());
        jda.build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor(); // Shortcut
        String command = event.getMessage().getContentRaw(); // Get the user input
        int statPoint, inputStatPoint;
        String[] extractStatPoint = new String[2]; // For a string split
        boolean isNewUser;

        if (user.isBot()) {
            return;
        } else {
            isNewUser = checkExistingUser(user); // Check if the user is registered
        }

        if (command.matches("^\\.[a-z]+$") || command.matches("^\\.[a-z]{3}\\s\\d+$")) { // If the command starts with a . followed by any amount of lower case characters; also allows for a . followed by 3 lower case characters as well as a whitespace and digits
            System.out.println(CurrentTime.getCurrentTime() + "[USER] " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay()); // Only display in console if valid commands are sent

            if (isNewUser && !command.equals(".help")) { // New user section
                if (command.equals(".create")) { // First creation of a character if the user is not registered and enters .create as the first command
                    createFirstCharacter(event, user);
                } else { // If the user is not registered and tries to enter a command other than .create or .help
                    event.getChannel().sendMessage("You are not registered, type .create to get started").queue();
                }
            }

            if (command.equals(".help")) { // Accessible by anyone
                displayHelpMenu(event, user);
            }

            if (!isNewUser) { // Commands are accessible only if the user exists in the DB
                switch (command) {
                    case ".create": // If the existing user tries to create a new character
                        displayErrorExistingChar(event, user);
                        break;
                    case ".info":
                        displayCharInfo(event, user);
                        break;
                    case ".explore":
                        goExplore(event, user);
                        break;
                }

                // START OF STAT ALLOCATION SECTION
                if (command.matches("^\\.str\\s\\d+$")) { // If the command starts with !str with a space and at least one or multiple digits
                    statPoint = selectStatPtsDB(user); // Stat points of the character in the DB
                    inputStatPoint = getInputStatPoint(user, command, extractStatPoint); // To recuperate the digits only

                    if (inputStatPoint > statPoint) { // If the user is trying to allocate an amount of points that he does not have
                        displayErrorAllocPt(event, user);
                    } else { // Updating stats and deducting available stat points
                        try {
                            JDBC.connect("UPDATE `character` SET `strength` = `strength` + " + inputStatPoint + " WHERE `discord_id` = " + user.getId(), false); // Adding the stat points
                            JDBC.connect("UPDATE `character` SET `stat_point` = `stat_point` - " + inputStatPoint + " WHERE `discord_id` = " + user.getId(), false); // Deducting the available stat points
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (command.matches("^\\.con\\s\\d+$")) { // If the command starts with .con with a space and at least one or multiple digits
                    statPoint = selectStatPtsDB(user); // Stat points of the character in the DB
                    inputStatPoint = getInputStatPoint(user, command, extractStatPoint); // To recuperate the digits only

                    if (inputStatPoint > statPoint) { // If the user is trying to allocate an amount of points that he does not have
                        displayErrorAllocPt(event, user);
                    } else { // Updating stats and deducting available stat points
                        // UPDATE STATEMENT
                    }
                } else if (command.matches("^\\.dex\\s\\d+$")) { // If the command starts with .dex with a space and at least one or multiple digits
                    statPoint = selectStatPtsDB(user); // Stat points of the character in the DB
                    inputStatPoint = getInputStatPoint(user, command, extractStatPoint); // To recuperate the digits only

                    if (inputStatPoint > statPoint) { // If the user is trying to allocate an amount of points that he does not have
                        displayErrorAllocPt(event, user);
                    } else { // Updating stats and deducting available stat points
                        // UPDATE STATEMENT
                    }
                } else if (command.matches("^\\.int\\s\\d+$")) { // If the command starts with .int with a space and at least one or multiple digits
                    statPoint = selectStatPtsDB(user); // Stat points of the character in the DB
                    inputStatPoint = getInputStatPoint(user, command, extractStatPoint); // To recuperate the digits only

                    if (inputStatPoint > statPoint) { // If the user is trying to allocate an amount of points that he does not have
                        displayErrorAllocPt(event, user);
                    } else { // Updating stats and deducting available stat points
                        // UPDATE STATEMENT
                    }
                } else if (command.matches("^\\.wis\\s\\d+$")) { // If the command starts with .wis with a space and at least one or multiple digits
                    statPoint = selectStatPtsDB(user); // Stat points of the character in the DB
                    inputStatPoint = getInputStatPoint(user, command, extractStatPoint); // To recuperate the digits only

                    if (inputStatPoint > statPoint) { // If the user is trying to allocate an amount of points that he does not have
                        displayErrorAllocPt(event, user);
                    } else { // Updating stats and deducting available stat points
                        // UPDATE STATEMENT
                    }
                } else if (command.matches("^\\.cha\\s\\d+$")) { // If the command starts with .cha with a space and at least one or multiple digits
                    statPoint = selectStatPtsDB(user); // Stat points of the character in the DB
                    inputStatPoint = getInputStatPoint(user, command, extractStatPoint); // To recuperate the digits only

                    if (inputStatPoint > statPoint) { // If the user is trying to allocate an amount of points that he does not have
                        displayErrorAllocPt(event, user);
                    } else { // Updating stats and deducting available stat points
                        // UPDATE STATEMENT
                    }
                }
                // END OF STAT ALLOCATION SECTION

            }
        }
    }

    public int getInputStatPoint(User user, String command, String[] extractStatPoint) {
        extractStatPoint = command.split("\\s"); // For the next step

        return Integer.parseInt(extractStatPoint[1]); // To recuperate the digits only
    }

    public int selectStatPtsDB(User user) {
        int statPoint = 0;
        ResultSet resultSet;

        try {
            resultSet = JDBC.connect("SELECT `stat_point` FROM `character` WHERE discord_id = " + user.getId(), true);
            resultSet.next(); // To be able to read values; move the pointer to the row that has a result set a.k.a a record
            statPoint = resultSet.getInt("stat_point");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statPoint;
    }

    public void displayErrorAllocPt(MessageReceivedEvent event, User user) {
        EmbedBuilder embedInvalidAllocation = new EmbedBuilder();
        embedInvalidAllocation.setColor(Color.RED);
        embedInvalidAllocation.setTitle("Error");
        embedInvalidAllocation.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
        embedInvalidAllocation.setDescription("You do not have that amount of skill points available");
        embedInvalidAllocation.setImage("https://logowik.com/content/uploads/images/daffy-duck7482.logowik.com.webp");
        event.getChannel().sendMessageEmbeds(embedInvalidAllocation.build()).queue();
    }

    public void goExplore(MessageReceivedEvent event, User user) {
        EmbedBuilder embedExploration = new EmbedBuilder();
        embedExploration.clear();
        embedExploration.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
        embedExploration.setColor(Color.GREEN);
        embedExploration.setTitle("Exploring...");

        embedExploration.setImage("https://img.freepik.com/premium-photo/pixel-art-game-background-with-green-desert-cloudy-blue-sky_887552-24887.jpg");

        event.getChannel().sendMessageEmbeds(embedExploration.build()).queue();
    }

    public void displayHelpMenu(MessageReceivedEvent event, User user) {
        EmbedBuilder embedHelpMenu = new EmbedBuilder();
        embedHelpMenu.clear();
        embedHelpMenu.setColor(Color.GREEN);
        embedHelpMenu.setTitle("Commands List");
        embedHelpMenu.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
        embedHelpMenu.setDescription("Lost? You can go on an adventure to start your journey with .explore");
        embedHelpMenu.setImage("https://static.wikia.nocookie.net/character-stats-and-profiles/images/7/72/Daffy.png/revision/latest?cb=20210202150414");
        embedHelpMenu.addField(".help", "Lists all commands", true);
        embedHelpMenu.addField(".create", "Create a character", true);
        embedHelpMenu.addField(".info", "Character information", true);
        embedHelpMenu.addField(".explore", "Go on an adventure", true);

        event.getChannel().sendMessageEmbeds(embedHelpMenu.build()).queue();
    }

    public void createFirstCharacter(MessageReceivedEvent event, User user) {

        try {
            JDBC.connect("INSERT INTO `character` (`discord_id`) VALUES (" + user.getId() + ")", false); // Default values are already set in the DB, only the discord_id is needed to create a character
        } catch (SQLException e) {
            e.printStackTrace();
        }

        EmbedBuilder embedCreateCharacter = new EmbedBuilder();
        embedCreateCharacter.clear();
        embedCreateCharacter.setColor(Color.GREEN);
        embedCreateCharacter.setTitle("Success");
        embedCreateCharacter.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
        embedCreateCharacter.setDescription("You have created a new character");
        embedCreateCharacter.setImage("https://static.miraheze.org/greatcharacterswiki/thumb/d/d7/Daffy_Duck.png/800px-Daffy_Duck.png");
        event.getChannel().sendMessageEmbeds(embedCreateCharacter.build()).queue();
    }

    public void displayErrorExistingChar(MessageReceivedEvent event, User user) {
        EmbedBuilder embedExistingChar = new EmbedBuilder();
        embedExistingChar.setColor(Color.RED);
        embedExistingChar.setTitle("Error");
        embedExistingChar.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
        embedExistingChar.setDescription("You already have a character");
        embedExistingChar.setImage("https://logowik.com/content/uploads/images/daffy-duck7482.logowik.com.webp");
        event.getChannel().sendMessageEmbeds(embedExistingChar.build()).queue();
    }

    public boolean checkExistingUser(User user) {
        boolean newUser = true;

        try {
            ResultSet resultSet = JDBC.connect("SELECT `discord_id` FROM `character` WHERE `discord_id` = " + user.getId(), true);
            if (resultSet.next() && resultSet.getString("discord_id").equals(user.getId())) { // If there`s a record and matching values
                newUser = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newUser;
    }

    public void displayCharInfo(MessageReceivedEvent event, User user) {
        EmbedBuilder embedCharInfo = new EmbedBuilder();
        embedCharInfo.clear();
        embedCharInfo.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
        embedCharInfo.setColor(Color.GREEN);
        embedCharInfo.setTitle("Character Info");
        embedCharInfo.setImage("https://cdnb.artstation.com/p/assets/images/images/026/592/195/large/cristina-pozuelo-lopez-render1-tpose.jpg?1589203673");

        try {
            ResultSet resultSet = JDBC.connect("SELECT * FROM `character` WHERE `discord_id` = " + user.getId(), true); // Find the corresponding character
            resultSet.next(); // To be able to read values; move the pointer to the row that has a result set a.k.a a record
            int level = resultSet.getInt("level"); // To calculate the exp needed to level up

            int statPoint = selectStatPtsDB(user); // Value from the DB

            embedCharInfo.addField("Level", resultSet.getString("level"), true);
            embedCharInfo.addField("EXP", resultSet.getString("experience") + "/" + calculateExpNeeded(level), true);
            embedCharInfo.addField("Class", convertClassID(resultSet), true);
            embedCharInfo.addField("Strength", resultSet.getString("strength"), true);
            embedCharInfo.addField("Constitution", resultSet.getString("constitution"), true);
            embedCharInfo.addField("Dexterity", resultSet.getString("dexterity"), true);
            embedCharInfo.addField("Intelligence", resultSet.getString("intelligence"), true);
            embedCharInfo.addField("Wisdom", resultSet.getString("wisdom"), true);
            embedCharInfo.addField("Charisma", resultSet.getString("charisma"), true);


            embedCharInfo.setFooter("You currently have : " + statPoint + " stat points available\nIf you wish to allocate those points, use one of the following commands followed by the amount (ex: .str 3 ) : .str .con .dex .int .wis .cha");

            event.getChannel().sendMessageEmbeds(embedCharInfo.build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int calculateExpNeeded(int level) {
        double expNeeded = 5 * Math.pow(level, (double) 3 / 4);

        return (int) expNeeded;
    }

    public String convertClassID(ResultSet resultSet) throws SQLException {
        String className = "";

        switch (resultSet.getString("class_id")) {
            case "1": // Beginner: No bonuses
                className = "Beginner";
                break;
            case "2": // Adventurer: No bonuses except for more drop rate and faster ticks
                className = "Adventurer";
                break;
            case "3": // Mage:
                className = "Mage";
                break;
            case "4": // Warrior:
                className = "Warrior";
                break;
            case "5": // Paladin:
                className = "Paladin";
                break;
        }
        return className;
    }

}