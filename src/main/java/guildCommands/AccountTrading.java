package guildCommands;

import java.awt.Color;

import main.Ref;
import multiQuestion.Questioner;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import sales.AccountSeller;

public class AccountTrading extends Command {
	AccountSeller accSeller = Ref.accSeller;
	Helper helper = new Helper();
	JDA api = main.Main.botAPI;
	
	AccountTrading() {
		setName("AccountTrading");
		setUserType(Command.USERTYPE_USER);
		String[] aliases = {"at"};
		setAliases(aliases);
		setDescription("Contains the commands related to account trading.");
		addSubcommandHelp("Trade"
				, "Initialize the process to trade or sell an account."
				, getName() + " Trade");
		addSubcommandHelp("Info"
				, "Provides full infomation about an account."
				, getName() + " Info [Market ID]"
						+ "\n[Market ID] - The market ID of the account that you wish to obtain information on.");
		
//		[0] - Command and it's arguments (example: schedule [name] [repetition] [time] [message])
//		[1] - What is argument one (Example: [name] - The name assigned to the schedule)
//		[2] - What is argument two (Example: [repetition] - The type of repetion: Daily, Weekly or Monthly) 
		
		buildHelp();
	}

	@Override
	protected void runCommand(GuildMessageReceivedEvent event) {
		String msg = event.getMessage().getContentRaw().toLowerCase().trim();
		String[] subcommands = msg.split(" ");
		if (subcommands.length == 1) {
			event.getChannel().sendMessage(helper.getHelp(getName())).queue();
		}
		else {
			switch (subcommands[1]) {
			case "trade":
				tradeCommand(event);
				break;
			case "info":
				infoCommand(event, subcommands);
			}
		}
	}
	
	/**
	 * Creates an embed according with the given info.
	 * @param title the embed's title.
	 * @param description the embed's description.
	 * @param color the embed's color.
	 * @return the embed created according with the given info.
	 */
	private MessageEmbed generateMessageEmbed(String title, String description, Color color) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(title);
		eb.setDescription(description);
		eb.setColor(color);
		return eb.build();
	}
	
	/**
	 * Processes the info command
	 * @param event the event asking for this command's execution
	 */
	private void tradeCommand(GuildMessageReceivedEvent event) {
		main.Ref.questioner.startNewQuestionnairePM(Questioner.DO_TRADE_QUESTIONNAIRE,
				event.getAuthor().getIdLong(), 
				event.getChannel().getIdLong());
	}
	
	/**
	 * Processes the info command
	 * @param event the event asking for this command's execution
	 * @param subcommands the message asking for this command's execution split by the regex " "
	 */
	private void infoCommand(GuildMessageReceivedEvent event, String[] subcommands) {
		TextChannel channel = event.getChannel();
		if (subcommands.length < 3) {
			MessageEmbed errorMessage = generateMessageEmbed("Account Info",
					"You need to specify the market ID of the account that you wish to obtain information on.",
					Color.RED);
			channel.sendMessage(errorMessage).queue();
			return;
		}
		
		int accountMarketID = 0;
		try {
			accountMarketID = Integer.parseInt(subcommands[2]);
		} catch (NumberFormatException e) {
			MessageEmbed errorMessage = generateMessageEmbed("Account Info",
					"You need to specify the market ID of the account that you wish to obtain information on.",
					Color.RED);
			channel.sendMessage(errorMessage).queue();
			return;
		}
		MessageEmbed accountInfo = accSeller.compileAccountInfo(accountMarketID);
		channel.sendMessage(accountInfo).queue();
	}
}
