package utilities;

public class InputValidation {
	// Determine if a string is a number with some constraints
	public static boolean isNumber(String s, boolean isNegative, boolean isDecimal) {
		boolean foundDecimalPoint = false;

		// If the string is empty, return false
		if(s.isEmpty())
			return false;

		// If the string should be negative, check for the minus sign
		else if(isNegative && s.charAt(0) != '-')
			return false;

		// Traverse through the string
		for(int i=0;i<s.length();i++) {
			// Check for a decimal point
			if (s.charAt(i) == '.') {
				if(foundDecimalPoint)
					return false;
				else
					foundDecimalPoint=true;
			}

			// Check if the current character is a number
			if (!isNumber(s.charAt(i)))
				return false;
		}

		return true;
	}

	// Determine if a character is a number
	public static boolean isNumber(char c) {
		return c=='0' || c=='1' || c=='2' || c=='3' || c=='4' || c=='5' || c=='6' || c=='7' || c=='8' || c=='9';
	}
}
