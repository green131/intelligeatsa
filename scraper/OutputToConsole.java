package scraper;

public class OutputToConsole implements OutputToStorage{

	@Override
	public int sendOutput(String json) {
		System.out.println(json);
		return 0;
	}

}
