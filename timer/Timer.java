package timer;

public class Timer {
	
	private long baseTime     = -1;
	private long currentTime  = -1;
	private long prevTime     = -1;
	private long deltaTime    = -0;
	private long totalTime    = 0;
	private boolean isRunning = false;
	
	
	
	
	
	public static double nanoToSeconds(long time) {
		return (double)time / 1000000000d;
	}
	
	
	
	
	
	public void start() {
		isRunning = true;
		baseTime = System.nanoTime();
		prevTime = baseTime;
	}
	
	
	
	
	public void tick() {
		if (!isRunning)
			return;
		
		currentTime = System.nanoTime();
		deltaTime   = currentTime - prevTime;
		prevTime    = currentTime;
	}
	
	
	
	
	public void stop() {
		isRunning = false;
		totalTime = System.nanoTime() - baseTime;
	}
	
	
	
	
	
	public void reset() {
		if (isRunning)
			baseTime = System.nanoTime();
		else
			baseTime    = -1;
		totalTime   = 0;
	}
	
	
	
	
	
	public long getElapsedTime() {
		if (isRunning)
			totalTime = System.nanoTime() - baseTime;
		return totalTime;
	}
	
	
	
	public long getDeltaTime() {
		return deltaTime;
	}
	
	
	
}
