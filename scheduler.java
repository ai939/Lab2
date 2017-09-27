import java.util.*;
import java.io.*;

/*
Plan:
1) Create some sort of process table in the schedule method
2) Pass that to the individual scheduling algorithm methods
3) Print result (probably make its own method for cleaner code)

Things I need:
1) How do I store the processes?
Idea is to have a quadruple for each of the four integers
It would need a lot of parameters though
A, B, C, M, state, and various times that need to be held on to

2) How to print the final summary
This will be done all within the method. This doesn't need any
new class. I think we save this for last
*/

//A is arrival, B is interval for CPU burst time, C is time needed, M is used for IO time

public class scheduler {
	final static String randFile = "random-numbers.txt";

	public static void main(String[]  args) {

		//Proper usage
		if (args.length == 0) {
			System.out.println("Incorrect usage");
			System.out.println("Either use java scheduler <input> or java scheduler --verbose <input>");
		}
		else if (args.length == 2 && args[0].equals("--verbose")) {
			try {
				scheduleVerbose(args[1]);
			}

			catch (FileNotFoundException e) { //Need to check these to make sure it will actually catch things
				System.out.println("File not found");
			}
		}

		else if (args.length == 1) {
			try {
				schedule(args[0]);
			}

			catch (FileNotFoundException e) {
				System.out.println("File not found");
			}
		}

		else {
			System.out.println("Incorrect usage");
			System.out.println("Either use java scheduler <input> or java scheduler --verbose <input>");
		}
	}

	public static void schedule(String input) throws FileNotFoundException {
		File file = new File(input);

		if (!file.canRead()) { //Make sure the file actually exists
			throw new FileNotFoundException();
		}

		Scanner in = new Scanner(file);

		int numProcesses = in.nextInt();

		ArrayList<process> processList = new ArrayList<process>();

		int A;
		int B;
		int C;
		int M;

		//Get all the processes
		for (int i = 0; i < numProcesses; i++) {
			A = in.nextInt();
			B = in.nextInt();
			C = in.nextInt();
			M = in.nextInt();

			process toAdd = new process(A, B, C, M);
			processList.add(toAdd);
		}

		FCFS(processList);


		return;
	}

	public static void scheduleVerbose(String input) throws FileNotFoundException {
		File file = new File(input);

		if (!file.canRead()) {
			throw new FileNotFoundException();
		}

		return;
	}


	//I think for all of the below, I pass in the processes.
	//The question is if I have a seperate method for printing.

	//First Come First Serve
	//Things do switch when blocked
	//Also need to figure out how I'm gonna do ties. I think the way I have things sorted is that the 
	//processes have proper priority

	//Need to add something about current time, so processes that haven't arrived yet don't get started (could set state to 3, that way increment doesn't do anything)
	//(WOuld just need to add a few more conditional statements)
	public static void FCFS(ArrayList<process> processList) {
		int completed = 0;
		int currProcess = 0;
		ArrayList<process> mutableList = (ArrayList<process>) processList.clone(); //Make a list we can delete from
		ArrayList<process> returnList = new ArrayList<process>();
		//Selection sort each process based on arrival time
		//NB: there's definitely a more efficient way to do this but later
		for (int i = 0; i < processList.size(); i++) {
			int minIndex = i;
			for (int j = i + 1; j < processList.size(); j++) {
				if (processList.get(j).getA() < processList.get(minIndex).getA()) {
					minIndex = j;
				}

				process temp = processList.get(minIndex);
				processList.set(minIndex, processList.get(i));
				processList.set(i, temp);
			}
		}

		//Default setup

		//Set default states
		processList.get(0).setState(0); //First arrival will always be in 0
		for (int i = 1; i < processList.size(); i++) {
			processList.get(i).setState(1); //Set everything else to ready at first
		}

		int CPUburst = 0, IOburst;

		while (!mutableList.isEmpty()) { //while we still have processes to run
			try { //OK, we need our CPU burst
				CPUburst = RandomOS(mutableList.get(0).getB(), randFile);
				if (CPUburst > mutableList.get(0).getC() - mutableList.get(0).getTimeRun()) {
					CPUburst = mutableList.get(0).getC() - mutableList.get(0).getTimeRun();
				}
				mutableList.get(0).setRemainingCPU(CPUburst);
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(1);
			}

			while (mutableList.get(0).getRemainingCPU() != 0) { //While our FCFS process is running, increment everything
				for (int i = 0; i < mutableList.size(); i++) {
					mutableList.get(i).increment(); 
				}
			}

			//Need out IOburst
			IOburst = CPUburst * mutableList.get(0).getM();
			mutableList.get(0).setRemainingIO(IOburst);
			mutableList.get(0).setState(2); //Change to blocked and put in the back of the line
			process temp = mutableList.get(0);
			mutableList.remove(0);
			mutableList.add(temp);

			//Check if anything should be unblocked
			for (int i = 0; i < mutableList.size(); i++) {
				if (mutableList.get(i).getRemainingIO() == 0) {
					mutableList.get(i).setState(1); //If it's done with IO, it gets unblocked
				}
			}

			//Check if something finished
			if (mutableList.get(0).getC() == mutableList.get(0).getTimeRun()) {
				process toAdd = mutableList.get(0);
				mutableList.remove(0);
				returnList.add(toAdd); 
				completed++;
				continue;
			}

			//If there's only one process remaining, and we need to get things unblocked
			if (mutableList.size() == 1) {
				while (mutableList.get(0).getRemainingIO() != 0) {
					mutableList.get(0).increment();
				}
			}

			

			mutableList.get(0).setState(0);
			//Mark everything else as ready, as long as it's not blocked
			for (int i = 1; i < mutableList.size(); i++) {
				if (mutableList.get(i).getState() == 2 || mutableList.get(i).getState() == 0) {
					continue;
				}
				mutableList.get(i).setState(1); //Set everything else to ready at first
			}

		}





		//Printing stuff
		System.out.println("Scheduling algorithm: FCFS");		
		printSummary(returnList);
		

		return;
	}

	//Round Robin (q = 2)
	public static void RR(ArrayList<process> processList) {
		return;
	}

	//Shortest Job First
	public static void SJF(ArrayList<process> processList) {
		return;
	}

	//Highest Penalty Run Next (?)
	public static void HPRN (ArrayList<process> processList) {
		return;
	}

	//Gotta get that uniformly distributed RV, sahn!
	//Maybe wanna change exception type, but I think I can hardcode file name in
	public static int RandomOS(int U, String fileName) throws FileNotFoundException {
		File file = new File(fileName);

		if(!file.canRead()) {
			throw new FileNotFoundException();
		}
		Scanner getRV = new Scanner(file);

		int bigNum = getRV.nextInt();
		return 1 + (bigNum % U);
	}

	//Need to add in final summary here
	public static void printSummary(ArrayList<process> processList) {
		for (int i = 0; i < processList.size(); i++) {
			System.out.printf("Process %d: \n", i);
			System.out.printf("(A, B, C, M): %s\n", processList.get(i).toString());
			System.out.printf("Finishing Time: %d\n", processList.get(i).getFinish());
			System.out.printf("Turnaround time: %d\n", processList.get(i).getTurnaround());
			System.out.printf("IO Time: %d\n", processList.get(i).getIOtime());
			System.out.printf("Waiting: %d\n\n", processList.get(i).getWaiting());
		}
	}
}