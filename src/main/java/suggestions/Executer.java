package suggestions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Footer;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Executer {
	private static database.SuggestionTable database = new database.SuggestionTable();
	private final String suggestionEmbedTitle = "Suggestion";
	
	/**
	 * Embeds the suggestion
	 * @param event the event to analyze
	 */
	public void processSuggestion(GuildMessageReceivedEvent event) {
		Message message = event.getMessage();
		EmbedBuilder reply = new EmbedBuilder();
		reply.setTitle(suggestionEmbedTitle);
		reply.setDescription(message.getContentRaw());
		User author = message.getAuthor();
		reply.setFooter("Suggested by: " + author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());
		if (!Ref.votingEmbedThumbnail.isEmpty())
			reply.setThumbnail(Ref.votingEmbedThumbnail);
		reply.setColor(Ref.suggestionEmbedColor);
		
		Message replyMessage = event.getChannel().sendMessage(reply.build()).complete();
		
		database.add(replyMessage.getChannel().getIdLong(), replyMessage.getIdLong(), getEndVoteDate());
		
		for (String emote : Ref.reactionEmotes)
			try {
				List<Emote> emotes = event.getGuild().getEmotesByName(emote, true);
				if (!emotes.isEmpty())
					replyMessage.addReaction(emotes.get(0)).queue();
				else
					main.ErrorMessenger.inform("No such emote: " + emote);
			} catch (Exception e) {
				main.ErrorMessenger.inform("Failed to add the following emote reaction to a suggestion: " + emote);
				e.printStackTrace();
			}
		
		event.getMessage().delete().queue();
	}
	
	/**
	 * Checks if a given event is a suggestion
	 * @param event the event to be analyzed
	 * @return true if the given event is a suggestion message
	 */
	public boolean isSuggestion(GuildMessageReceivedEvent event) {
		return Ref.suggestionChannelsIDs.contains(event.getChannel().getIdLong());
	}
	
	/**
	 * Calculates when the voting period should end according to config.txt
	 * @return when the voting period should end according to config.txt
	 */
	private Date getEndVoteDate() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(Ref.timezone));
		cal.add(Calendar.HOUR_OF_DAY, main.Ref.suggestionsVotingDuration);
		
		return cal.getTime();
	}
	
	/**
	 * Verifies if the voting period for each suggestion is over and, if so, makes users unable to vote on that specific suggestion.
	 */
	public static void validateSuggestions() {
		Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone(Ref.timezone));
		Date currentDate = currentCal.getTime();
		
		// Do not remove from the list while looping trough it or will get a concurrent access exception
		List<SuggestionIdentifier> suggestionsForRemoval = new ArrayList<SuggestionIdentifier>();
		for (SuggestionIdentifier suggestion : database.getSuggestionsIdentifiers()) {
			if (currentDate.after(suggestion.votingPeriodEndDate))
				suggestionsForRemoval.add(new SuggestionIdentifier(suggestion.channelID, suggestion.messageID, new Date()));
		}
		for (SuggestionIdentifier suggestion : suggestionsForRemoval)
			terminateVoting(suggestion.channelID, suggestion.messageID);
	}
	
	/**
	 * Terminates the voting for a certain message by removing the reactions and displaying the poll results
	 * @param channelID the channel where the suggestion message is
	 * @param messageID the suggestion message's ID
	 */
	private static void terminateVoting(long channelID, long messageID) {

		TextChannel channel = main.Main.botAPI.getTextChannelById(channelID);
		Message message = null;
		try {
			message = channel.getMessageById(messageID).complete();
		} catch (Exception e) {
			// if it executes this block, it means that the bot couldn't access the message
			// for whatever reason. Just ignore
		}

		if (message != null) {
			try {
				database.remove(channelID, messageID);
				message.clearReactions().queue();
				MessageEmbed embed = message.getEmbeds().get(0);
				EmbedBuilder newEmbed = new EmbedBuilder();
				if (!Ref.votingEmbedThumbnail.isEmpty())
					newEmbed.setThumbnail(Ref.votingEmbedThumbnail);
				newEmbed.setTitle(embed.getTitle());
				newEmbed.setDescription(embed.getDescription());
				Footer footer = embed.getFooter();
				newEmbed.setFooter(footer.getText(), footer.getIconUrl());
				newEmbed.setColor(embed.getColor());

				Map<Emote, Integer> emotes = new HashMap<Emote, Integer>();
				for (MessageReaction messageReaction : message.getReactions()) {
					Emote emote = messageReaction.getReactionEmote().getEmote();
					if (Ref.reactionEmotes.contains(emote.getName())) {
						int count = messageReaction.getCount();
						emotes.put(emote, count - 1); // -1 because one of them is the bot's reaction
					}
				}

				StringBuilder results = new StringBuilder("");
				for (Emote emote : emotes.keySet()) {
					String textEmote = emote.getAsMention();
					results.append("\n" + textEmote + " - " + emotes.get(emote));
				}
				newEmbed.addField("Results", results.toString(), false);

				message.editMessage(newEmbed.build()).queue();
			} catch (Exception e) {
				main.ErrorMessenger.inform("Unable end voting on the message of ID " + messageID + ", of the channel "
						+ channel.getAsMention());
				e.printStackTrace();
			}
		}
	}
}
