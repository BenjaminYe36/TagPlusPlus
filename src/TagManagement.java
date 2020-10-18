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
				ois.close();
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
		// TODO 1.if the data file exists, load the contents through object input stream
		// into tagSet
		// 2.if not exists return an empty set(using tagSet, initialize it to an empty
		// HashSet)
		// related tutorial:
		// https://stackoverflow.com/questions/7673424/how-to-dump-a-hashset-into-a-file-in-java
		// similar to init() above

		// Implemented by Haoyiwen Guo

//		if(file.exists()) {
//            ObjectInputStream ois  = null;
//            try {
//                ois = new ObjectInputStream(new FileInputStream(file));
//                tagSet = (Set<String>) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            tagSet = null;
//        }
//        return tagSet;

		File fData = new File(file.getAbsolutePath() + DATA_SUFFIX);
		if (fData.exists()) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(fData.getAbsolutePath()));
				tagSet = (Set<String>) ois.readObject();
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			tagSet = new HashSet<>();
		}
		return tagSet;
	}

	public static void saveTags(File file) {
		// TODO 1.saves tagSet to the absolute path of the file + DATA_SUFFIX, using
		// object output stream
		// 2.if the absolute path of the file + DATA_SUFFIX is not hidden, make it a
		// hidden file (how to make a file hidden shown below)
		/*
		 * File f = new File("Your file path here"); String sets = "attrib +H \"" +
		 * f.getAbsolutePath() + "\""; try { Runtime.getRuntime().exec(sets); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
		// 3.saves the TagMap to MAP_PATH in the same directory, using object output
		// stream
		// related tutorial:
		// https://stackoverflow.com/questions/7673424/how-to-dump-a-hashset-into-a-file-in-java

		// Implemented my Qiu Lin
		File fData = new File(file.getAbsolutePath() + DATA_SUFFIX);

//		if (!fData.isHidden()) {
//			File f = new File(file.getAbsolutePath());
//			String sets = "attrib +H \"" + f.getAbsolutePath() + "\"";
//			try {
//				Runtime.getRuntime().exec(sets);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		if (fData.isHidden()) {
//			System.out.println("hidden");
			String sets = "attrib -H " + fData.getAbsolutePath();
			try {
				Runtime.getRuntime().exec(sets);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while(fData.isHidden()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ObjectOutputStream oosSet = null;
		try {
			oosSet = new ObjectOutputStream(new FileOutputStream(fData.getAbsolutePath())); // change to fData
			oosSet.writeObject(tagSet);
//			oosSet.flush();
			oosSet.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String sets = "attrib +H \"" + fData.getAbsolutePath() + "\"";
		try {
			Runtime.getRuntime().exec(sets);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ObjectOutputStream oosMap = null;
		try {
			oosMap = new ObjectOutputStream(new FileOutputStream(MAP_PATH));
			oosMap.writeObject(tagMap);
//			oosMap.flush();
			oosMap.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void add(String tag, File file) {
		// TODO 1.add the tag to the tagSet
		// 2.add a new mapping to the tagMap (tag->absolute file path HashSet)
		// -if not exists tag(key), then initialize an empty HashSet and put it in
		// -Then, get the set with the tag(key) and add the absolute file path to the
		// set
		// 3.call saveTags(file) in the end

		// Implemented by Haoyiwen Guo

		tagSet.add(tag);

		if (!tagMap.containsKey(tag)) {
			tagMap.put(tag, new HashSet<>());
		}
		tagMap.get(tag).add(file.getAbsolutePath());

//      HashSet<String> set = new HashSet<>();
//      set.add(file.getAbsolutePath());
//      tagMap.put(tag, set);

		saveTags(file);
	}

	public static void remove(String tag, File file) {
		// TODO 1.remove the tag in the tagSet
		// 2.remove the mapping from the given tag to the given file's absolute path in
		// tagMap
		// 3.call saveTags(file) in the end

		// Alternative by Qui Lin
//        Iterator<String> itr = tagSet.iterator();
//        while(itr.hasNext()) {
//            if(tag.equals(itr.next())) {
//                itr.remove();
//            }
//        }
		tagSet.remove(tag);
//        Iterator<Map.Entry<String, Set<String>>> itr2 = tagMap.entrySet().iterator();
//        while(itr.hasNext()) {
//            Map.Entry<String, Set<String>> entry = itr2.next();
//            if(entry.getKey().equals(tag)) {
//                itr2.remove();
//            }
//        }
		if (tagMap.containsKey(tag)) {
			tagMap.get(tag).remove(file.getAbsolutePath());
		}
//        try {
//            saveTags(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
		saveTags(file);

	}

	// Searches for a specific tag, if not exists then returns an empty set, else
	// returns the matching set
	public static Set<String> get(String tag) {
		if (tagMap.containsKey(tag)) {
			return tagMap.get(tag);
		}
		return new HashSet<String>();
	}
}
