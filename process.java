import java.util.*; 

public class process implements Comparable<process> {
	private int PID;

	private int A, B, C, M;
	private int finish = 0;
	private int IOtime = 0;
	private int waiting = 0;

	private int CPUburst = 0;

	private int remainingCPU = 0;
	private int remainingIOtime = 0;
	private int timeRun = 0;


	//Constructor. There's no need for a default. 
	public process(int A, int B, int C, int M) {
		//Maybe try being lazy and set the PID in the sorting loop
		this.A = A;
		this.B = B;
		this.C = C;
		this.M = M;
	}

	public process(int A, int B, int C, int M, int PID) {
		this.PID = PID;

		this.A = A;
		this.B = B;
		this.C = C;
		this.M = M;
	}

	public void increment(int type) {
		finish += 1;

		switch (type) {
			case 0: remainingCPU -= 1; timeRun += 1; break;
			case 1: waiting += 1; break;
			case 2: IOtime += 1; remainingIOtime -= 1; break;
			case 3: waiting -= 1; finish -= 1; break;
			default: break;

		}
	}


	//This doesn't actually need a setter, or a 
	//data field, since it's just these two numbers
	public int getTurnaround() {
		return finish - A; 
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

	public int getPID() {
		return PID;
	}

	public void setPID(int PID) {
		this.PID = PID;
	}

	public int getCPUburst() {
		return CPUburst;
	}

	public void setCPUburst(int burst) {
		this.CPUburst = burst;
	}


	//For HRPN
	public int getPenaltyRatio() {
		return finish / Math.max(1, timeRun);
	}

	public String toString() {
		String toReturn = "(" + A + " , " + B + " , " + C + " , " + M + ")";

		return toReturn;
	}

	public boolean equals(process p) {
		if (this.PID == p.PID) {
			return true;
		}

		return false;

	}

	public int compareTo(process o) {
		if (this.A > o.A) {
			return 1;
		}

		else if (this.A < o.A) {
			return -1;
		}

		return 0;
	}
}