package guildCommands;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class CommandHelp {
	private EmbedBuilder eb = new EmbedBuilder();
	private MessageEmbed helpMessage;
	private String name;
	private String description;
	private String usage;
	
	/**
	 * Creates a new subcommand with the given info.
	 * @param name the subcommand's name.
	 * @param description the subcommand's description. The description should be concise and a single line.
	 * @param usage how to use to use this command.
	 */
	CommandHelp(String name, String description, String usage) {
		this.name = name;
		this.description = description;
		String usageFirstLine = usage.split("\n")[0];
		String realUsage = "**" + usageFirstLine + "**" + usage.substring(usageFirstLine.length());
		this.usage = realUsage;
		defineMessageEmbed();
	}
	
	/**
	 * Creates a new subcommand with the given info.
	 * @param name the subcommand's name.
	 * @param description the subcommand's description. The description should be concise and a single line.
	 */
	CommandHelp(String name, String description) {
		this.name = name;
		this.description = description;
		this.usage = "";
		defineMessageEmbed();
	}
	
	/**
	 * Returns this command help embed message.
	 */
	public MessageEmbed getEmbed() {
		return helpMessage;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	private void defineMessageEmbed() {
		eb.setTitle(name);
		eb.setColor(Ref.helpMessageEmbedColor);
		eb.setFooter("Command help", Ref.helpMessageEmbedFooterImageUrl);
		eb.setDescription(description);
		if (!usage.isEmpty())
			eb.addField("Usage", usage, false);
		helpMessage = eb.build();
	}
}
