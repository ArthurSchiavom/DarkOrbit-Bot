package suggestions;

/**
 * Frequently checks if it's time to stop the voting on a suggestion
 */
public class SuggestionTerminator implements Runnable {

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Executer.validateSuggestions();
		}

	}
	
}
