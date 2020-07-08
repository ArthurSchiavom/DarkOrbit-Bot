package guildCommands;

import main.Ref;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Help extends Command {
	
	Helper helper = new Helper();
	
	Help() {
		setName("Help");
		setDescription("Displays the available commands");
		setUserType(Command.USERTYPE_USER);
		buildHelp();
	}

	@Override
	protected void runCommand(GuildMessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();
		int helpCommandLenght = getName().length() + Ref.commandsPrefix.length();
		if (helpCommandLenght == message.trim().length()) {
			event.getChannel().sendMessage(helper.getMainHelp()).queue();
		} else {
			String command = message.substring(helpCommandLenght).trim().toLowerCase();
			MessageEmbed helpMessage = helper.getHelp(command);
			if (helpMessage != null)
				event.getChannel().sendMessage(helpMessage).queue();
			else
				event.getChannel().sendMessage("The command **" + command + "** does not exist.").queue();
		}
	}
}
