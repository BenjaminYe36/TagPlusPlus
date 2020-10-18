import java.io.*;
import java.util.*;

// Provides Helper methods for the ui interface
// All fields and methods should be static

public class TagManagement {
	// Stores the tag for a single file
	private static Set<String> tagSet;
	// Stores the mapping from tags to file paths
	private static Map<String, Set<String>> tagMap;
	// The filename used to store the tagMap in the same folder
	public static final String MAP_PATH = "tagMap.TagMapData";
	// The suffix of tag data files saved
	public static final String DATA_SUFFIX = ".TagSetData";

	// Loads the tagMap if the file in the same directory exists
	public static void init() {
		ObjectInputStream ois;
		if (new File(MAP_PATH).exists()) {
			try {
				ois = new ObjectInputStream(new FileInputStream(MAP_PATH));
				tagMap = (HashMap<String, Set<String>>) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			tagMap = new HashMap<>();
		}
	}
	
	public static Map<String, Set<String>> getMap() {
		return tagMap;
	}
	
	public static Set<String> getSet() {
		return tagSet;
	}

	public static Set<String> loadTags(File file) {
		// ! ! ! the file name for data files should add the suffix DATA_SUFFIX ! ! !
		// file is normal file
		// TODO 1.if the data file exists, load the contents through object input stream into tagSet
		// 2.if not exists return an empty set(using tagSet, initialize it to an empty HashSet) 
		// related tutorial: https://stackoverflow.com/questions/7673424/how-to-dump-a-hashset-into-a-file-in-java
		// similar to init() above

		return tagSet;
	}

	public static void saveTags(File file) {
		// TODO 1.saves tagSet to the absolute path of the file + DATA_SUFFIX, using object output stream
		// 2.if the absolute path of the file + DATA_SUFFIX is not hidden, make it a hidden file (how to make a file hidden shown below)
/* File f = new File("Your file path here");
   String sets = "attrib +H \"" + f.getAbsolutePath() + "\"";
	try {
		Runtime.getRuntime().exec(sets);
	} catch (IOException e) {
		e.printStackTrace();
	}*/
		// 3.saves the TagMap to MAP_PATH in the same directory, using object output stream
		// related tutorial: https://stackoverflow.com/questions/7673424/how-to-dump-a-hashset-into-a-file-in-java
		
	}
	
	public static void add(String tag, File file) {
		// TODO 1.add the tag to the tagSet
		// 2.add a new mapping to the tagMap (tag->absolute file path HashSet)
		//    -if not exists tag(key), then initialize an empty HashSet and put it in
		//    -Then, get the set with the tag(key) and add the absolute file path to the set
		// 3.call saveTags(file) in the end
		
	}
	
	public static void remove(String tag, File file) {
		// TODO 1.remove the tag in the tagSet
		// 2.remove the mapping from the given tag to the given file's absolute path in tagMap
		// 3.call saveTags(file) in the end
		
	}
	
	// Searches for a specific tag, if not exists then returns an empty set, else returns the matching set
	public static Set<String> get(String tag) {
		if (tagMap.containsKey(tag)) {
			return tagMap.get(tag);
		}
		return new HashSet<String>();
	}
}
