package main;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import userListeners.UserEventListener;
import botListeners.BotEventListener;
import main.Ref;

public class Main extends ListenerAdapter {

	public static JDA botAPI;
	public static JDA userAPI;

	public static void main(String[] args) {
		System.out.println("Restarting");
		Ref.loadRef();
		runOnNewThread(new RSSFeed.Informer());
		runOnNewThread(new suggestions.SuggestionTerminator());
		database.Database.initializeDriver();
		database.SuggestionTable.loadSuggestionsIdentifiers();
		
		try {
			botAPI = new JDABuilder(AccountType.BOT).setToken(Ref.botToken).buildBlocking();
			botAPI.addEventListener(new BotEventListener());
			if(!Ref.game.isEmpty())
				botAPI.getPresence().setGame(Game.playing(Ref.game));
		} catch (Exception e) {
			System.out.println("Failed to log in the bot");
			e.printStackTrace();
		}
		
		try {
			userAPI = new JDABuilder(AccountType.CLIENT).setToken(Ref.userToken).buildBlocking();
			userAPI.addEventListener(new UserEventListener());
		} catch (Exception e) {
			System.out.println("Failed to log in the user");
			e.printStackTrace();
		}
		
		System.out.println("Successfully restarted");
	}
	
	private static void runOnNewThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
