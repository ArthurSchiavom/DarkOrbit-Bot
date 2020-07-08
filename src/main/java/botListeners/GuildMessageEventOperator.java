package botListeners;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GuildMessageEventOperator {
	guildCommands.Executer commandExecuter = new guildCommands.Executer();
	suggestions.Executer suggestionExecuter = new suggestions.Executer();
	
	/**
	 * Performs the operations set to deal with GuildMessageReceivedEvents
	 * @param event the event to work with
	 */
	void operate(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		if (suggestionExecuter.isSuggestion(event)) {
			suggestionExecuter.processSuggestion(event);
			return;
		}
		
//		if (event.getMessage().getContentDisplay().equals("!sa"))
//			Ref.questioner.startNewQuestionnairePM(event.getAuthor().getIdLong(), new multiQuestion.AccountSellingQuestionnaire());
		
		String message = event.getMessage().getContentRaw();
		if (message.startsWith(main.Ref.commandsPrefix)) {
			commandExecuter.executeCommand(event);
		}
	}
	
	
}
