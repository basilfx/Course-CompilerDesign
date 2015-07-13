package vb.obama.antlr.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import vb.obama.antlr.tree.info.Info;

/**
 * Represents a node with a type.
 * @version 1.0
 * @see http://www.antlr.org/wiki/display/ANTLR3/Tree+construction
 */
public class TypedNode extends CommonTree {
	/**
	 * Type of node
	 * @see vb.obama.tree.NodeType
	 */
	private NodeType nodeType;
	
	/**
	 * Node return type
	 */
	private Class<?> returnType;
	
	/**
	 * Info about this node, if any
	 */
	private Info info;
	
	/**
	 * Required constructor, only for TypedErrorNode
	 * @see vb.obama.antlr.TypedErrorNode
	 */
	public TypedNode() { 
		// NOP
	}
	
	/**
     * {@inheritDoc}
     */
	public TypedNode(Token t) { 
		this.token = t;
	}
	
	/**
     * {@inheritDoc}
     */
    public TypedNode(TypedNode node) {
    	super(node); 
    	
    	this.nodeType = node.nodeType;
    	this.returnType = node.returnType;
    	this.info = node.info;
    }
    
    /**
     * {@inheritDoc}
     */
    public Tree dupNode() { 
    	return new TypedNode(this); 
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() { 
    	return token.getText();
    }
    
    /**
     * Set the node type
     * @requires nodeType != null
     * @see vb.obama.tree.NodeType
     */
    public void setNodeType(NodeType nodeType) {
    	this.nodeType = checkNotNull(nodeType);
    }
    
    /**
     * Get the return type of this node
     * @return Type of node
     * @see vb.obama.tree.NodeType
     */
    public NodeType getNodeType() {
    	return this.nodeType;
    }
    
    /**
     * Set the return type of this node
     * @param returnType
     */
    public void setReturnType(Class<?> returnType) {
    	this.returnType = returnType;
    }
    
    /**
     * Return the type of this node
     * @return Instance of Class
     */
    public Class<?> getReturnType() {
    	return this.returnType;
    }
    
    /**
     * Set the info of this node
     * @param info Instance of info
     */
    public void setInfo(Info info) {
    	this.info = info;
    }
    
    /**
     * Return the info associated with this node, if any
     * @return Instance of Info
     */
    public Info getInfo() {
    	return this.info;
    }
}