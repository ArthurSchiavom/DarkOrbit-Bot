package guildCommands;

import java.util.HashMap;
import java.util.Map;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class Helper {
	private static EmbedBuilder helpMain;
	private static String helpMainDescription = "`help`";
	private static Map<String, MessageEmbed> commandsHelpInfo = new HashMap<String, MessageEmbed>();
	
	
	Helper() {
		helpMain = new EmbedBuilder();
		helpMain.setTitle("Available commands");
		helpMain.setFooter("Use [" + Ref.commandsPrefix + "help command] to obtain help on how to use a specific command.", Ref.helpMessageEmbedFooterImageUrl);
		helpMain.setColor(Ref.helpMessageEmbedColor);
	}
	
	/**
	 * Sets the help message (embed) for the given command.
	 * @param command the command, or command+subcommand.
	 * @param helpMessage the message to be sent when help is requested for the given command.
	 */
	public static void addCommand(String command, MessageEmbed helpMessage) {
		commandsHelpInfo.put(command.toLowerCase(), helpMessage);
		helpMainDescription.concat("; `" + command + "`");
	}
	
	public MessageEmbed getHelp(String command) {
		if (commandsHelpInfo.containsKey(command))
			return commandsHelpInfo.get(command);
		else
			return null;
	}
}
