package model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

	public static boolean valEmpty(String[] args) {
		for (Object o : args) {
			if (o.equals("")) {
				return true;
			}
		}
		return false;
	}

	public static boolean valEmail(String string) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
		Matcher mat = pattern.matcher(string);

		return mat.matches();

	}

	public static boolean valRole(String role) {
		if(role.equalsIgnoreCase("ADMIN")) {
			return true;
		}else if(role.equalsIgnoreCase("USER")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean valName(String name) {
		return !name.matches(".*\\d.*");
	}
	
	public static boolean valName(String[] args) {
		boolean ind = true;
		for(String s : args) {
			ind = ind && valName(s);
		}
		return ind;
	}
	
	public static boolean valNumber(String[] args) {
		boolean ind = true;
		for(String s : args) {
			ind = ind && valNumber(s);
		}
		return ind;
	}
	
	public static boolean valPositive(String[] args) {
		boolean ind = true;
		for(String s : args) {
			ind = ind && valPositive(s);
		}
		return ind;
	}
	
	public static boolean valPositive(int i) {
		return i<0;
	}
	
	public static boolean valPositive(List<Integer> args) {
		boolean ind = true;
		for(Integer i : args) {
			ind = ind && valPositive(i);
		}
		return ind;
	}
	
	public static boolean valPositive(String arg) {
		try {
			Integer i = Integer.parseInt(arg);
			if(i > 0)
				return true;
			else
				return false;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean valNumber(String arg) {
		try {
			Integer.parseInt(arg);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean valType(String type) {
		if(type.equals("SSD")) {
			return true;
		}else if(type.equals("HDD")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String[] a = {"-1", "-1", "-110", "-10"};
		System.out.println(valPositive(a));
		
		
	}
	

}
