import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) {

		/*
		 * THIS CODE SECTION JUST READS DATE FROM FILE
		 */
		ArrayList<String> toSort = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
			String line = reader.readLine();
			while ((line != null && !line.isEmpty())) {
				toSort.add(line);
				line = reader.readLine();
			}
		} catch(IOException e) {
			System.out.println("no appropriate file with name \"input.txt\" found");
		}

		/*
		 * THIS IS THE METHOD OF INTEREST
		 */
		randomize(toSort);


		/*
		 * THIS CODE SECTION JUST READS DATE FROM FILE
		 */
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
			for (String line : toSort) {
				writer.write(line + "\n");
			}
			writer.close();
		} catch(IOException e) {
			System.out.println("IOException");
		}
	}

	/**MAIN RANDOMIZER METHOD:*/
	public static void randomize(List<String> toSort) {

		//organize strings of format (NAME,COMPANY)
		//into a hashmap with both and an array list with just companies
		HashMap<String, ArrayList<String>> organizedData = new HashMap<>();
		ArrayList<String> onlyCompanies = new ArrayList<>();

		for(String s : toSort) {
			String[] fullInfo = s.split(",");
			String name = fullInfo[0];
			String company = fullInfo[1];
			onlyCompanies.add(company);
			organizedData.putIfAbsent(company, new ArrayList<>());
			organizedData.get(company).add(name);
		}

		//calls randomizeHelper to put order of companies in random order
		//so long as no duplicates are adjacent
		onlyCompanies = new ArrayList<>(List.of(randomizeHelper(onlyCompanies)));

		//refills the original array list
		toSort.clear();

		//goes through all companies
		for (String company : onlyCompanies) {
			int rand = new Random().nextInt(organizedData.get(company).size());
			//gets a RANDOM name of a person in the current company
			String name = organizedData.get(company).remove(rand);
			toSort.add(
					//combines name and company to put into the list
					name + "," + company
			);
		}
	}

	/**Helper method - randomizes ONLY companies (with duplicates)
	 * so that the duplicates are never adjacent*/
	public static String[] randomizeHelper(List<String> arr)  {
		//the hashmap helps to keep track
		//of the frequency of each company name
		Map<String, Integer> map = new HashMap<>();
		for (String s : arr) {
			map.put(s, map.getOrDefault(s, 0) + 1);
		}

		//this is done to see if the first element CAN be anything
		//but the most frequent one
		int max = map.values().stream().max(Integer::compare).orElse(0);
		int total = map.values().stream().reduce(Integer::sum).orElse(0);

		//priority queue that weighs the frequency of each company
		PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> t, Map.Entry<String, Integer> t1) {
				int val = t1.getValue() - t.getValue();
				if (val == 0) {
					int rand = new Random().nextInt(2);
					val = (rand == 0) ? -1 : 1;
				}
				return val;
			}
		});

		//fields needed to build the return value
		String[] result = new String[arr.size()];
		int i = 0;
		int floor = 0;

		//if we CAN use anything for our first company on the list - we do it
		if (max <= (total/2)) {
			result[0] = arr.get(new Random().nextInt(arr.size()));
			map.put(result[0],(map.get(result[0])-1));
			if (map.get(result[0]) == 0) {
				map.remove(result[0]);
			}
			i++;
			floor++;
		}

		pq.addAll(map.entrySet());

		//start combining
		while (!pq.isEmpty()) {
			List<Map.Entry<String, Integer>> temp = new ArrayList<>();
			//k = 2 ensures that no 2 adjecent elements are the same
			int k = 2;
			while (k > 0 && !pq.isEmpty()) {
				//grab an entry from the priority queue and check if
				//it can go into the final array. if it cannot go
				//that means that the highest priority company is already on the list,
				//and it's impossible to add anything else without duplicating,
				//so the program just stops
				Map.Entry<String, Integer> entry = pq.poll();
				if (i > floor && result[i - 1].equals(entry.getKey())) {
					System.out.println("ERROR: It's impossible to rearrange the array such that no two adjacent elements are identical.");
					System.exit(1);
				}
				result[i++] = entry.getKey();
				if (entry.getValue() > 1) {
					entry.setValue(entry.getValue() - 1);
					temp.add(entry);
				}
				k--;
			}
			pq.addAll(temp);
		}

		return result;
	}

}