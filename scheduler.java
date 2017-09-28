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

		ArrayList<process> processListFCFS = new ArrayList<process>();
		ArrayList<process> processListRR = new ArrayList<process>();
		ArrayList<process> processListSJF = new ArrayList<process>();
		ArrayList<process> processListHPRN = new ArrayList<process>();

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

			process toAdd1 = new process(A, B, C, M, i);
			process toAdd2 = new process(A, B, C, M, i);
			process toAdd3 = new process(A, B, C, M, i);
			process toAdd4 = new process(A, B, C, M, i);
			processListFCFS.add(toAdd1);
			processListRR.add(toAdd2);
			processListSJF.add(toAdd3);
			processListHPRN.add(toAdd4);
		}

		sortList(processListFCFS);
		sortList(processListRR);
		sortList(processListSJF);
		sortList(processListHPRN);

		FCFS(processListFCFS);


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
		int cycle = 0;
		int CPUburst = 0, IOburst;

		process running;
		LinkedList<process> ready = new LinkedList<process>();
		ArrayList<process> blocked = new ArrayList<process>();
		ArrayList<process> notStarted = new ArrayList<process>(); //Process that hasn't started
		ArrayList<process> finished = new ArrayList<process>(processList.size());

		//The process list is sorted based on arrival time, so first process in that list is running
		running = processList.get(0);

		try {
			CPUburst = RandomOS(running.getB(), randFile);
			running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
		}

		catch (FileNotFoundException e) {
			System.out.println("Random number generator not found");
			System.exit(0);
		}

		//Everything else is waiting
		for (int i = 1; i < processList.size(); i++) {
			if (processList.get(i).getA() == 0) {
				ready.addLast(processList.get(i)); //Put them in the back
			}

			else
				notStarted.add(processList.get(i));
		}

		//While there;s still processes to run
		while (completed < processList.size()) {
			//Blocked processes
			for (int i = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() == 0) { 
					ready.addLast(blocked.get(i));
					blocked.remove(i);
				}
				if (!blocked.isEmpty()) {
					blocked.get(i).increment(2);
	
				}			
			}

			if (running.getTimeRun() == running.getC()) {
				finished.add(running.getPID(), running);
				completed++;
			}
			//Running process
			running.increment(0);

			if (running.getRemainingCPU() == 0) {
				blocked.add(running);
				if (!ready.isEmpty()) {
					running = ready.pop();
				}
			}


			//Waiting process
			for (int i = 0; i < notStarted.size(); i++) {
				if (notStarted.get(i).getA() == cycle) {
					ready.addLast(notStarted.get(i));
				}
			}

			//Ready process
			for (int i = 0; i < ready.size(); i++) {
				ready.get(i).increment(1);
			}


			cycle++;
		}
	



		//Printing stuff
		System.out.println("Scheduling algorithm: FCFS");		
		printSummary(finished);
		

		return;
	}
	

	//Round Robin (q = 2)
	public static void RR(ArrayList<process> processList) {
		printSummary(processList);
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

	public static void sortList(ArrayList<process> processList) {
		//Sort the list based on arrival time for later for later
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