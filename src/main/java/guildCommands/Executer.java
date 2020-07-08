package guildCommands;

import java.util.HashMap;
import java.util.Map;

import main.Ref;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Executer {
	// key: alias | value: command
	private static Map<String, String> commandsAliases = new HashMap<String, String>();
	private Map<String, Command> commands = new HashMap<String, Command>();
	
	public Executer() {
		commands.put("accountselling", new AccountSelling());
		commands.put("help", new Help());
	}
	
	public static void addAlias(String alias, String commandName) {
		commandsAliases.put(alias, commandName);
	}
	
	/**
	 * Checks if the given name is the alias of some command and, if so, return that command's name.
	 * @param name the target of the verification.
	 * @return if the given name is an alias of a command: the command's name.<br>
	 * returns the given name otherwise.
	 */
	private String checkForAlias(String name) {
		name = name.toLowerCase();
		if (commandsAliases.containsKey(name))
			return commandsAliases.get(name);
		else
			return name;
	}
	
	/**
	 * Executes the command present in the event, if it exists.
	 * @param event the event that possibly contains a command.
	 */
	public void executeCommand(GuildMessageReceivedEvent event) {
		String commandName = event.getMessage().getContentRaw().substring(Ref.commandsPrefix.length()).toLowerCase().split(" ")[0];
		commandName = checkForAlias(commandName);
		if (commands.containsKey(commandName))
			commands.get(commandName).execute(event);
	}
}
