package vb.obama.antlr.tree.info;

import static com.google.common.base.Preconditions.*;

/**
 * Typed node for a parameter
 * @version 1.0
 */
public class ParameterInfo extends Info {
	/**
	 * Name of this parameter
	 */
	public String name;
	
	/**
	 * Name of the keyword
	 */
	public String keyword;
	
	/**
	 * Index of the parameters. Used during codegen.
	 */
	public int index;
	
	/**
	 * 
	 * @param name
	 * @param keyword
	 * @param index
	 * @require name != null
	 * @require keyword != null
	 * @require index != null
	 */
	public ParameterInfo(String name, String keyword, int index) {
		super();
		this.name = checkNotNull(name);
		this.keyword = checkNotNull(keyword);
		this.index = checkNotNull(index);
	}
	
	/**
	 * Returns the keyword of this node
	 * 
	 * @return the keyword of this node
	 * @ensure result != null
	 */
	public String getKeyword() {
		return this.keyword;
	}
	
	/**
	 * Sets the keyword of this node
	 * 
	 * @require keyword != null
	 * @ensure getKeyword() == keyword
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * Returns the name of this node
	 * 
	 * @return the name of this node
	 * @ensure result != null
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this node
	 * @require name != null
	 */
	public void setName(String name) {
		this.name = name;
	}
}
