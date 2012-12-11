package vb.obama.compiler;

import vb.obama.antlr.tree.TypedNode;

import static com.google.common.base.Preconditions.*;

/**
 * Describes an identifier.
 * @version 1.1
 */
public class IdEntry {
    private int level = -1;
    
    /**
     * Reference to node
     */
    private TypedNode node = null;
    
    /**
     * Get reference of node
     * @return
     */
    public TypedNode getNode() {
    	return this.node;
    }
    
    /**
     * Set reference of node
     * @require node != null
     */
    public void setNode(TypedNode node) {
    	this.node = checkNotNull(node);
    }
    
    /**
     * Get the level of this node
     * @return level of node
     */
    public int getLevel() { 
    	return level;         
    }
    
    /**
     * Set the level of this node
     * @param level
     */
    public void setLevel(int level) { 
    	this.level = level;   
    }   
}
