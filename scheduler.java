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
	final static File randNums = new File("random-numbers.txt");

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

			catch (FileNotFoundException e) { 
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

			process toAdd1 = new process(A, B, C, M);
			process toAdd2 = new process(A, B, C, M);
			process toAdd3 = new process(A, B, C, M);
			process toAdd4 = new process(A, B, C, M);
			processListFCFS.add(toAdd1);
			processListRR.add(toAdd2);
			processListSJF.add(toAdd3);
			processListHPRN.add(toAdd4);
		}

		sortList(processListFCFS);
		sortList(processListRR);
		sortList(processListSJF);
		sortList(processListHPRN);

		try {

			FCFS(processListFCFS, false);
			//RR(processListRR, false);
			//SJF(processListSJF, false);
			//HPRN(processListHPRN, false);
		}

		catch (Exception e) {
			System.out.println("Error:");
			e.printStackTrace();
		}


		return;
	}

	public static void scheduleVerbose(String input) throws FileNotFoundException {
		File file = new File(input);

		if (!file.canRead()) {
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

			process toAdd1 = new process(A, B, C, M);
			process toAdd2 = new process(A, B, C, M);
			process toAdd3 = new process(A, B, C, M);
			process toAdd4 = new process(A, B, C, M);
			processListFCFS.add(toAdd1);
			processListRR.add(toAdd2);
			processListSJF.add(toAdd3);
			processListHPRN.add(toAdd4);
		}



		sortList(processListFCFS);
		sortList(processListRR);
		sortList(processListSJF);
		sortList(processListHPRN);


		for (int i = 0; i < processListFCFS.size(); i++) {
			System.out.println(processListFCFS.get(i).toString());
		}

		try {
			FCFS(processListFCFS, true);
			//RR(processListRR, true);
			//SJF(processListSJF, true);
			//HPRN(processListHPRN, true);
		}

		catch (Exception e) {
			System.out.println("Error:");
			e.printStackTrace();
		}
		return;
	}
	//First Come First Serve
	//Things do switch when blocked
	//Also need to figure out how I'm gonna do ties. I think the way I have things sorted is that the 
	//processes have proper priority

	//Input 4 and 5 need fixing (problem from 4 might be coming from not changing null running to running properly)


	/* Idea to consider:
	beginning of the while loop: if (ready == null && !ready.isEmpty())
	ready = ready.pop()
	Or something. I think this is why things aren't adding up properly for the longer runs.
	*/

	public static void FCFS(ArrayList<process> processList, boolean verbose) throws Exception {

		Scanner randFile = new Scanner(randNums);

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

		//Everything else is waiting
		for (int i = 1; i < processList.size(); i++) {
			if (processList.get(i).getA() == 0) {
				ready.addLast(processList.get(i)); //Put them in the back
			}

			else
				notStarted.add(processList.get(i));
		}

		if (running != null) { //Getting our necessary time
			try {
				CPUburst = RandomOS(running.getB(), randFile); 
				running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(0);
			}
		}

		//Printing for verbose mode. Gonna need to add how much time it has left
		while (finished.size() < processList.size()) {
			//To see if there's a tiebreak that needs fixing
			if (running != null && running.getA() == cycle) { //I don't think this is going to work because if there's more than one process, it might get knocked too far back
				if (!ready.isEmpty() && ready.peek().getPID() < running.getPID()) {
					ready.addLast(running);
					running = ready.pop();

					if (running.getRemainingCPU() <= 0) {
						try {
							CPUburst = RandomOS(running.getB(), randFile);
							running.setRemainingCPU(CPUburst);
						}

						catch (FileNotFoundException e) {
							System.out.println("RNG not found");
						}
					}
				}
			}

			if (verbose) { 
				System.out.printf("\nBefore cycle %d:\n", cycle + 1);
				for (int i = 0; i < processList.size(); i ++) {
					if (running != null && running.getPID() == i) {
						System.out.printf("Process %d is running (%d)  ", running.getPID(), running.getRemainingCPU());
					}

					for (int j = 0; j < ready.size(); j++) {
						if (ready.get(j).getPID() == i) {
							System.out.printf("Process %d is ready   ", ready.get(j).getPID());
						}
					}


					for (int j = 0; j < blocked.size(); j++) {
						if (blocked.get(j).getPID() == i) {
							System.out.printf("Process %d is blocked (%d)   ", blocked.get(j).getPID(), blocked.get(j).getRemainingIO());
						}
					}


					for (int j = 0; j < notStarted.size(); j++) {
						if (notStarted.get(j).getPID() == i) {
							System.out.printf("Process %d has not started   ", notStarted.get(j).getPID());
						}
					}

					for (int j = 0; j < finished.size(); j++) {
						if (finished.get(j).getPID() == i) {
							System.out.printf("Process %d has finished   ", finished.get(j).getPID());
						}
					}
				}
			}



			//Blocked process
			for (int i = 0; i < blocked.size(); i ++) {
				blocked.get(i).increment(2);
			}

			//running process
			if (running != null) {
				running.increment(0);
			}

			//Waiting process
			for (int i = 0; i < notStarted.size(); i++) {
				if (cycle + 1 == notStarted.get(i).getA()) { //It's at the end of the cycle, so we need to know if it should be added
					ready.addLast(notStarted.get(i)); //into the ready list for the next cycle
				}

				else {
					notStarted.get(i).increment(3);
				}
			}

			for (int i = 0; i < notStarted.size(); i++) {
				if (ready.contains(notStarted.get(i))) {
					notStarted.remove(i);
					i--; //Since something got removed, the list size shrinks, and we need to account for that
				}
			}

			//Ready process
			for (int i  = 0; i < ready.size(); i++) {
				ready.get(i).increment(1);
			}

			if (running != null && running.getTimeRun() == running.getC()) {
				finished.add(running);
				running = null;
			}

			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = CPUburst * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()) {
					running = ready.pop();
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						if (CPUburst > running.getC() - running.getTimeRun()) {
							CPUburst = running.getC() - running.getTimeRun();
						}
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
			
				}

				else {
					running = null; //Can eventually use this as a check for if there's one process left
				}
			}



			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) {
					ready.addLast(blocked.get(i));
					blocked.remove(i);
				}
			}

			if (running == null && !ready.isEmpty()) {
				running = ready.pop(); //If there's one process left, bring that bitch back
				try {
					CPUburst = RandomOS(running.getB(), randFile); 
					running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
				}

				catch (FileNotFoundException e) {
					System.out.println("Random number generator not found");
					System.exit(0);
				}
			}

			cycle++;

		}

		//Printing stuff
		System.out.println("\nScheduling algorithm: FCFS");		
		printSummary(finished);

		randFile.close();
		

		return;
	}
	

	//Round Robin (q = 2)
	//Same general idea as above, just have an additional check if something's been running for two to cause a swap

	//Ties aren't being broken properly (have an additional check against PID)
	//pop it and make it running, but then peek the next one to see if we need to tie break
	public static void RR(ArrayList<process> processList, boolean verbose) throws Exception {
		Scanner randFile = new Scanner(randNums);

		int completed = 0;
		int cycle = 0;
		int quant = 0; //Use this as an additional check for if we need to make the switcheroo
		int CPUburst = 0, IOburst;
		process running;

		LinkedList<process> ready = new LinkedList<process>();

		ArrayList<process> blocked = new ArrayList<process>();
		ArrayList<process> notStarted = new ArrayList<process>(); //Process that hasn't started
		ArrayList<process> finished = new ArrayList<process>(processList.size());

		//The process list is sorted based on arrival time, so first process in that list is running
		running = processList.get(0);

		//Everything else is waiting
		for (int i = 1; i < processList.size(); i++) {
			if (processList.get(i).getA() == 0) {
				ready.addLast(processList.get(i)); //Put them in the back
			}

			else
				notStarted.add(processList.get(i));
		}

		if (running != null) { //Getting our necessary time
			try {
				CPUburst = RandomOS(running.getB(), randFile); 
				running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(0);
			}
		}

		//Printing for verbose mode. Gonna need to add how much time it has left
		while (finished.size() < processList.size()) {
			if (verbose) { 
				System.out.printf("\nBefore cycle %d:\n", cycle);
				for (int i = 0; i < processList.size(); i ++) {
					if (running != null && running.getPID() == i) {
						System.out.printf("Process %d is running (%d)  ", running.getPID(), running.getRemainingCPU());
					}

					for (int j = 0; j < ready.size(); j++) {
						if (ready.get(j).getPID() == i) {
							System.out.printf("Process %d is ready   ", ready.get(j).getPID());
						}
					}


					for (int j = 0; j < blocked.size(); j++) {
						if (blocked.get(j).getPID() == i) {
							System.out.printf("Process %d is blocked (%d)   ", blocked.get(j).getPID(), blocked.get(j).getRemainingIO());
						}
					}


					for (int j = 0; j < notStarted.size(); j++) {
						if (notStarted.get(j).getPID() == i) {
							System.out.printf("Process %d has not started   ", notStarted.get(j).getPID());
						}
					}

										for (int j = 0; j < finished.size(); j++) {
						if (finished.get(j).getPID() == i) {
							System.out.printf("Process %d has finished   ", finished.get(j).getPID());
						}
					}
				}
			}



			//Blocked process
			for (int i = 0; i < blocked.size(); i ++) {
				blocked.get(i).increment(2);
			}

			//running process
			if (running != null) {
				running.increment(0);
				quant++;
			}

			//Waiting process
			for (int i = 0; i < notStarted.size(); i++) {
				if (cycle == notStarted.get(i).getA()) {
					ready.addLast(notStarted.get(i));
				}

				else {
					notStarted.get(i).increment(3);
				}
			}

			for (int i = 0; i < notStarted.size(); i++) {
				if (ready.contains(notStarted.get(i))) {
					notStarted.remove(i);
				}
			}

			//Ready process
			for (int i  = 0; i < ready.size(); i++) {
				ready.get(i).increment(1);
			}

			if (running != null && running.getTimeRun() == running.getC()) {
				finished.add(running);
				running = null;
			}

			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = CPUburst * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()) {
					running = ready.pop();
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						if (CPUburst > running.getC() - running.getTimeRun()) {
							CPUburst = running.getC() - running.getTimeRun();
						}
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
			
				}

				else {
					running = null; //Can eventually use this as a check for if there's one process left
				}
				quant = 0;
			}

			else if (running != null && quant >= 2) {
				if (ready.isEmpty()) {
					//If no one else is ready, just keep going
				}
				else if (!ready.isEmpty()) {
					ready.addLast(running);
					running = ready.pop();

					if (running.getRemainingCPU() <= 0) { //If the new process hasn't actually started yet
						try {
							CPUburst = RandomOS(running.getB(), randFile);
							running.setRemainingCPU(CPUburst);
						}

						catch(FileNotFoundException e) {} //Laziness
					}
				}
				quant = 0;
			}



			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) {
					ready.addLast(blocked.get(i));
					blocked.remove(i);
				}
			}

			if (running == null && !ready.isEmpty()) {
				running = ready.pop(); //If there's one process left, bring that bitch back
				try {
					CPUburst = RandomOS(running.getB(), randFile); 
					running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
				}

				catch (FileNotFoundException e) {
					System.out.println("Random number generator not found");
					System.exit(0);
				}
			}

			cycle++;

		}

		System.out.println("\nScheduling Algorithm: RR");
		printSummary(processList);

		randFile.close();
		return;
	}

	//Shortest Job First
	//After every cycle, run through the ready list for the shortest one.
	//Also, 4 and 5 still don't work
	public static void SJF(ArrayList<process> processList, boolean verbose) throws Exception {
		Scanner randFile = new Scanner(randNums);
		int origSize = processList.size();

		int completed = 0;
		int cycle = 0;
		int CPUburst = 0, IOburst;
		process running;

		ArrayList<process> ready = new ArrayList<process>();

		ArrayList<process> blocked = new ArrayList<process>();
		ArrayList<process> notStarted = new ArrayList<process>(); //Process that hasn't started
		ArrayList<process> finished = new ArrayList<process>();

		//Finding our shortest guy to start with
		int minIndex = 0;
		for (int i = 0; i < processList.size(); i++) {
			if (processList.get(i).getC() < processList.get(minIndex).getC() && processList.get(i).getA() <= 0) { //In general, it's gonna be getC - timeRun
				minIndex = i;
			}
		}

		running = processList.get(minIndex);
		processList.remove(minIndex);

		//Everything else is waiting
		for (int i = 0; i < processList.size(); i++) { //Since we removed the first one running, we start back at zero and go up to the new size
			if (processList.get(i).getA() == 0) {
				ready.add(processList.get(i)); //Put them in the back
			}

			else
				notStarted.add(processList.get(i));
		}

		if (running != null) { //Getting our necessary time
			try {
				CPUburst = RandomOS(running.getB(), randFile); 
				running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(0);
			}
		}

		//Printing for verbose mode. Gonna need to add how much time it has left
		while (finished.size() < origSize ) {
			//Maybe check for new shortest here?

			minIndex = -1;
			int shortest = 1000000; //Big sentinel
			for (int i = 0; i < ready.size(); i++) {
				if (ready.get(i).getC() - ready.get(i).getTimeRun() < running.getC() - running.getTimeRun()) { //Bug is where I get you
					minIndex = i;
					shortest = ready.get(i).getC() - ready.get(i).getTimeRun();
				}
			}

			//Check for non-zero CPU time around here
			if (minIndex >= 0 && !ready.isEmpty()) {
				ready.add(running);
				running = ready.get(minIndex);
				ready.remove(minIndex);
				if (running.getRemainingCPU() <= 0) {
					try {
						CPUburst = RandomOS(running.getB(), randFile);
						running.setRemainingCPU(CPUburst);
						if (CPUburst > running.getC() - running.getTimeRun()) {
							CPUburst = running.getC() - running.getTimeRun();
						}
					}

					catch (FileNotFoundException e) {
						System.out.println("RNG not found");
					}
				}
			}


			if (verbose) { 
				System.out.printf("\nBefore cycle %d:\n", cycle);
				for (int i = 0; i < origSize; i ++) {
					if (running != null && running.getPID() == i) {
						System.out.printf("Process %d is running (%d)  ", running.getPID(), running.getRemainingCPU());
					}

					for (int j = 0; j < ready.size(); j++) {
						if (ready.get(j).getPID() == i) {
							System.out.printf("Process %d is ready   ", ready.get(j).getPID());
						}
					}


					for (int j = 0; j < blocked.size(); j++) {
						if (blocked.get(j).getPID() == i) {
							System.out.printf("Process %d is blocked (%d)   ", blocked.get(j).getPID(), blocked.get(j).getRemainingIO());
						}
					}


					for (int j = 0; j < notStarted.size(); j++) {
						if (notStarted.get(j).getPID() == i) {
							System.out.printf("Process %d has not started   ", notStarted.get(j).getPID());
						}
					}

					for (int j = 0; j < finished.size(); j++) {
						if (finished.get(j).getPID() == i) {
							System.out.printf("Process %d has finished   ", finished.get(j).getPID());
						}
					}
				}
			}



			//Blocked process
			for (int i = 0; i < blocked.size(); i ++) {
				blocked.get(i).increment(2);
			}

			//running process
			if (running != null) {
				running.increment(0);
			}

			//Waiting process
			for (int i = 0; i < notStarted.size(); i++) {
				if (cycle == notStarted.get(i).getA()) {
					ready.add(notStarted.get(i));
				}

				else {
					notStarted.get(i).increment(3);
				}
			}

			for (int i = 0; i < notStarted.size(); i++) {
				if (ready.contains(notStarted.get(i))) {
					notStarted.remove(i);
				}
			}

			//Ready process
			for (int i  = 0; i < ready.size(); i++) {
				ready.get(i).increment(1);
			}

			if (running != null && running.getTimeRun() == running.getC()) {
				finished.add(running);
				running = null;
			}

			//Check for a new shortest yob
			//Keep track of currently shortest ready job
			//Swap if needed

			minIndex = 0;
			shortest = 1000000;
			for (int i = 0; i < ready.size(); i++) {
				if (ready.get(i).getC() - ready.get(i).getTimeRun() < shortest) { //Bug is where I get you
					minIndex = i;
					shortest = ready.get(i).getC() - ready.get(i).getTimeRun();
				}
			}


			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = CPUburst * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()) {
					running = ready.get(minIndex); //Had to change from .pop() since it's no longer a linked list. I might be where a bug is
					ready.remove(minIndex);
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						if (CPUburst > running.getC() - running.getTimeRun()) {
							CPUburst = running.getC() - running.getTimeRun();
						}
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
			
				}

				else {
					running = null; //Can eventually use this as a check for if there's one process left
				}
			}

			if (minIndex > ready.size() - 1) {
				minIndex = 0;
			}


			//New shortest job requires a switch
			if (running != null && !ready.isEmpty() && !ready.get(minIndex).equals(running)) {

				ready.add(running);
				running = ready.get(minIndex);
				ready.remove(minIndex);
			}



			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) {
					ready.add(blocked.get(i));
					blocked.remove(i);
				}
			}

			if (running == null && !ready.isEmpty()) {
				running = ready.get(0); //If there's one process left, bring that bitch back
				ready.remove(0);
				try {
					CPUburst = RandomOS(running.getB(), randFile); 
					running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					if (CPUburst > running.getC() - running.getTimeRun()) {
						CPUburst = running.getC() - running.getTimeRun();
					}
				}

				catch (FileNotFoundException e) {
					System.out.println("Random number generator not found");
					System.exit(0);
				}
			}

			cycle++;

		}

		//Printing stuff
		System.out.println("\nScheduling algorithm: SJF");		
		printSummary(finished);

		randFile.close();
		return;
	}

	//Highest Penalty Ratio Next (?)
	//Can really do the same thing as SJF, but replace checking job length with checking penalty ratio

	//Not switching properly when things enter the system
	public static void HPRN (ArrayList<process> processList, boolean verbose) throws Exception {
		Scanner randFile = new Scanner(randNums);
		int origSize = processList.size();

		int minIndex = 0; //NB: change these all to max

		int completed = 0;
		int cycle = 0;
		int CPUburst = 0, IOburst;
		process running;

		ArrayList<process> ready = new ArrayList<process>();

		ArrayList<process> blocked = new ArrayList<process>();
		ArrayList<process> notStarted = new ArrayList<process>(); //Process that hasn't started
		ArrayList<process> finished = new ArrayList<process>();

		//Finding our shortest guy to start with
		running = processList.get(0);

		//Everything else is waiting
		for (int i = 1; i < processList.size(); i++) {
			if (processList.get(i).getA() == 0) {
				ready.add(processList.get(i)); //Put them in the back
			}

			else
				notStarted.add(processList.get(i));
		}

		if (running != null) { //Getting our necessary time
			try {
				CPUburst = RandomOS(running.getB(), randFile); 
				running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(0);
			}
		}

		//Printing for verbose mode. Gonna need to add how much time it has left
		while (finished.size() < origSize ) {
			//Maybe check for new shortest here?

			minIndex = -1;
			int shortest = -1000000; //Big sentinel
			for (int i = 0; i < ready.size(); i++) {
				if (ready.get(i).getPenaltyRatio() > running.getPenaltyRatio()) { //Bug is where I get you
					minIndex = i;
					shortest = ready.get(i).getPenaltyRatio();
				}
			}

			//Check for non-zero CPU time around here
			if (minIndex >= 0 && !ready.isEmpty()) {
				ready.add(running);
				running = ready.get(minIndex);
				ready.remove(minIndex);
				if (running.getRemainingCPU() <= 0) {
					try {
						CPUburst = RandomOS(running.getB(), randFile);
						running.setRemainingCPU(CPUburst);
						if (CPUburst > running.getC() - running.getTimeRun()) {
							CPUburst = running.getC() - running.getTimeRun();
						}
					}

					catch (FileNotFoundException e) {
						System.out.println("RNG not found");
					}
				}
			}


			if (verbose) { 
				System.out.printf("\nBefore cycle %d:\n", cycle);
				for (int i = 0; i < origSize; i ++) {
					if (running != null && running.getPID() == i) {
						System.out.printf("Process %d is running (%d)  ", running.getPID(), running.getRemainingCPU());
					}

					for (int j = 0; j < ready.size(); j++) {
						if (ready.get(j).getPID() == i) {
							System.out.printf("Process %d is ready   ", ready.get(j).getPID());
						}
					}


					for (int j = 0; j < blocked.size(); j++) {
						if (blocked.get(j).getPID() == i) {
							System.out.printf("Process %d is blocked (%d)   ", blocked.get(j).getPID(), blocked.get(j).getRemainingIO());
						}
					}


					for (int j = 0; j < notStarted.size(); j++) {
						if (notStarted.get(j).getPID() == i) {
							System.out.printf("Process %d has not started   ", notStarted.get(j).getPID());
						}
					}

					for (int j = 0; j < finished.size(); j++) {
						if (finished.get(j).getPID() == i) {
							System.out.printf("Process %d has finished   ", finished.get(j).getPID());
						}
					}
				}
			}



			//Blocked process
			for (int i = 0; i < blocked.size(); i ++) {
				blocked.get(i).increment(2);
			}

			//running process
			if (running != null) {
				running.increment(0);
			}

			//Waiting process
			for (int i = 0; i < notStarted.size(); i++) {
				if (cycle == notStarted.get(i).getA()) {
					ready.add(notStarted.get(i));
				}

				else {
					notStarted.get(i).increment(3);
				}
			}

			for (int i = 0; i < notStarted.size(); i++) {
				if (ready.contains(notStarted.get(i))) {
					notStarted.remove(i);
				}
			}

			//Ready process
			for (int i  = 0; i < ready.size(); i++) {
				ready.get(i).increment(1);
			}

			if (running != null && running.getTimeRun() == running.getC()) {
				finished.add(running);
				running = null;
			}

			//Check for a new shortest yob
			//Keep track of currently shortest ready job
			//Swap if needed

			minIndex = 0;
			shortest = -1000000;
			for (int i = 0; i < ready.size(); i++) {
				if (ready.get(i).getPenaltyRatio() > shortest) { //Bug is where I get you
					minIndex = i;
					shortest = ready.get(i).getPenaltyRatio();
				}
			}


			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = CPUburst * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()) {
					running = ready.get(minIndex); //Had to change from .pop() since it's no longer a linked list. I might be where a bug is
					ready.remove(minIndex);
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						if (CPUburst > running.getC() - running.getTimeRun()) {
							CPUburst = running.getC() - running.getTimeRun();
						}
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
			
				}

				else {
					running = null; //Can eventually use this as a check for if there's one process left
				}
			}

			if (minIndex > ready.size() - 1) {
				minIndex = 0;
			}


			//New shortest job requires a switch
			if (running != null && !ready.isEmpty() && !ready.get(minIndex).equals(running)) {

				ready.add(running);
				running = ready.get(minIndex);
				ready.remove(minIndex);
			}



			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) {
					ready.add(blocked.get(i));
					blocked.remove(i);
				}
			}

			if (running == null && !ready.isEmpty()) {
				running = ready.get(0); //If there's one process left, bring that bitch back
				ready.remove(0);
				try {
					CPUburst = RandomOS(running.getB(), randFile); 
					running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					if (CPUburst > running.getC() - running.getTimeRun()) {
						CPUburst = running.getC() - running.getTimeRun();
					}
				}

				catch (FileNotFoundException e) {
					System.out.println("Random number generator not found");
					System.exit(0);
				}
			}

			cycle++;

		}

		//Printing stuff
		System.out.println("\nScheduling algorithm: HPRN");		
		printSummary(finished);

		randFile.close();
		return;
	}

	//Maybe have this return an actual list, and pass that in instead.
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

		for (int i = 0; i < processList.size(); i++) {
			processList.get(i).setPID(i);
		}
	}

	//Gotta get that uniformly distributed RV, sahn!
	//Maybe wanna change exception type, but I think I can hardcode file name in
	//This might need some editing. I don't know if how I have the RV reader working
	public static int RandomOS(int U, Scanner getRV) throws FileNotFoundException {
		//File file = new File(fileName);

		// if(!Scanner.canRead()) {
		// 	throw new FileNotFoundException();
		// }
		// Scanner getRV = new Scanner(file);

		int bigNum = getRV.nextInt();
		//System.out.println(bigNum);
		return 1 + (bigNum % U);
	}

	//Need to add in final summary here
	public static void printSummary(ArrayList<process> processList) {

		for (int i = 0; i < processList.size(); i++) {
			for (int j = 0; j < processList.size(); j++) {
				if (processList.get(j).getPID() == i) {
					System.out.printf("Process %d: \n", processList.get(j).getPID());
					System.out.printf("(A, B, C, M): %s\n", processList.get(j).toString());
					System.out.printf("Finishing Time: %d\n", processList.get(j).getFinish());
					System.out.printf("Turnaround time: %d\n", processList.get(j).getTurnaround());
					System.out.printf("IO Time: %d\n", processList.get(j).getIOtime());
					System.out.printf("Waiting: %d\n\n", processList.get(j).getWaiting());
				}
			}
		}
	}
}