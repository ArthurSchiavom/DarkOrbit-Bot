package main;

import net.dv8tion.jda.core.entities.MessageEmbed;

public class ErrorMessenger {
	public static void inform(String error) {
		if (Ref.errorChannelID != -1 && error != null)
			try {
				Main.botAPI.getTextChannelById(Ref.errorChannelID).sendMessage(error).queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void inform(MessageEmbed embed) {
		if (Ref.errorChannelID != -1 && embed != null && !(embed.isEmpty()))
			try {
				Main.botAPI.getTextChannelById(Ref.errorChannelID).sendMessage(embed).queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
