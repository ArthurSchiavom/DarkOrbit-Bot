package suggestions;

import java.util.Date;

/**
 * Contains the following information about suggestions:<br>
 * channelID<br>
 * messageID<br>
 * votingPeriodEndDate<br>
 */
public class SuggestionIdentifier {
	public long channelID;
	public long messageID;
	public Date votingPeriodEndDate;
	
	/**
	 * Creates a new object containing the channel ID, message ID and the date when the voting period should end.
	 * @param channelID
	 * @param messageID
	 * @param votingPeriodEndDate
	 */
	public SuggestionIdentifier(long channelID, long messageID, Date votingPeriodEndDate) {
		this.channelID = channelID;
		this.messageID = messageID;
		this.votingPeriodEndDate = votingPeriodEndDate;
	}
}
