import java.util.*; //Might not need this 

public class process {
	private int A, B, C, M;
	private int state; //Maybe a different data type? Initial plan is 0 = running, 1 = ready, 2 = blocked
	private int finish = 0;
	private int IOtime = 0;
	private int waiting = 0;

	private int remainingCPU = 0;
	private int remainingIOtime = 0;
	private int timeRun = 0;


	//Constructor. There's no need for a default. 
	public process(int A, int B, int C, int M) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.M = M;
	}

	public void setState(int newState) {
		state = newState;
		return;
	}

	//This doesn't actually need a setter, or a 
	//data field, since it's just these two numbers
	public int getTurnaround() {
		return finish - A; 
	}

	//Maybe have this take an argument for time units
	//Designed to increment the proper time based on what state
	public void increment() {
		finish += 1;

		switch (state) {
			case 0: timeRun += 1; remainingCPU -= 1; break;

			case 1: waiting += 1; break;

			case 2: IOtime += 1; remainingIOtime -= 1; break;

			default: break;
		}
		return; 

	}

	//Getters for eventual printing
	public int getFinish() {
		return finish;
	}

	public int getIOtime() {
		return IOtime;
	}

	public int getWaiting() {
		return waiting;
	}

	public int getA() {
		return A;
	}

	public int getB() {
		return B;
	}

	public int getC() {
		return C;
	}

	public int getM() {
		return M;
	}

	public int getState() {
		return state;
	}

	public void setRemainingCPU(int burst) {
		remainingCPU = burst;
	}

	public void setRemainingIO(int burst) {
		remainingIOtime = burst;
	}

	public int getRemainingCPU() {
		return remainingCPU;
	}

	public int getRemainingIO() {
		return remainingIOtime;
	}

	public int getTimeRun() {
		return timeRun;
	}

	public String toString() {
		String toReturn = "(" + A + " , " + B + " , " + C + " , " + M + ")";

		return toReturn;
	}
}