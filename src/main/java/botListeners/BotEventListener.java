package botListeners;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotEventListener extends ListenerAdapter {
	
	GuildMessageEventOperator guildMsgOperator = new GuildMessageEventOperator();
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		guildMsgOperator.operate(event);
	}
	
	PrivateMessageEventOperator privateMsgOperator = new PrivateMessageEventOperator();
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		privateMsgOperator.operate(event);
	}
}
