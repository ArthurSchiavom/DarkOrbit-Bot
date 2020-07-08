package guildCommands;

import java.util.ArrayList;
import java.util.List;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * Configurations:<br>
 * setName()<br>
 * setUserType()<br>
 * setDescription()<br>
 * setAliases()<br>
 * addSubcommandHelp<br>
 * setRunInNewThread<br>
 * <br>
 * add help info to the help list: buildHelp()
 */
public abstract class Command {
	public static final int USERTYPE_USER = 0;
	public static final int USERTYPE_ADMIN = 1;
	public static final int USERTYPE_OWNER = 2;
	private int userType = -1;
	
	private String description = "";
	private String usage = "";
	private List<CommandHelp> subcommandsHelp = new ArrayList<CommandHelp>();
	
	/** Prefix not included*/
	private String name = "COMMAND_NAME_STILL_NOT_SET";
	private boolean runInNewThread = false;
	
	protected void execute(GuildMessageReceivedEvent event) {
		if (userType == -1) {
			main.ErrorMessenger.inform("Command **" + name + "** did not define the userType");
			return;
		}
		
		boolean run = true;
		if(userType == USERTYPE_OWNER) {
			if( !main.Ref.isOwner(event.getAuthor().getIdLong()) ) {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", this command can only be used by the bot owner").queue();
				run = false;
			}
		}
		else if (userType == USERTYPE_ADMIN) {
			if ( !main.Ref.isAdmin(event.getAuthor().getIdLong()) ) {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", this command can only be used by admins").queue();
				run = false;
			}
		}
		if (run == true) {
			if (!runInNewThread)
				runCommand(event);
			else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						runCommand(event);
					}
				}).start();
			}
				
		}
		
	}
	
	// If (subcomand.equals("x")) x(event);
	/**
	 * Runs this command
	 * @param event the event with the info to be processed
	 */
	protected abstract void runCommand(GuildMessageReceivedEvent event);
	
	/** 
	 * Define which users can use this command.
	 * @param newValue one of the constants at guildMessage.
	 * */
	protected void setUserType(int newValue) {
		userType = newValue;
	}
	
	protected void setAliases(String[] aliases) {
		for (String alias : aliases)
			Executer.addAlias(alias.toLowerCase(), name);
		
	}
	
	/**
	 * Sets the help information for this command.
	 * @param description what the command does.
	 * */
	protected void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Sets the help information for this command.
	 * @param description what the command does.
	 * @param usage lines:<br>
	 * [0] - Command and it's arguments (example: pschedule [name] [repetition] [time] [message])<br>
	 * [1] - What is argument one (Example: [name] - The name assigned to the schedule)<br>
	 * [2] - What is argument two (Example: [repetition] - The type of repetion: Daily, Weekly or Monthly) 
	 * */
	protected void setDescription(String description, String usage) {
		this.description = description;
		this.usage = usage;
	}
	
	/**
	 * Sets the help information for a subcommand.
	 * @param name the subcommand's name
	 * @param description what the subcommand does.
	 * */
	protected void addSubcommandHelp(String name, String description) {
		subcommandsHelp.add(new CommandHelp(name, description));
	}
	
	/**
	 * Sets the help information for a subcommand.
	 * @param name the subcommand's name
	 * @param description what the command does.
	 * @param usage lines:<br>
	 * [0] - Command and it's arguments (example: pschedule [name] [repetition] [time] [message])<br>
	 * [1] - What is argument one (Example: [name] - The name assigned to the schedule)<br>
	 * [2] - What is argument two (Example: [repetition] - The type of repetion: Daily, Weekly or Monthly) 
	 * */
	protected void addSubcommandHelp(String name, String description, String usage) {
		subcommandsHelp.add(new CommandHelp(name, description, Ref.commandsPrefix + usage));
	}
	
	protected void buildHelp() {

		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(name);
		eb.setColor(Ref.helpMessageEmbedColor);
		eb.setFooter("Type " + Ref.commandsPrefix + "help [command] [subcommand] to see obtain help on a specific subcommand, if any.", Ref.helpMessageEmbedFooterImageUrl);
		if (!usage.isEmpty())
			eb.addField("Usage", usage, false);
		
		boolean first = true;
		for (CommandHelp subcommandHelp : subcommandsHelp) {
			String subcommandName = subcommandHelp.getName();
			Helper.addCommand(name + " " + subcommandName, subcommandHelp.getEmbed());
			if (first) {
				description = description.concat("\n\n**Subcommands**");
				first = false;
			}
			description = description.concat("\n`" + Ref.commandsPrefix + name + " " + subcommandName + "` - " + subcommandHelp.getDescription());
		}
		if (!description.isEmpty())
			eb.setDescription(description);
		
		Helper.addCommand(name, eb.build());
	}
	
	/**
	 * Sets the command's name, to be displayed on the help menu.
	 * @param name the command's name
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	protected String getName() {
		return name;
	}
	
	/**
	 * Sets whether this command should be run on a new thread or not. False by default.
	 * @param newValue true if should be run on another thread and false otherwise.
	 */
	protected void setRunInNewThread(boolean newValue) {
		this.runInNewThread = newValue;
	}
}
