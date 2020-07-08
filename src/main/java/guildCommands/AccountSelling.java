package guildCommands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class AccountSelling extends Command {

	AccountSelling() {
		setName("AccountSelling");
		setUserType(Command.USERTYPE_USER);
		String[] aliases = {"ac"};
		setAliases(aliases);
		
		addSubcommandHelp("Sell"
				, "Sell an account"
				, "accountselling sell [parameter1] [parameter2]"
						+ "\n[parameter1] - this and that"
						+ "\n[parameter2] - that and this");
		
//		[0] - Command and it's arguments (example: schedule [name] [repetition] [time] [message])
//		[1] - What is argument one (Example: [name] - The name assigned to the schedule)
//		[2] - What is argument two (Example: [repetition] - The type of repetion: Daily, Weekly or Monthly) 
		
		buildHelp();
	}

	@Override
	protected void runCommand(GuildMessageReceivedEvent event) {
		main.ErrorMessenger.inform("I am working! hehe ;p");
		
	}

}
