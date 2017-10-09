import java.util.*;
import java.io.*;

//Realizing now the while loop to get something that was just added to the end might cause an infinite loop
//Depending on what other inputs they use
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
			RR(processListRR, false);
			SJF(processListSJF, false);
			HPRN(processListHPRN, false);
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
			RR(processListRR, true);
			SJF(processListSJF, true);
			HPRN(processListHPRN, true);
		}

		catch (Exception e) {
			System.out.println("Error:");
			e.printStackTrace();
		}
		return;
	}

	//First Come First Serve
	public static void FCFS(ArrayList<process> processList, boolean verbose) throws Exception {

		Scanner randFile = new Scanner(randNums);

		int CPUused = 1;
		int IOused = 0;

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
					notStarted.get(i).increment(3);
					ready.addLast(notStarted.get(i)); //into the ready list for the next cycle
				}

				else {
					notStarted.get(i).increment(4);
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

				if (!ready.isEmpty() && ready.peek().getA() != cycle + 1) {
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

			sortListPID(blocked);

			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) { //The issue is if more than one process gets unblocked
					ready.addLast(blocked.get(i));
					blocked.remove(i);
					i--;
				}
			}

			if (running == null && !ready.isEmpty()) {				
				while (ready.peek().getA() == cycle + 1) {
					process temp = ready.pop();
					ready.addLast(temp);
				}
				running = ready.pop(); //If there's one process left, bring that bitch back
				if (running.getRemainingCPU() <= 0) {
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
				}
			}
			cycle++;

			if (running != null) {
				CPUused++;
			}

			if (!blocked.isEmpty()) {
				IOused++;
			}

		}

		//Printing stuff
		System.out.println("\nScheduling algorithm: FCFS\n");		
		printSummary(finished, CPUused, IOused);

		randFile.close();
		

		return;
	}
	

	//Round Robin (q = 2)
	public static void RR(ArrayList<process> processList, boolean verbose) throws Exception {
		Scanner randFile = new Scanner(randNums);

		int readied = 0; //To check if we need to settle a tie

		int CPUused = 1;
		int IOused = 0;

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
				running.setCPUburst(CPUburst);
				running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(0);
			}
		}

		//Printing for verbose mode. 
		while (finished.size() < processList.size()) {
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
				quant++;
			}

			//Waiting process
			for (int i = 0; i < notStarted.size(); i++) {
				if (cycle + 1 == notStarted.get(i).getA()) { //It's at the end of the cycle, so we need to know if it should be added
					notStarted.get(i).increment(3);
					ready.addLast(notStarted.get(i)); //into the ready list for the next cycle
				}

				else {
					notStarted.get(i).increment(4);
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

			//Finished
			if (running != null && running.getTimeRun() == running.getC()) {
				finished.add(running);
				running = null;
				quant = 0;
			}

			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = running.getCPUburst() * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()  && ready.peek().getA() != cycle + 1 && quant < 2) {
					running = ready.pop();
					if (running.getRemainingCPU() <= 0) {
						try {
							CPUburst = RandomOS(running.getB(), randFile); 
							if (CPUburst > running.getC() - running.getTimeRun()) {
								CPUburst = running.getC() - running.getTimeRun();
							}
							running.setCPUburst(CPUburst);
							running.setRemainingCPU(CPUburst); 
						}

						catch (FileNotFoundException e) {
							System.out.println("Random number generator not found");
							System.exit(0);
						}
					}
			
				}

				else {
					running = null; //Can eventually use this as a check for if there's one process left
				}
				quant = 0;
			}

			else if (running != null && quant >= 2) {
				readied++;
				if (!ready.isEmpty() && ready.getLast().getA() == cycle + 1) { //This only applies for when something new is added
					int k = ready.size() - 1;
					while (ready.get(k).getA() == cycle + 1) {
						k--;
					} 
					ready.add(k + 1, running);
				}

				else {
					ready.addLast(running);
				}
				running = null;
				quant = 0;
			}

			sortListPID(blocked);

			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) { 
					if (readied >= 1) {
						if (blocked.get(i).getPID() < ready.getLast().getPID()) {
							process temp = ready.pollLast();
							ready.addLast(blocked.get(i));
							ready.addLast(temp);
						}

						else {
							ready.addLast(blocked.get(i));
						}

					}
					

					else {
						ready.addLast(blocked.get(i));
					}
					readied++;
					blocked.remove(i);
					i--;
				}
			}

			if (running == null && !ready.isEmpty()) {
				while (ready.peek().getA() == cycle + 1) {
					process temp = ready.pop();
					ready.addLast(temp);
				}
				running = ready.pop(); //If there's one process left, bring that bitch back
				if (running.getRemainingCPU() <= 0) {
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						running.setCPUburst(CPUburst);
						running.setRemainingCPU(CPUburst); 
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
				}
			}
			cycle++;
			readied = 0;

			if (running != null) {
				CPUused++;
			}

			if (!blocked.isEmpty()) {
				IOused++;
			}

		}

		System.out.println("\nScheduling Algorithm: RR\n");
		printSummary(finished, CPUused, IOused);

		randFile.close();
		return;
	}

	//Shortest Job First 
	public static void SJF(ArrayList<process> processList, boolean verbose) throws Exception {

		Scanner randFile = new Scanner(randNums);

		int CPUused = 1;
		int IOused = 0;

		int completed = 0;
		int cycle = 0;
		int CPUburst = 0, IOburst;
		process running;

		ArrayList<process> ready = new ArrayList<process>();

		ArrayList<process> blocked = new ArrayList<process>();
		ArrayList<process> notStarted = new ArrayList<process>(); //Process that hasn't started
		ArrayList<process> finished = new ArrayList<process>(processList.size());

		int origSize = processList.size();

		int minIndex = 0; //For finding our shortest yob
		int shortestTime = 100000; //Large default sentinel

		//Find our original shortest process
		for (int i = 0; i < processList.size(); i++) {
			if (processList.get(i).getC() < shortestTime && processList.get(i).getA() <= 0) {
				minIndex = i;
				shortestTime = processList.get(i).getC();
			}
		}

		running = processList.get(minIndex);
		processList.remove(minIndex);

		//Everything else is waiting
		for (int i = 0; i < processList.size(); i++) {
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
		while (finished.size() < origSize) {

			if (verbose) { 
				System.out.printf("\nBefore cycle %d:\n", cycle + 1);
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
				if (cycle + 1 == notStarted.get(i).getA()) { //It's at the end of the cycle, so we need to know if it should be added
					notStarted.get(i).increment(3);
					ready.add(notStarted.get(i)); //into the ready list for the next cycle
				}

				else {
					notStarted.get(i).increment(4);
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

			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) { //The issue is if more than one process gets unblocked
					ready.add(blocked.get(i));
					blocked.remove(i);
					i--;
				}
			}

			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = CPUburst * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()) {
					//Need to find new shortest job here
					minIndex = 0;
					shortestTime = 100000;
					for (int i = 0; i < ready.size(); i++) {
						if (ready.get(i).getC() - ready.get(i).getTimeRun() < shortestTime) {
							minIndex = i;
							shortestTime = ready.get(i).getC() - ready.get(i).getTimeRun();
						}
					}

					running = ready.get(minIndex);
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

			sortListPID(blocked);



			if (running == null && !ready.isEmpty()) {				
				minIndex = 0;
				shortestTime = 100000;
				for (int i = 0; i < ready.size(); i++) {
					if (ready.get(i).getC() - ready.get(i).getTimeRun() < shortestTime) {
						minIndex = i;
						shortestTime = ready.get(i).getC() - ready.get(i).getTimeRun();
					}
				}

				running = ready.get(minIndex);
				ready.remove(minIndex);
				if (running.getRemainingCPU() <= 0) {
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
				}
			}
			cycle++;

			if (running != null) {
				CPUused++;
			}

			if (!blocked.isEmpty()) {
				IOused++;
			}

		}

		//Printing stuff
		System.out.println("\nScheduling algorithm: SJF\n");		
		printSummary(finished, CPUused, IOused);

		randFile.close();
		

		return;

	}

	//Highest Penalty Ratio Next 
	//Technically, input 5 has the same output, but two processes are swapped around
	public static void HPRN (ArrayList<process> processList, boolean verbose) throws Exception {
		Scanner randFile = new Scanner(randNums);

		int CPUused = 1; 
		int IOused = 0;

		int completed = 0;
		int cycle = 0;
		int CPUburst = 0, IOburst;
		process running;

		ArrayList<process> ready = new ArrayList<process>();

		ArrayList<process> blocked = new ArrayList<process>();
		ArrayList<process> notStarted = new ArrayList<process>(); //Process that hasn't started
		ArrayList<process> finished = new ArrayList<process>(processList.size());

		int origSize = processList.size();

		int maxIndex = 0; //For finding our shortest yob
		float highRatio = -100000; //Large default sentinel

		//Find our original process
		for (int i = 0; i < processList.size(); i++) {
			if (processList.get(i).getPenaltyRatio() > highRatio && processList.get(i).getA() <= 0) {
				maxIndex = i;
				highRatio = processList.get(i).getPenaltyRatio();
			}
		}

		running = processList.get(maxIndex);
		processList.remove(maxIndex);

		//Everything else is waiting
		for (int i = 0; i < processList.size(); i++) {
			if (processList.get(i).getA() == 0) {
				ready.add(processList.get(i)); //Put them in the back
			}

			else
				notStarted.add(processList.get(i));
		}

		if (running != null) { //Getting our necessary time
			try {
				CPUburst = RandomOS(running.getB(), randFile); 
				running.setRemainingCPU(CPUburst); 
			}

			catch (FileNotFoundException e) {
				System.out.println("Random number generator not found");
				System.exit(0);
			}
		}


		//Printing for verbose mode. Gonna need to add how much time it has left
		while (finished.size() < origSize) {

			if (verbose) { 
				System.out.printf("\nBefore cycle %d:\n", cycle + 1);
				for (int i = 0; i < origSize; i ++) {
					if (running != null && running.getPID() == i) {
						System.out.printf("Process %d is running (%d) ", running.getPID(), running.getRemainingCPU());
					}

					for (int j = 0; j < ready.size(); j++) {
						if (ready.get(j).getPID() == i) {
							System.out.printf("Process %d is ready  ", ready.get(j).getPID());
						}
					}


					for (int j = 0; j < blocked.size(); j++) {
						if (blocked.get(j).getPID() == i) {
							System.out.printf("Process %d is blocked (%d)  ", blocked.get(j).getPID(), blocked.get(j).getRemainingIO());
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
					notStarted.get(i).increment(3);
					ready.add(notStarted.get(i)); //into the ready list for the next cycle
				}

				else {
					notStarted.get(i).increment(4);
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

			for (int i  = 0; i < blocked.size(); i++) {
				if (blocked.get(i).getRemainingIO() <= 0) {
					ready.add(blocked.get(i));
					blocked.remove(i);
					i--;
				}
			}

			//Check if things need to be swapped out
			if (running != null && running.getRemainingCPU() <= 0) {
				IOburst = CPUburst * running.getM();
				running.setRemainingIO(IOburst);
				blocked.add(running);

				if (!ready.isEmpty()) {
					maxIndex = 0;
					highRatio = -100000;
					for (int i = 0; i < ready.size(); i++) {
						if (ready.get(i).getPenaltyRatio() > highRatio) {
							maxIndex = i;
							highRatio = ready.get(i).getPenaltyRatio();
						}
					}

					running = ready.get(maxIndex);
					ready.remove(maxIndex);
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

			sortListPID(blocked);



			if (running == null && !ready.isEmpty()) {				
				maxIndex = 0;
				highRatio = -100000;
				for (int i = 0; i < ready.size(); i++) {
					if (ready.get(i).getPenaltyRatio() > highRatio) {
						maxIndex = i;
						highRatio = ready.get(i).getPenaltyRatio();
					}
				}

				running = ready.get(maxIndex);
				ready.remove(maxIndex);
				if (running.getRemainingCPU() <= 0) {
					try {
						CPUburst = RandomOS(running.getB(), randFile); 
						running.setRemainingCPU(CPUburst); //Maybe add in check for the burst being longer than finishing time
					}

					catch (FileNotFoundException e) {
						System.out.println("Random number generator not found");
						System.exit(0);
					}
				}
			}
			cycle++;

			if (running != null) {
				CPUused++;
			}

			if (!blocked.isEmpty()) {
				IOused++;
			}

		}

		//Printing stuff
		System.out.println("\nScheduling algorithm: HPRN\n");		
		printSummary(finished, CPUused, IOused);

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

	public static void sortListPID(ArrayList<process> processList) {
		//Sort the list based on arrival time for later for later
		for (int i = 0; i < processList.size(); i++) {
			int minIndex = i;
			for (int j = i + 1; j < processList.size(); j++) {
				if (processList.get(j).getPID() < processList.get(minIndex).getPID()) {
					minIndex = j;
				}

				process temp = processList.get(minIndex);
				processList.set(minIndex, processList.get(i));
				processList.set(i, temp);
			}
		}
	}

	public static void sortListPID(LinkedList<process> processList) {
		//Sort the list based on arrival time for later for later
		for (int i = 0; i < processList.size(); i++) {
			int minIndex = i;
			for (int j = i + 1; j < processList.size(); j++) {
				if (processList.get(j).getPID() < processList.get(minIndex).getPID()) {
					minIndex = j;
				}

				process temp = processList.get(minIndex);
				processList.set(minIndex, processList.get(i));
				processList.set(i, temp);
			}
		}
	}

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
	public static void printSummary(ArrayList<process> processList, int CPUused, int IOused) {

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

		System.out.println("Summary data: ");

		int finishTime = 0;
		float totalTurn = 0;
		float totalWait = 0;
		for (int i = 0; i < processList.size(); i++) {
			if (processList.get(i).getFinish() > finishTime) {
				finishTime = processList.get(i).getFinish();
			}
			totalTurn += processList.get(i).getTurnaround();
			totalWait += processList.get(i).getWaiting();
		}

		float avgTurn = totalTurn / processList.size();
		float avgWait = totalWait / processList.size();
		float through = (float)processList.size() / finishTime;

		System.out.printf("Finishing time: %d\n", finishTime);
		System.out.printf("CPU Utilization: %f\n", (float)CPUused / finishTime);
		System.out.printf("IO Utilization: %f\n", (float)IOused / finishTime);
		System.out.printf("Throughput: %f processes per hundred cycles\n", through * 100);
		System.out.printf("Average Turnaround time: %f\n", avgTurn);
		System.out.printf("Average Waiting Time: %f\n", avgWait);
	}
}