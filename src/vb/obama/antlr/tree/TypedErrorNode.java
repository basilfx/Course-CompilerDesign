package vb.obama.antlr.tree;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonErrorNode;
import org.apache.log4j.Logger;

import vb.obama.Obama;

/**
 * Represents an typed error node
 * 
 * @version 1.2
 * @see http://www.antlr.org/wiki/display/ANTLR3/Tree+construction
 */
public class TypedErrorNode extends TypedNode {
	/** 
	 * Error delegator
	 */
	private CommonErrorNode delegate;
	
	/**
     * {@inheritDoc}
     */
    public TypedErrorNode(TokenStream input, Token start, Token stop, RecognitionException e) {
    	this.delegate = new CommonErrorNode(input, start, stop, e);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNil() { 
    	return delegate.isNil(); 
    }

    /**
     * {@inheritDoc}
     */
    public int getType() { 
    	return delegate.getType(); 
    }

    /**
     * {@inheritDoc}
     */
    public String getText() { 
    	return delegate.getText(); 
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() { 
    	return delegate.toString(); 
    }
}
