package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author sam
 * An ini parser which can specify sections including properties. 
 */

public class IniParser {

	List<String> section;
	List<String> property;
	List<String> content ;
	private HashMap<String, String> secMapping;
	//private HashMap<String, String> contentMapping;
	
	public IniParser() {
		section = new ArrayList<>();
		property = new ArrayList<>();
		content = new ArrayList<>();
		secMapping = new HashMap<String, String> ();
		//contentMapping = new HashMap<String, String> ();
	}

	//property -> section
	public void mapping(String key,String value) {
		secMapping.put(key,value);
	}
	
	public String findSection(String property) {
		return secMapping.get(property) ;
	}
	
	public int findIndex() {
		return section.indexOf("asdas");
	}
	
	public void parse(String line) {
		line = line.trim();
		if(line.matches("\\[.*\\]")) {
			line = line.replaceAll("\\[|\\]", "");
			section.add(line);
		}
		else if(line.matches("^[^;].*=.*")) {
//			if(line.matches("\\\\$")) {
//				line = line.replaceAll("\\\\", "");
//			}
			if(line.charAt(line.length()-1) == '\\') {
				line = line.replaceAll("\\\\", "");
				line = line.trim();
			}
			String[] prop = line.split("=");
			property.add(prop[0]);
			content.add(prop[1]);
			mapping(Integer.toString(property.size()-1),section.get(section.size()-1));
		}
		else if(line.matches("^[^;].+")) {
			if(line.charAt(line.length()-1) == '\\') {
				line = line.replaceAll("\\\\", "");
			}
			content.set(content.size()-1, content.get(content.size()-1) +" "+line);
		}
			
	}

}
