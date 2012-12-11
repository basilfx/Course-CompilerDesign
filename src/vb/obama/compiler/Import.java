package vb.obama.compiler;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.*;

/**
 * Describes an package/class import and provides some handy functions to check
 * @version 1.0
 */
public class Import {
	/**
	 * Parts of this import, for example [java, lang, String]
	 */
	private List<String> parts = null;
	
	/**
	 * Indicate if last part of an package was a *.
	 */
	private boolean wildImport = false;
	
	/**
	 * Construct a new import
	 * 
	 * @requires path != null && path.length() > 0;
	 */
	public Import(String path) {
		checkArgument(!Strings.isNullOrEmpty(path));
		
		// Split in parts
		this.parts = Lists.newArrayList(Splitter.on(".").split(path));
		
		// Remove wild imports
		if (this.parts.get(this.parts.size() - 1).equals("*")) {
			this.parts.remove(this.parts.size() - 1);
			this.wildImport = true;
		}
	}
	
	/**
	 * Check if this import was a wild import
	 * @return true if import was wild import
	 */
	public boolean isWildImport() {
		return this.wildImport;
	}
	
	/**
	 * Check if a import can contain a class. This does not mean that it is true
	 * @param name Name of class
	 * @return True if this package can contain the class, for example if it
	 * was a wild import, or a direct import.
	 */
	public String hasClass(String name) {
		if (this.isWildImport()) {
			try {
				name = this.getPackage() + "." + name;
				Class.forName(name, false, ClassLoader.getSystemClassLoader());
				return name;
			} catch (ClassNotFoundException exception) {
				return null;
			}
		} else {
			if (this.parts.get(this.parts.size() - 1).equals(name)) {
				return this.toString();
			}
		}
		
		return null;
	}
	
	/**
	 * Check if name of class ends with package name
	 * @param identifier Class
	 * @return
	 */
	public boolean importsClass(String identifier) {
		return this.parts.get(this.parts.size() - 1).equals(identifier) && !this.isWildImport();
	}
	
	/**
	 * Check if this import imports everything, e.g. the only part is *
	 * @return
	 */
	public boolean isBaseImport() {
		return this.parts.size() == 0 && this.isWildImport();
	}
	
	/**
	 * Return representation without wild import, but it includes the last
	 * Class if it was an direct import
	 * 
	 * @return String representation of this import
	 */
	public String getPackage() {
		return Joiner.on(".").join(this.parts);
	}
	
	/**
	 * Return the exact representation of this import, just like it was entered
	 * @return string representation
	 */
	public String toString() {
		return this.getPackage() + (this.wildImport ? ".*" : "");
	}
}
