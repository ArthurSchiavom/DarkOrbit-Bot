package guildCommands;

import java.util.HashMap;
import java.util.Map;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class Helper {
	private static MessageEmbed mainHelp;
	private static EmbedBuilder mainHelpBuilder;
	private static String mainHelpDescription = "";
	private static Map<String, MessageEmbed> commandsHelpInfo = new HashMap<String, MessageEmbed>();
	
	
	Helper() {
		mainHelpBuilder = new EmbedBuilder();
		mainHelpBuilder.setTitle("Available commands");
		mainHelpBuilder.setFooter("Type " + Ref.commandsPrefix + "help [command] to obtain help on how to use a specific command.", Ref.helpMessageEmbedFooterImageUrl);
		mainHelpBuilder.setColor(Ref.helpMessageEmbedColor);
	}
	
	static boolean firstCommand = true;
	/**
	 * Sets the help message (embed) for the given command.
	 * @param command the command, or command+subcommand.
	 * @param helpMessage the message to be sent when help is requested for the given command.
	 * @param isSubcommand if the given command is a subcommand
	 */
	public static void addCommand(String command, MessageEmbed helpMessage, boolean isSubcommand) {
		commandsHelpInfo.put(command.toLowerCase(), helpMessage);
		if (!isSubcommand)
		{
			if (firstCommand) {
				mainHelpDescription = mainHelpDescription + "`" + command + "`";
				firstCommand = false;
			} 
			else
				mainHelpDescription =mainHelpDescription + "; `" + command + "`";
			mainHelpBuilder.setDescription(mainHelpDescription);
			mainHelp = mainHelpBuilder.build();
		}
	}
	
	/**
	 * Provides the help message embed for a given command.
	 * @param command the name of the command for which help info is wanted.
	 * @return the help message embed.
	 */
	public MessageEmbed getHelp(String command) {
		String[] subcommands = command.split(" ");
		subcommands[0] = Executer.isAlias(subcommands[0]);
		command = subcommands[0];
		String str;
		for (int i = 1; i<subcommands.length; i++) {
			str = subcommands[i];
			command = command.concat(" ").concat(str);
		}
		
		if (commandsHelpInfo.containsKey(command))
			return commandsHelpInfo.get(command);
		else
			return null;
	}
	
	/**
	 * Provides the main help menu that contains a list of all the commands.
	 * @return the main help menu that contains a list of all the commands.
	 */
	public MessageEmbed getMainHelp() {
		return mainHelp;
	}
}
