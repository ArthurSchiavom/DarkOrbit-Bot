package main;

public class ErrorMessenger {
	public static void inform(String error) {
		if (Ref.errorChannelID != -1 && error != null)
			try {
				Main.botAPI.getTextChannelById(Ref.errorChannelID).sendMessage(error).queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
