package userListeners;

import java.util.ArrayList;
import java.util.List;

import main.Main;

public class ChannelMirror {
	private List<Long> originChannelsIDs = new ArrayList<Long>();
	private List<Long> targetChannelsIDs = new ArrayList<Long>();
	
	/**
	 * Retrives a list with all the target channels' IDs associated to the given origin channel ID
	 * @param originChannelID the origin channel ID where the message was received
	 * @return a list with all the target channels' IDs associated to the given origin channel ID
	 */
	public List<Long> getTargetChannelIDs(long originChannelID) {
		List<Long> results = new ArrayList<Long>();
		
		int index = 0;
		for (long ID : originChannelsIDs) {
			if (ID == originChannelID) {
				results.add(targetChannelsIDs.get(index));
			}
			index++;
		}
		
		return results;
	}
	
	/** 
	 * Sends a message to all target channels associated with this origin channel by the user at config.txt
	 * @param msg The message to be sent
	 * @param channelID the origin channel ID
	 *  */
	public void mirrorMessage(String msg, Long channelID) {
		for(Long ID : getTargetChannelIDs(channelID)) {
			try {
				if (!msg.isEmpty())
					Main.botAPI.getTextChannelById(ID).sendMessage(msg).queue();
			} catch(Exception e) {
				main.ErrorMessenger.inform("Error while trying to mirror a message from the channel <#" + channelID + ">");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Verifies if the given ID is the ID of an origin channel
	 * @param channelID
	 * @return true if the given channel is an origin channel and false otherwise
	 */
	public boolean isOriginChannel(Long channelID) {
		return originChannelsIDs.contains(channelID);
	}
	
	/**
	 * Associates the given originChannelID to the given targetChannelID
	 * @param originChannelID the channel to capture messages from
	 * @param targetChannelID the channel to send the messages received at the origin channel
	 */
	public void add(Long originChannelID, Long targetChannelID) {
		originChannelsIDs.add(originChannelID);
		targetChannelsIDs.add(targetChannelID);
	}
}
