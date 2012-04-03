package uk.co.jbothma.olt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an instance of swedish word morphology
 * 
 * TODO: right now we're assuming nouns.
 * Might be smarter to support other kinds later.
 * 
 * NM: This is probably totally wrong: I'm not a linguist!
 * 
 * TODO: This is a good case for unit tests
 * 
 * TODO: I'm skipping proper nouns
 * TODO: I'm skipping sammansättningsform (c)
 */
public class NounMorph {
//	public enum Type {
//		PAROLE,	// e.g. NCUSN@IS
//		SUC,	// e.g. NN UTR SIN IND NOM
//		SALDO,	// e.g  nn u sg indef nom <- pos inhs[0] param
//	}

	public enum Genus {
		UTR,
		NEU,
		NONE,
	}
	
	public enum Numerus {
		SIN,
		PLU,
		NONE,
	}
	
	public enum Species {
		DEF,
		IND,
		NONE,
	}
	
	public enum Case {
		NOM,
		GEN,
		NONE,
	}	
	
	private Genus genus;
	private Numerus numerus;
	private Species species;
	private Case _case; // case is a keyword.
	
	NounMorph(String morph) {
		if (morph.matches("NN.+"))
			initializeAsSuc(morph);
		else if (morph.matches("nn.+"))
			initializeAsSaldo(morph);
		else if (morph.matches("NC.+"))
			initializeAsParole(morph);
		else throw new IllegalArgumentException("Morphology " + morph + " is invalid or unsupported");
	}

	/**
	 * TODO: The repetition between these initializeAs* functions
	 * can brobably be abstracted away but this is a start. 
	 */
	private void initializeAsParole(String morph) {
		String patternStr = "NC(0|N|U)(0|S|P)(0|G|N)@(0|D|I)S";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(morph);
		// ugly hack - if this string is not null we'll throw an exception.
		String errorDetail = null;
		if (matcher.matches()) {
			if (matcher.group(1).equals("0"))
				this.genus = Genus.NONE;
			else if (matcher.group(1).equals("N"))
				this.genus = Genus.NEU;
			else if (matcher.group(1).equals("U"))
				this.genus = Genus.UTR;
			else errorDetail = "matching genus";
			
			if (matcher.group(2).equals("0"))
				this.numerus = Numerus.NONE;
			else if (matcher.group(2).equals("S"))
				this.numerus = Numerus.SIN;
			else if (matcher.group(2).equals("P"))
				this.numerus = Numerus.PLU;
			else errorDetail = "matching numerus";
			
			if (matcher.group(3).equals("0"))
				this._case = Case.NONE;
			else if (matcher.group(3).equals("G"))
				this._case = Case.GEN;
			else if (matcher.group(3).equals("N"))
				this._case = Case.NOM; // nom nom yum cheeseburger
			else errorDetail = "matching case";
			
			if (matcher.group(4).equals("0"))
				this.species = Species.NONE;
			else if (matcher.group(4).equals("D"))
				this.species = Species.DEF;
			else if (matcher.group(4).equals("I"))
				this.species = Species.IND;
			else errorDetail = "matching species";
		} else {
			errorDetail = "no match";
		}
		if (errorDetail != null) {
			String message = "Morphology " + morph + " is invalid or unsupported"
					+ " when parsed as Parole tag. " + errorDetail;
			throw new IllegalArgumentException(message);
		}
	}
	
