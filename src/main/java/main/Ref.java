package main;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import RSSFeed.FeedChannels;
import userListeners.ChannelMirror;

public class Ref {
	public static String commandsPrefix = "!";
	public static multiQuestion.Questioner questioner = new multiQuestion.Questioner();
	public static String timezone = "Etc/GMT";
	public static String botToken = "";
	public static String userToken = "";
	public static ChannelMirror channelMirror = new ChannelMirror();
	public static String game = "";
	public static List<Long> suggestionChannelsIDs = new ArrayList<Long>();
	public static List<String> reactionEmotes = new ArrayList<String>();
	public static List<Long> origin = new ArrayList<Long>();
	public static FeedChannels RSSFeeds = new FeedChannels();
	public static String darkOrbitLogoUrl = "https://farm2.staticflickr.com/1802/42415695115_87f8b92cde_o_d.png";
	public static String seafightLogoUrl = "https://farm2.staticflickr.com/1767/41768637170_cb1ac31821_o_d.png";
	public static String pirateStormLogoUrl = "https://farm2.staticflickr.com/1821/41768636670_1d320c348f_o_d.png";
	public static String leagueOfAngelsLogoUrl = "https://farm1.staticflickr.com/936/41768636050_6bfb51df8d_o_d.png";
	public static String drakensangLogoUrl = "https://farm2.staticflickr.com/1766/42859015954_98a11694eb_o_d.png";
	public static String battlestarLogoUrl = "https://farm1.staticflickr.com/942/42671704685_20f6d2ffb2_o_d.png";
	public static long errorChannelID = -1;
	public static Color feedEmbedColor = Color.decode("#2CF68A");
	public static String feedEmbedFooterImageUrl = "https://farm1.staticflickr.com/913/42604112564_b33988c838_o_d.png";
	public static String databaseUrl = "";
	public static String databaseName = "";
	public static String databaseUsername = "";
	public static String databasePassword = "";
	public static List<Long> ownersIDs = new ArrayList<Long>();
	public static List<Long> adminsIDs = new ArrayList<Long>();
	public static int suggestionsVotingDuration = -1;
	public static String votingEmbedThumbnail = "";
	public static Color suggestionEmbedColor = Color.decode("#2CF68A");
	public static Color helpMessageEmbedColor = Color.decode("#2CF68A");
	public static String helpMessageEmbedFooterImageUrl = "https://c1.staticflickr.com/1/858/43641846732_0096c5df33_m_d.jpg";
	
	/** Loads the reference variables */
	public static void loadRef() {
		try( BufferedReader br = new BufferedReader(new FileReader("config.txt")) ) {
			String str;
			String config[];
			while ((str = br.readLine()) != null) {
				config = str.split("=");
				if (config.length > 1) {
					config[0] = config[0].toLowerCase().replace(" ", "");
					config[1] = config[1].trim();
					switch (config[0]) {
					case "bottoken":
						botToken = config[1];
						break;
					case "usertoken":
						userToken = config[1];
						break;
					case "addmirror":
						String[] IDs = config[1].split(",");
						channelMirror.add(Long.parseLong(IDs[0]), Long.parseLong(IDs[1]));
						break;
					case "game":
						game = config[1];
						break;
					case "addsuggestionchannel":
						suggestionChannelsIDs.add(Long.parseLong(config[1]));
						break;
					case "addemote":
						reactionEmotes.add(config[1]);
						break;
					case "addrssfeed":
						String[] info = config[1].split(",");
						RSSFeeds.add(info[0], Long.parseLong(info[1]));
						break;
					case "feedembedcolor":
						feedEmbedColor = Color.decode(config[1]);
						break;
					case "feedembedfooterimageurl":
						feedEmbedFooterImageUrl = config[1];
						break;
					case "darkorbitlogourl":
						darkOrbitLogoUrl = config[1];
						break;
					case "seafightlogourl":
						seafightLogoUrl = config[1];
						break;
					case "piratestormlogourl":
						pirateStormLogoUrl = config[1];
						break;
					case "leagueofangelslogourl":
						leagueOfAngelsLogoUrl = config[1];
						break;
					case "drakensanglogourl":
						drakensangLogoUrl = config[1];
						break;
					case "galacticalogourl":
						battlestarLogoUrl = config[1];
						break;
					case "errorchannel":
						errorChannelID = Long.parseLong(config[1]);
						break;
					case "addowner":
						ownersIDs.add(Long.parseLong(config[1]));
						break;
					case "addadmin":
						adminsIDs.add(Long.parseLong(config[1]));
						break;
					case "commandsprefix":
						commandsPrefix = config[1];
						break;
					case "suggestionvotingduration":
						suggestionsVotingDuration = Integer.parseInt(config[1]);
						break;
					case "votingembedthumbnail":
						votingEmbedThumbnail = config[1];
						break;
					case "databaseurl":
						databaseUrl = config[1];
						break;
					case "databasename":
						databaseName = config[1];
						break;
					case "databaseusername":
						databaseUsername = config[1];
						break;
					case "databasepassword":
						databasePassword = config[1];
						break;
					case "suggestionembedcolor":
						feedEmbedColor = Color.decode(config[1]);
						break;
					case "helpmessageembedcolor":
						helpMessageEmbedColor = Color.decode(config[1]);
						break;
					case "helpmessageembedfooterimageurl":
						helpMessageEmbedFooterImageUrl = config[1];
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: FAILED TO LOAD main.Ref");
		}
	}
	
	public static boolean isOwner(Long ID) {
		return ownersIDs.contains(ID);
	}
	
	public static boolean isAdmin(Long ID) {
		return adminsIDs.contains(ID);
	}
	
	public static void verifyMissingSettings() {
		if (suggestionsVotingDuration == -1)
			main.ErrorMessenger.inform("No voting duration set, please do");
		//...
	}
}
