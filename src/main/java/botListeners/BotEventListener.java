package botListeners;

import main.Ref;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
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
	
	private final String STREAMING_NICKNAME_PREFIX = "[ Streaming ] ";
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		if (Ref.streamingRoleID == -1) {
			System.out.println("The ID of the streaming role was not set.");
			return;
		}
		
		for (Role role : event.getRoles()) {
			if (role.getIdLong() == Ref.streamingRoleID) {
				Member member = event.getMember();
				String nickname = getNickname(member);
				
				event.getGuild().getController().setNickname(member, STREAMING_NICKNAME_PREFIX + nickname).queue(null,
						(t) -> {
							main.ErrorMessenger.inform("Failed to change the nickname of <@!" + member.getUser().getId() + "> after streaming role was obtained.");
						});
				break;
			}
		}
	}
	
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		if (Ref.streamingRoleID == -1) {
			System.out.println("The ID of the streaming role was not set.");
			return;
		}
		
		for (Role role : event.getRoles()) {
			if (role.getIdLong() == Ref.streamingRoleID) {
				Member member = event.getMember();
				String currentNickname = getNickname(member);
				
				if (currentNickname.startsWith(STREAMING_NICKNAME_PREFIX)) {
					event.getGuild().getController().setNickname(member, currentNickname.substring(STREAMING_NICKNAME_PREFIX.length())).queue(null,
							(t) -> {
								main.ErrorMessenger.inform("Failed to change the nickname of <@!" + member.getUser().getId() 
										+ "> after streaming role was removed.");
							});
				}
				break;
			}
		}
	}
	
	private String getNickname(Member member) {
		String nickname = member.getNickname();
		if (nickname == null)
			nickname = member.getUser().getName();
		return nickname;
	}
}