	private void initializeAsSuc(String morph) {
		String patternStr = "NN (NEU|UTR|-) "
						  +    "(SIN|PLU|-) "
						  +    "(DEF|IND|-) "
						  +    "(GEN|NOM|-)";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(morph);
		// ugly hack - if this string is not null we'll throw an exception.
		String errorDetail = null;
		if (matcher.matches()) {
			if (matcher.group(1).equals("-"))
				this.genus = Genus.NONE;
			else if (matcher.group(1).equals("NEU"))
				this.genus = Genus.NEU;
			else if (matcher.group(1).equals("UTR"))
				this.genus = Genus.UTR;
			else errorDetail = "matching genus";
			
			if (matcher.group(2).equals("-"))
				this.numerus = Numerus.NONE;
			else if (matcher.group(2).equals("SIN"))
				this.numerus = Numerus.SIN;
			else if (matcher.group(2).equals("PLU"))
				this.numerus = Numerus.PLU;
			else errorDetail = "matching numerus";
			
			if (matcher.group(3).equals("-"))
				this.species = Species.NONE;
			else if (matcher.group(3).equals("DEF"))
				this.species = Species.DEF;
			else if (matcher.group(3).equals("IND"))
				this.species = Species.IND;
			else errorDetail = "matching species";
			
			if (matcher.group(4).equals("-"))
				this._case = Case.NONE;
			else if (matcher.group(4).equals("GEN"))
				this._case = Case.GEN;
			else if (matcher.group(4).equals("NOM"))
				this._case = Case.NOM; // nom nom yum cheeseburger
			else errorDetail = "matching case";
		} else {
			errorDetail = "no match";
		}
		if (errorDetail != null) {
			String message = "Morphology " + morph + " is invalid or unsupported"
					+ " when parsed as SUC tag. " + errorDetail;
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * nn u sg indef nom
	 */
	private void initializeAsSaldo(String morph) {
		String patternStr = "nn (n|u)? "
						  +    "(sg|pl)? "
						  +    "(indef|def)? "
						  +    "(gen|nom)?";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(morph);
		// ugly hack - if this string is not null we'll throw an exception.
		String errorDetail = null;
		if (matcher.matches()) {
			if (matcher.group(1).equals(""))
				this.genus = Genus.NONE;
			else if (matcher.group(1).equals("n"))
				this.genus = Genus.NEU;
			else if (matcher.group(1).equals("u"))
				this.genus = Genus.UTR;
			else errorDetail = "matching genus";
			
			if (matcher.group(2).equals(""))
				this.numerus = Numerus.NONE;
			else if (matcher.group(2).equals("sg"))
				this.numerus = Numerus.SIN;
			else if (matcher.group(2).equals("pl"))
				this.numerus = Numerus.PLU;
			else errorDetail = "matching numerus";
			
			if (matcher.group(3).equals(""))
				this.species = Species.NONE;
			else if (matcher.group(3).equals("def"))
				this.species = Species.DEF;
			else if (matcher.group(3).equals("indef"))
				this.species = Species.IND;
			else errorDetail = "matching species";
			
			if (matcher.group(4).equals(""))
				this._case = Case.NONE;
			else if (matcher.group(4).equals("gen"))
				this._case = Case.GEN;
			else if (matcher.group(4).equals("nom"))
				this._case = Case.NOM; // nom nom yum cheeseburger
			else errorDetail = "matching case";
		} else {
			errorDetail = "no match";
		}
		if (errorDetail != null) {
			String message = "Morphology " + morph + " is invalid or unsupported"
					+ " when parsed as Saldo morphological analysis. " + errorDetail;
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * http://www.javapractices.com/topic/TopicAction.do?Id=17
	 */
	@Override
	public boolean equals(Object aThat) {
		// check for self-comparison
		if (this == aThat)
			return true;

		// use instanceof instead of getClass here for two reasons
		// 1. if need be, it can match any supertype, and not just one class;
		// 2. it renders an explict check for "that == null" redundant, since
		// it does the check for null already - "null instanceof [type]" always
		// returns false. (See Effective Java by Joshua Bloch.)
		if (!(aThat instanceof NounMorph))
			return false;

		// cast to native object is now safe
		NounMorph that = (NounMorph) aThat;

		// now a proper field-by-field evaluation can be made
		return this.genus.equals(that.getGenus())
				&& this.numerus.equals(that.getNumerus())
				&& this.species.equals(that.getSpecies())
				&& this._case.equals(that.getCase());
	}

	
	public Genus getGenus() {
		return genus;
	}

	public void setGenus(Genus genus) {
		this.genus = genus;
	}

	public Numerus getNumerus() {
		return numerus;
	}

	public void setNumerus(Numerus numerus) {
		this.numerus = numerus;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public Case getCase() {
		return _case;
	}

	public void setCase(Case _case) {
		this._case = _case;
	}
}
