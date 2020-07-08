package userListeners;


import main.Ref;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserEventListener extends ListenerAdapter {
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Long channelID = event.getChannel().getIdLong();
		if(Ref.channelMirror.isOriginChannel(channelID))
			Ref.channelMirror.mirrorMessage(event.getMessage().getContentRaw(), channelID);
	}
	
}
