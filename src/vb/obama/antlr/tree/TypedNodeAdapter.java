package vb.obama.antlr.tree;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;

/**
 * Adapter to create typed nodes.
 * @version 1.0
 * @see http://www.antlr.org/wiki/display/ANTLR3/Tree+construction
 */
public class TypedNodeAdapter extends CommonTreeAdaptor {
	/**
     * {@inheritDoc}
     */
    public Object create(Token token) {
    	//logger.debug(String.format("New node created"));
        return new TypedNode(token);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object dupNode(Object t) {
    	if ( t==null ) return null;

		return ((TypedNode)t).dupNode();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object errorNode(TokenStream input, Token start, Token stop, RecognitionException e) {
    	TypedErrorNode node = new TypedErrorNode(input, start, stop, e);
        return node;
    }
}
