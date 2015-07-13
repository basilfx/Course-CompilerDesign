package vb.obama.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import vb.obama.exceptions.SymbolTableException;

import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.*;

/**
 * Holds the information about the identifiers for a program.
 * @version 1.1
 */
public class SymbolTable {
	private static final Logger logger = Logger.getLogger(SymbolTable.class.getName());
	
	/**
	 * Current identifier level
	 */
	private int level;
	
	/**
	 * Holds the identifier for a specific level
	 */
	private Map<String, ArrayList<IdEntry>> table;
	
    /** 
     * Construct a new symbol table
     * @ensures  this.getCurrentLevel() == -1 
     */
    public SymbolTable() { 
        this.level = -1;
        this.table = Maps.newHashMap();
    }

    /** 
     * Opens a new scope. 
     * @ensures this.getCurrentLevel() == old.getCurrentLevel() + 1;
     */
    public void openScope()  {
        this.level++;
    }

    /** 
     * Closes the current scope. All identifiers in the current scope will be 
     * removed from the SymbolTable.
     * 
     * @requires this.getCurrentLevel() > -1;
     * @ensures this.getCurrentLevel() == old.getCurrentLevel()-1;
     */
    public void closeScope() {
    	this.level--;
    	
    	for (ArrayList<IdEntry> value : this.table.values()) {
    		Iterator<IdEntry> iterator = value.iterator();
    		
    		while (iterator.hasNext()) {
    			IdEntry entry = iterator.next();
    			
    			if (entry.getLevel() > this.level) 
    				iterator.remove();
    		}
    	}
    }

    /** 
     * Returns the current scope level.
     */
    public int getCurrentLevel() {
        return level;
    }    

    /** 
     * Enters an id together with an entry into this SymbolTable using the 
     * current scope level. The entry's level is set to currentLevel().
     * 
     * @requires id != null && id.length() > 0 && entry != null;
     * @ensures this.retrieve(id).getLevel() == currentLevel();
     * @throws SymbolTableException when there is no valid current scope level,
     * or when the id is already declared on the current level. 
     */
    public void enter(String id, IdEntry entry) throws SymbolTableException {
    	checkNotNull(id);
    	checkArgument(id.length() > 0);
    	checkNotNull(entry);
    	
    	if (this.level == -1) throw new SymbolTableException(String.format("Invalid scope level %d", this.level));
    	entry.setLevel(this.level);
    	
    	if (!this.table.containsKey(id)) {
    		this.table.put(id, new ArrayList<IdEntry>());
    	}
    	
    	for (IdEntry e : this.table.get(id)) {
    		if(e.getLevel() == entry.getLevel()) {
    			throw new SymbolTableException(String.format("Duplicate identifier '%s' on level %d", id, this.level));
    		}
    	}
    	
    	this.table.get(id).add(entry);
    }

    /** 
     * Get the Entry corresponding with id whose level is the highest; in other 
     * words, that is defined last.
     * 
     * @return  Entry of this id on the highest level null if this SymbolTable 
     * does not contain id.
     * @requires id != null 
     */
    public IdEntry retrieve(String id) {
    	checkNotNull(id);
    	
    	// Check if identifier exists
    	if (table.get(id) == null) {
    		return null;
    	}
    	
    	// Try to fetch the last addition
    	try {
    		return table.get(id).get(table.get(id).size() - 1);
    	} catch (IndexOutOfBoundsException e) {
    		return null;
    	}
    }
}
