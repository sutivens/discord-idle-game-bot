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
        User user = event.getAuthor();
        String command = event.getMessage().getContentRaw(); // Get the user input
        boolean isNewUser;

        if (user.isBot()) {
            return;
        } else {
            isNewUser = checkExistingUser(user); // Check if the user is registered
        }

        if (validCommands(command)) { // Valid commands
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
                }
            }
        }
    }

    public void displayHelpMenu(MessageReceivedEvent event, User user) {
        EmbedBuilder embedHelpMenu = new EmbedBuilder();
        embedHelpMenu.clear();
        embedHelpMenu.setColor(Color.GREEN);
        embedHelpMenu.setTitle("Help");
        embedHelpMenu.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());

        embedHelpMenu.addField(".help", "Lists all commands", true);
        embedHelpMenu.addField(".create", "Create a character", true);
        embedHelpMenu.addField(".info", "Character information", true);

        event.getChannel().sendMessageEmbeds(embedHelpMenu.build()).queue();
    }

    public void createFirstCharacter(MessageReceivedEvent event, User user) {
        try {
            JDBC.connect("INSERT INTO `character` (discord_id, class_id, level, experience, strength, constitution, dexterity, intelligence, wisdom, charisma) VALUES (" + user.getId() + ", 1, 1, 0, 10, 10, 10, 10, 10, 10)", false);
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
            ResultSet resultSet = JDBC.connect("SELECT discord_id FROM `character` WHERE discord_id = " + user.getId(), true);
            if (resultSet.next() && resultSet.getString("discord_id").equals(user.getId())) {
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
        embedCharInfo.addBlankField(false);

        try {
            ResultSet resultSet = JDBC.connect("SELECT * FROM `character` WHERE discord_id = " + user.getId(), true);
            resultSet.next(); // To be able to read values; move the pointer to the row that has a result set a.k.a a record
            int level = Integer.parseInt(resultSet.getString("level")); // To calculate the exp needed to level up

            embedCharInfo.addField("Level", resultSet.getString("level"), true);
            embedCharInfo.addField("EXP", resultSet.getString("experience") + "/" + calculateExpNeeded(level), true);
            embedCharInfo.addField("Class", convertClassID(resultSet), true);
            embedCharInfo.addField("Strength", resultSet.getString("strength"), true);
            embedCharInfo.addField("Constitution", resultSet.getString("constitution"), true);
            embedCharInfo.addField("Dexterity", resultSet.getString("dexterity"), true);
            embedCharInfo.addField("Intelligence", resultSet.getString("intelligence"), true);
            embedCharInfo.addField("Wisdom", resultSet.getString("wisdom"), true);
            embedCharInfo.addField("Charisma", resultSet.getString("charisma"), true);

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

    public boolean validCommands(String command) {
        boolean valid = false;

        if (command.equals(".create")) {
            valid = true;
        } else if (command.equals(".info")) {
            valid = true;
        } else if (command.equals(".help")) {
            valid = true;
        }
        return valid;
    }
}