package org.tanjakostic.jcleancim.common;

/**
 * EAProjType defines an enum for all types of EA Project files. 
 *
 * @author todd.viegut@gmail.com
 * @version $Id: EAProjType.java 21 2024-09-11 15:44:50Z dev978 $
 */
public enum EAProjType {
	EAP, EAPX, FEAP, QEA, QEAX;

	public static EAProjType toEAProjType(String extension) {
		extension = (extension != null ? extension.toUpperCase() : extension);
		EAProjType type;
		try {
			type = EAProjType.valueOf(extension);
		} catch (Exception e) {
			type = null;
		}
		return type;
	}

}
