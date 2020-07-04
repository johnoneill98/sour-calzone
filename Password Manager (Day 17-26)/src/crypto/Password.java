package crypto;

import java.security.SecureRandom;

public class Password {
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	private static final String specials = "`~!@#$%^&*()-_=+[{]}\\|\"';:/?.>,<";

	// Generate a single password of a specific length
	public static String generate(int length) {
		String password = "";
		SecureRandom rand = new SecureRandom();
		int option = 0;

		for (int i = 0; i < length; i++) {
			// Choose a random option
			option = rand.nextInt(3);

			// Append the corresponding character
			switch (option) {
			case 0: // Number
				password += (rand.nextInt(10));
				break;
			case 1: // Letter
				// Lowercase
				if(rand.nextInt(2) == 0)
					password += (alphabet.toLowerCase().charAt(rand.nextInt(alphabet.length())));
				// Uppercase
				else
					password += (alphabet.toUpperCase().charAt(rand.nextInt(alphabet.length())));
				break;
			case 2: // Special
				password += (specials.charAt(rand.nextInt(specials.length())));
			}
		}

		return password;
	}

	// Determine the strength of a given password
	public static int strengthOf(String password) {
		int score = 0;
		int upperLetterCounter = 0;
		int lowerLetterCounter = 0;
		int numberCounter = 0;
		int symbolCounter = 0;
		int middleCounter = 0;
		int consecutiveCounter = 0;
		int sequenceCounter = 0, currSequence = 0;
		char currChar;
		boolean onlyLetters = true, onlyNumbers = true;
		boolean consUpper = false, consLower = false, consNumber = false;

		// Number of characters
		score += (password.length()*4);
		
		// If the string is empty, return 0
		if(password.isEmpty())
			return 0;

		// Check the first character
		if(Character.isUpperCase(password.charAt(0))) {
			onlyNumbers = false;
			upperLetterCounter++;
		}
		else if(Character.isLowerCase(password.charAt(0))) {
			onlyNumbers = false;
			lowerLetterCounter++;
		}
		else if(Character.isDigit(password.charAt(0))) {
			onlyLetters = false;
			numberCounter++;
		}
		else {
			onlyLetters = false;
			onlyNumbers = false;
			symbolCounter++;
		}

		// Check the middle characters, if length>2
		if(password.length() > 2) {
			for(int i = 1; i< password.length()-1; i++) {
				if(Character.isUpperCase(password.charAt(i))) {
					onlyNumbers = false;
					upperLetterCounter++;
				}
				else if(Character.isLowerCase(password.charAt(i))) {
					onlyNumbers = false;
					lowerLetterCounter++;
				}
				else if(Character.isDigit(password.charAt(i))) {
					onlyLetters = false;
					numberCounter++;
					middleCounter++;
				}
				else {
					onlyLetters = false;
					onlyNumbers = false;
					symbolCounter++;
					middleCounter++;
				}
			}
		}

		// Check the last character
		if(Character.isUpperCase(password.charAt(password.length()-1))) {
			onlyNumbers = false;
			upperLetterCounter++;
		}
		else if(Character.isLowerCase(password.charAt(password.length()-1))) {
			onlyNumbers = false;
			lowerLetterCounter++;
		}
		else if(Character.isDigit(password.charAt(password.length()-1))) {
			onlyLetters = false;
			numberCounter++;
		}
		else {
			onlyLetters = false;
			onlyNumbers = false;
			symbolCounter++;
		}
		
		// Check for consecutive patterns
		for(int i = 0; i < password.length(); i++) {
			if(Character.isLowerCase(password.charAt(i))) {
				if(consLower)
					consecutiveCounter++;
				consLower = true;
				consUpper = false;
				consNumber = false;
			}
			else if(Character.isUpperCase(password.charAt(i))) {
				if(consUpper)
					consecutiveCounter++;
				consLower = false;
				consUpper = true;
				consNumber = false;
			}
			else if(Character.isDigit(password.charAt(i))) {
				if(consNumber)
					consecutiveCounter++;
				consLower = false;
				consUpper = false;
				consNumber = true;
			}
			else {
				consLower = false;
				consUpper = false;
				consNumber = false;
			}
		}
		
		// Check for sequences
		for(int i = 0; i < password.length(); i++) {
			currChar = password.charAt(i);
			for(int j = i+1; j < password.length(); j++) {
				if(isSequential(currChar, password.charAt(j))) {
					currChar = password.charAt(j);
					currSequence++;
				}
				else
					break;
			}
			if(currSequence > 2)
				sequenceCounter += currSequence;
			currSequence = 0;
		}
		

		// Apply additions
		if(lowerLetterCounter != 0)
			score += ((password.length()-upperLetterCounter)*2);
		if(upperLetterCounter != 0)
			score += ((password.length()-lowerLetterCounter)*2);
		if(!onlyNumbers)
			score += (numberCounter*4);
		score += (symbolCounter*6);
		score += (middleCounter*2);
		
		// Apply deductions
		if(onlyLetters || onlyNumbers)
			score -= password.length();
		score -= (consecutiveCounter*2);
		score -= (sequenceCounter*3);

		// Put score in range 0-100
		score = (score > 100)? 100 : score;
		score = (score < 0)? 0 : score;
		
		return score;
	}

	// Determine if two characters are sequential
	private static boolean isSequential(char a, char b) {
		if(Character.isDigit(a) && Character.isDigit(b))
			return Character.getNumericValue(a)+1 == Character.getNumericValue(b);
			
		else if (Character.isAlphabetic(a) && Character.isAlphabetic(b))
			return Character.toUpperCase(a)+1 == Character.toUpperCase(b);

		return false;
	}
}
